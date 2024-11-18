package a107.cardmore.domain.card.dto;

import a107.cardmore.util.api.dto.card.CardBenefitsInfo;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardBenefitResponseDto {
    private String categoryId;
    private String merchantCategory;
    private Double discountRate;

    public CardBenefitResponseDto(CardBenefitsInfo benefitsInfo){
        this.categoryId = benefitsInfo.getCategoryId();
        this.merchantCategory = benefitsInfo.getCategoryName();
        this.discountRate = benefitsInfo.getDiscountRate();
    }
}
