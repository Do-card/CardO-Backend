package a107.cardmore.util.api.dto.card;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCreditCardTransactionRequestRestTemplateDto {
    private String cardNo;
    private String cvc;
    private Long merchantId;
    private Long paymentBalance;
}
