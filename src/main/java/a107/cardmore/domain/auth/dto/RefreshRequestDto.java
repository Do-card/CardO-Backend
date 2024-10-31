package a107.cardmore.domain.auth.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RefreshRequestDto {
    String refreshToken;
}
