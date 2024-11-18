package a107.cardmore.util.api.dto.card;

import lombok.Getter;
import lombok.Setter;

//import a107.cardmore.util.api.dto.card.Billing;

import java.util.List;

@Getter
@Setter
public class InquireBillingStatementsResponseRestTemplateDto {
    private String billingMonth;
    private List<Billing> billingList;
}
