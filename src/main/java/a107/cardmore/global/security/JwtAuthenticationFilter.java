package a107.cardmore.global.security;

import a107.cardmore.domain.auth.dto.DecodedJwtToken;
import a107.cardmore.global.exception.InvalidTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static a107.cardmore.util.constant.RedisPrefix.ACCESS_TOKEN;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = resolveToken(request);
            if (Objects.nonNull(token)) {
                Authentication authentication = getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication); //SecurityContextHolder에 담기
            }
            filterChain.doFilter(request, response);
        } catch (AuthenticationException e){
            handleAuthenticatedException(response);
        }
    }

    private Authentication getAuthentication(String token) {
        DecodedJwtToken decodedJwtToken = jwtUtil.decodeToken(token, ACCESS_TOKEN);

        String email = decodedJwtToken.getEmail();
        String role = decodedJwtToken.getRole();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));

        return new UsernamePasswordAuthenticationToken(email, null, authorities);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken == null){
            return null;
        }

        if (bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new InvalidTokenException();
    }

    private void handleAuthenticatedException(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"유효하지 않은 토큰입니다.\"}");
    }
}
