package a107.cardmore.util.api.dto.card;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class InquireCreditCardTransactionListRequestRestTemplateDto {
    private String cardNo;
    private String cvc;
    private String startDate;
    private String endDate;
}
