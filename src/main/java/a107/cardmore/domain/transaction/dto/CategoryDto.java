package a107.cardmore.domain.transaction.dto;

import a107.cardmore.util.constant.MerchantCategory;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
//TEST
@ToString
public class CategoryDto {
    private String name;
    private Long balance;
}
