package a107.cardmore.domain.discount.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountDetailDto {
    private String cardId;
    private String cardName;
    private String merchantCategory;
    private String colorTitle;
    private String colorBackground;
    private Long price;
}
