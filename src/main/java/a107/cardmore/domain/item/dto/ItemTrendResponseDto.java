package a107.cardmore.domain.item.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ItemTrendResponseDto {
    private List<ItemTrendSubResponseDto> userTrend = new ArrayList<>();
    private List<ItemTrendSubResponseDto> mainTrend = new ArrayList<>();

}
