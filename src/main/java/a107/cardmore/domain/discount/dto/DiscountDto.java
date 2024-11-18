package a107.cardmore.domain.discount.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiscountDto {
    private List<String> categoryNames;
    private List<String> cardNames;
    private List<DiscountDetailDto> discountInfos;
}
