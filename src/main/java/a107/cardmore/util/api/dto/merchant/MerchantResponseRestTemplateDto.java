package a107.cardmore.util.api.dto.merchant;

import a107.cardmore.util.constant.MerchantCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MerchantResponseRestTemplateDto {
    private String categoryId;
    private String categoryName;
    private Long merchantId;
    private String merchantName;
}
