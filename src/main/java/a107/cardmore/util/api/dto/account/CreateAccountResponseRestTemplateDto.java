package a107.cardmore.util.api.dto.account;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountResponseRestTemplateDto {
    private String bankCode;
    private String accountNo;
    private Currency currency;
}

@Getter
@Setter
class Currency{
    private String currency;
    private String currencyName;
}
