package a107.cardmore.util.api.dto.card;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Data
@ToString
public class Transaction{
    private Long transactionUniqueNo;
    private String categoryId;
    private String categoryName;
    private Long merchantId;
    private String merchantName;
    private String transactionDate;
    private String transactionTime;
    private Long transactionBalance;
    private String cardStatus;
    private String billStatementsYn;
    private String billStatementsStatus;
}
