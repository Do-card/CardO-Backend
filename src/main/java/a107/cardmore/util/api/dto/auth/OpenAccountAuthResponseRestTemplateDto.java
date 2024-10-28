package a107.cardmore.util.api.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpenAccountAuthResponseRestTemplateDto {
    private String transactionUniqueNo;
    private String accountNo;
}