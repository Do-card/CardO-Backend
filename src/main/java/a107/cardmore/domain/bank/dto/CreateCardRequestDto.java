package a107.cardmore.domain.bank.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCardRequestDto {
    private String cardUniqueNo;
    private String withdrawalAccountNo;
    private String withdrawalDate;
}
