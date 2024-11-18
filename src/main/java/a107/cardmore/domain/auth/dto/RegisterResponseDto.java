package a107.cardmore.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RegisterResponseDto {
    private String email;
}
