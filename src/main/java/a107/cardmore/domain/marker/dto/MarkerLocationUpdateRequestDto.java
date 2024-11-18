package a107.cardmore.domain.marker.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MarkerLocationUpdateRequestDto {
    private String poiId;
    private String poiName;
    private Double latitude;
    private Double longitude;
}
