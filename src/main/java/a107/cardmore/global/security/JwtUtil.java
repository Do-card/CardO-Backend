package a107.cardmore.global.security;

import a107.cardmore.domain.auth.dto.DecodedJwtToken;
import a107.cardmore.domain.redis.BlacklistTokenRedisRepository;
import a107.cardmore.domain.redis.RefreshTokenRedisRepository;
import a107.cardmore.domain.user.entity.User;
import a107.cardmore.global.exception.BadRequestException;
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

    // TODO: Exception 처리 및 세분화
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final BlacklistTokenRedisRepository blacklistTokenRedisRepository;

    @Value("${jwt.secret-key}") private String secretKey;
    @Value("${jwt.access-token-exp}") private long accessTokenExp;
    @Value("${jwt.refresh-token-exp}") private long refreshTokenExp;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public Jws<Claims> getClaim(String token){
        validateToken(token);
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token);
    }

    private void validateToken(String token){
        boolean isBlacked = blacklistTokenRedisRepository.hasKey(token);

        if (isBlacked) {
            throw new BadRequestException("유효하지 않은 토큰입니다.");
        }
    }
    public String generateAccessToken(User user){
        return issueToken(user.getId(), user.getRole(), ACCESS_TOKEN, accessTokenExp);
    }

    public String generateRefreshToken(User user){
        return issueToken(user.getId(), user.getRole(), REFRESH_TOKEN, refreshTokenExp);
    }

    public void saveRefreshToken(String accessToken, String refreshToken){
        refreshTokenRedisRepository.save(accessToken, refreshToken);
    }

    public void renewRefreshToken(String oldAccessToken, String newAccessToken, String newRefreshToken){
        refreshTokenRedisRepository.save(newAccessToken, newRefreshToken);
        expireToken(oldAccessToken);
    }

    private String issueToken(Long userId, String role, String type, Long time) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + time * 1000);
        return Jwts.builder()
                .issuer("Card-O! Inc.")
                .signWith(getSecretKey())
                .subject(userId.toString())
                .claim("type", type)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiration)
                .compact();
    }

    public void expireToken(String accessToken) {
        blacklistTokenRedisRepository.save(accessToken, getRemainingTime(accessToken));
        refreshTokenRedisRepository.delete(accessToken);
        log.debug("Token added to blacklist: {}", accessToken);
    }

    private long getRemainingTime(String token) {
        Date expiration = getClaim(token).getPayload().getExpiration();
        Date now = new Date();

        return Math.max(0, expiration.getTime() - now.getTime());
    }

    public DecodedJwtToken decodeToken(String token, String type) {
        Claims claims = getClaim(token).getPayload();
        checkType(claims, type);

        return new DecodedJwtToken(
                Long.valueOf(claims.getSubject()),
                String.valueOf(claims.get("role")),
                String.valueOf(claims.get("type"))
        );
    }

    private void checkType(Claims claims, String type) {
        if (!type.equals(String.valueOf(claims.get("type")))) {
            throw new BadRequestException("유효하지 않은 토큰입니다.");
        }
    }

    public String findRefreshTokenByAccessToken(String accessToken) {
        return refreshTokenRedisRepository.findById(accessToken)
                .orElseThrow(() -> new BadRequestException("유효하지 않은 토큰입니다."));
    }

}
