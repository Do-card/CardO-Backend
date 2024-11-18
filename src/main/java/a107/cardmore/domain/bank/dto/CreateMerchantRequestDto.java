package a107.cardmore.domain.bank.dto;

import a107.cardmore.util.constant.MerchantCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMerchantRequestDto {
    private MerchantCategory merchantCategory;
    private String merchantName;
}
