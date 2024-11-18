package a107.cardmore.util.api.dto.card;

import a107.cardmore.domain.card.dto.CardResponseDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
//import a107.cardmore.util.api.dto.card.CardBenefitInfo;

import java.util.List;

@Getter
@Setter
@ToString
public class CardProductResponseRestTemplateDto {
    private String cardUniqueNo;
    private String cardIssuerCode;
    private String cardIssuerName;
    private String cardName;
    private String cardTypeCode;
    private String cardTypeName;
    private Long baselinePerformance;
    private Long maxBenefitLimit;
    private String cardDescription;
    private List<CardBenefitsInfo> cardBenefitsInfo;

    public static CardProductResponseRestTemplateDto fromCardResponseDto(
        CardResponseDto cardResponseDto) {
        CardProductResponseRestTemplateDto newCardProduct = new CardProductResponseRestTemplateDto();

        newCardProduct.setCardUniqueNo(cardResponseDto.getCardUniqueNo());
        newCardProduct.setCardIssuerCode(cardResponseDto.getCompanyId());
        newCardProduct.setCardIssuerName(cardResponseDto.getCompanyName());
        newCardProduct.setCardName(cardResponseDto.getCardName());
        newCardProduct.setCardDescription(cardResponseDto.getCardDescription());
        newCardProduct.setBaselinePerformance(cardResponseDto.getPerformanceRemaining());
        newCardProduct.setMaxBenefitLimit(cardResponseDto.getLimitRemaining());

        // CardBenefitsInfo와 CardBenefitResponseDto의 구조에 따라 매핑
        List<CardBenefitsInfo> benefitsInfoList = new ArrayList<>();
        cardResponseDto.getCardBenefits().forEach(benefit -> {
            // 여기서 CardBenefitResponseDto를 CardBenefitsInfo로 변환
            CardBenefitsInfo cardBenefitsInfo = new CardBenefitsInfo();
            // 필요한 필드 변환 및 설정
            cardBenefitsInfo.setCategoryId(benefit.getCategoryId());
            cardBenefitsInfo.setDiscountRate(benefit.getDiscountRate());
            cardBenefitsInfo.setCategoryName(benefit.getMerchantCategory());
            // 예시: cardBenefitsInfo.setSomeField(benefit.getSomeField());

            benefitsInfoList.add(cardBenefitsInfo);
        });
        newCardProduct.setCardBenefitsInfo(benefitsInfoList);

        return newCardProduct;
    }
}