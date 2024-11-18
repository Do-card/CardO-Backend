package a107.cardmore.util.api.dto.card;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCreditCardTransactionResponseRestTemplateDto {
    private Long transactionUniqueNo;
    private String categoryId;
    private String categoryName;
    private Long merchantId;
    private String merchantName;
    private String transactionDate;
    private String transactionTime;
    private Long paymentBalance;

}
