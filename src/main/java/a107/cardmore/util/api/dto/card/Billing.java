package a107.cardmore.util.api.dto.card;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class Billing{
    private String billingWeek;
    private String billingDate;
    private Long totalBalance;
    private String status;
    private String paymentDate;
    private String paymentTime;
}
