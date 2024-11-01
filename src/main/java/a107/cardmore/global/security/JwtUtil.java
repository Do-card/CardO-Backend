package a107.cardmore.global.security;

import a107.cardmore.domain.auth.dto.DecodedJwtToken;
import a107.cardmore.domain.redis.BlacklistTokenRedisRepository;
import a107.cardmore.domain.redis.RefreshTokenRedisRepository;
import a107.cardmore.domain.user.entity.User;
import a107.cardmore.global.exception.BadRequestException;
import a107.cardmore.global.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

import static a107.cardmore.util.constant.RedisPrefix.ACCESS_TOKEN;
import static a107.cardmore.util.constant.RedisPrefix.REFRESH_TOKEN;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final BlacklistTokenRedisRepository blacklistTokenRedisRepository;

    @Value("${jwt.secret-key}") private String secretKey;
    @Value("${jwt.access-token-exp}") private long accessTokenExp;
    @Value("${jwt.refresh-token-exp}") private long refreshTokenExp;

    public Jws<Claims> getClaim(String token){
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token);
    }

    public boolean isTokenExpired(String token){
        Date expiration = getClaim(token).getPayload().getExpiration();
        return expiration.before(new Date());
    }

    public void validateRefreshToken(String refreshToken){
        Boolean isAvailable = refreshTokenRedisRepository.hasKey(refreshToken);
        Boolean isBlacked = blacklistTokenRedisRepository.hasKey(refreshToken);

        if (!isAvailable || isBlacked) {
            throw new BadRequestException("유효하지 않은 토큰입니다.");
        }
    }

    public String generateAccessToken(User user){
        return issueToken(user.getEmail(), user.getRole(), ACCESS_TOKEN, accessTokenExp);
    }

    public String generateRefreshToken(User user){
        return issueToken(user.getEmail(), user.getRole(), REFRESH_TOKEN, refreshTokenExp);
    }

    public void saveRefreshToken(String accessToken, String refreshToken){
        refreshTokenRedisRepository.save(refreshToken, accessToken);
    }

    public void saveRefreshToken(String oldRefreshToken, String newAccessToken, String newRefreshToken){
        saveRefreshToken(newAccessToken, newRefreshToken);
        expireToken(oldRefreshToken);
    }

    private String issueToken(String email, String role, String type, Long time) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + time * 1000);
        return Jwts.builder()
                .issuer("Card-O! Inc.")
                .signWith(getSecretKey())
                .subject(email)
                .claim("type", type)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiration)
                .compact();
    }

    public void expireToken(String refreshToken) {
        if (!refreshTokenRedisRepository.hasKey(refreshToken)){
            log.debug("존재하지 않는 토큰입니다.");
            return;
        }

        blacklistTokenRedisRepository.save(refreshToken, getRemainingTime(refreshToken));
        refreshTokenRedisRepository.delete(refreshToken);
        log.debug("블랙리스트에 등록되었습니다.: {}", refreshToken);
    }

    private long getRemainingTime(String token) {
        Date expiration = getClaim(token).getPayload().getExpiration();
        Date now = new Date();

        return Math.max(0, expiration.getTime() - now.getTime());
    }

    public DecodedJwtToken decodeToken(String token, String type) {
        try {
            Claims claims = getClaim(token).getPayload();

            checkType(claims, type);
            return new DecodedJwtToken(
                    String.valueOf(claims.getSubject()),
                    String.valueOf(claims.get("role")),
                    String.valueOf(claims.get("type"))
            );
        } catch (Exception e) {
            throw new InvalidTokenException();
        }
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    private void checkType(Claims claims, String type) {
        String claimType = String.valueOf(claims.get("type"));
        if (!type.equals(claimType)) {
            throw new InvalidTokenException();
        }
    }

}
