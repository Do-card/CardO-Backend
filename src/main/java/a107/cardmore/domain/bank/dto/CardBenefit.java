package a107.cardmore.domain.bank.dto;


import a107.cardmore.util.constant.MerchantCategory;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
//TEST
@ToString
public class CardBenefit{
    private MerchantCategory merchantCategory;
    private Double discountRate;
}
