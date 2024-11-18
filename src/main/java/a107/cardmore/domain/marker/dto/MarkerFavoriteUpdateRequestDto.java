package a107.cardmore.domain.marker.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MarkerFavoriteUpdateRequestDto {
    private Boolean isFavorite;
}
