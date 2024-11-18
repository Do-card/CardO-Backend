package a107.cardmore.domain.recommend.dto;

import a107.cardmore.util.constant.MerchantCategory;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class MapRequestDto {

    private String name;
    private MerchantCategory merchantCategory;
    private double latitude;
    private double longitude;
    private String address;
    private String placeUrl;

}

