package a107.cardmore.domain.marker.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MarkerNameUpdateRequestDto {
    private String name;
}
