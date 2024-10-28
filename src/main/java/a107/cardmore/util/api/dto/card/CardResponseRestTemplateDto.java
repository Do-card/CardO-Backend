package a107.cardmore.util.api.dto.card;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
//TEST
@ToString
public class CardResponseRestTemplateDto {
    private String cardNo;
    private String cvc;
    private String cardUniqueNo;
    private String cardIssuerCode;
    private String cardIssuerName;
    private String cardName;
    private Long baselinePerformance;
    private Long maxBenefitLimit;
    private String cardDescription;
    private String cardExpiryDate;
    private String withdrawalAccountNo;
    private String withdrawalDate;
}
