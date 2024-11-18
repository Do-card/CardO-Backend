package a107.cardmore.domain.transaction.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCreditCardTransactionResponseDto {
    private Long transactionUniqueNo;
    private String categoryId;
    private String categoryName;
    private Long merchantId;
    private String merchantName;
    private String transactionDate;
    private String transactionTime;
    private Long paymentBalance;
}
