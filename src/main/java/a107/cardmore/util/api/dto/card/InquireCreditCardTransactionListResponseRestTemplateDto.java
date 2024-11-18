package a107.cardmore.util.api.dto.card;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//import a107.cardmore.util.api.dto.card.Transaction;

import java.util.List;

@Getter
@Setter
//TEST
@ToString
public class
InquireCreditCardTransactionListResponseRestTemplateDto {
    private String cardIssuerCode;
    private String cardIssuerName;
    private String cardName;
    private String cardNo;
    private String cvc;
    private Long estimatedBalance;
    private List<Transaction>transactionList;
}


