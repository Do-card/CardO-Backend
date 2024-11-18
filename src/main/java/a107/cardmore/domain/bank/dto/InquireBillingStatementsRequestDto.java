package a107.cardmore.domain.bank.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class InquireBillingStatementsRequestDto {
    private String cardNo;
    private String cvc;
    private LocalDate startMonth;
    private LocalDate endMonth;
}
