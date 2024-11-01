package a107.cardmore.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidTokenException extends AuthenticationException {
    public InvalidTokenException() {
        super("유효하지 않은 토큰입니다.");
    }
}
