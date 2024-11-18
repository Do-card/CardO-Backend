package a107.cardmore.domain.user.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PositionRequestDto {
    double longitude;
    double latitude;
}
