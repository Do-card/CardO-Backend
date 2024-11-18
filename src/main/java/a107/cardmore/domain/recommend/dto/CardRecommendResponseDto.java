package a107.cardmore.domain.recommend.dto;

import a107.cardmore.util.api.dto.card.CardProductResponseRestTemplateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CardRecommendResponseDto {
    CardProductResponseRestTemplateDto card;
    int originalDiscountMoney;  //현재 할인 받고 있는 금액
    int discountMoney;  //카드 바꿀 시 할인 받을 수 있는 금액
    String colorBackground;
    String colorTitle;
}
