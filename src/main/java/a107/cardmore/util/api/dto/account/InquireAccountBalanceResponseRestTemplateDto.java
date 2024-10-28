package a107.cardmore.util.api.dto.account;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InquireAccountBalanceResponseRestTemplateDto {
    private String bankCode;
    private String accountNo;
    private Long accountBalance;
    private String accountCreatedDate;
    private String accountExpireDate;
    private String lastTransactionDate;
    private String currency;
}