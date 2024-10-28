package a107.cardmore.domain.card.dto;

import a107.cardmore.domain.card.entity.Card;
import a107.cardmore.util.api.dto.card.CardBenefitsInfo;
import a107.cardmore.util.api.dto.card.CardProductResponseRestTemplateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CardResponseDto {
    private Long cardId;
    private String companyId;
    private String companyName;
    private String cardNo;
    private String cardName;
    private String cardUniqueNo;
    private String cardTypeCode;
    private String cardTypeName;
    private String cvc;
    private String cardExpiryDate;
    private String cardDescription;
    private String colorBackground;
    private String colorTitle;
    private Boolean isSelected;
    private Long limitRemaining;
    private Long performanceRemaining;
    private List<CardBenefitResponseDto> cardBenefits = new ArrayList<>();

    public CardResponseDto(Card card, CardProductResponseRestTemplateDto cardProduct) {
        this.cardId = card.getId();
        this.companyId = card.getCompany().getCompanyNo();
        this.companyName = card.getCompany().getName();
        this.cardNo = card.getCardNo();
        this.cardUniqueNo = card.getCardUniqueNo();
        this.cvc = card.getCvc();
        this.cardExpiryDate = card.getCardExpiryDate();
        this.cardName = cardProduct.getCardName();
        this.cardDescription = cardProduct.getCardDescription();
        this.colorBackground = card.getColorBackground();
        this.colorTitle = card.getColorTitle();
        this.isSelected = card.getIsSelected();
        this.limitRemaining = card.getLimitRemaining();
        this.performanceRemaining = card.getPerformanceRemaining();
        cardProduct.getCardBenefitsInfo().forEach(benefitsInfo ->
                this.cardBenefits.add(new CardBenefitResponseDto(benefitsInfo)));
    }
}
