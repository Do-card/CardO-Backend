package a107.cardmore.util.api.dto.card;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCardRequestRestTemplateDto {
    private String cardUniqueNo;
    private String withdrawalAccountNo;
    private String withdrawalDate;
}
