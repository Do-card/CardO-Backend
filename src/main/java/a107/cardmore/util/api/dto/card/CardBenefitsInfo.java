package a107.cardmore.util.api.dto.card;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardBenefitsInfo {
    private String categoryId;
    private String categoryName;
    private Double discountRate;
}
