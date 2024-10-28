package a107.cardmore.domain.bank.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCreditCardTransactionRequestDto {
    private String cardNo;
    private String cvc;
    private Long merchantId;
    private Long paymentBalance;
}
