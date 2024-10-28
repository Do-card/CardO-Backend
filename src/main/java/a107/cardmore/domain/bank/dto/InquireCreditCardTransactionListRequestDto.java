package a107.cardmore.domain.bank.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class InquireCreditCardTransactionListRequestDto {
    private String cardNo;
    private String cvc;
    private LocalDate startDate;
    private LocalDate endDate;
}
