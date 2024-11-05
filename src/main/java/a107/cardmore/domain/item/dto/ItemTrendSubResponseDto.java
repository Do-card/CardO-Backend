package a107.cardmore.domain.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ItemTrendSubResponseDto {
    private Long rank;
    private String category;
    private Long count;
}
