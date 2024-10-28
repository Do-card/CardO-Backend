package a107.cardmore.util.api.dto.card;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMerchantRequestRestTemplateDto {
    private String categoryId;
    private String merchantName;
}
