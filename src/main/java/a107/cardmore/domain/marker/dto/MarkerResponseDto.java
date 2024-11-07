package a107.cardmore.domain.marker.dto;

import a107.cardmore.domain.item.dto.ItemResponseDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MarkerResponseDto {
    private Long id;
    private String name;
    private String poiId;
    private String poiName;
    private Double latitude;
    private Double longitude;
    private String colorBackground;
    private Boolean isFavorite;
    private Boolean isComplete;
    private LocalDateTime created;
    private LocalDateTime updated;
    private List<ItemResponseDto> items;
}
