package a107.cardmore.domain.recommend.dto;

import a107.cardmore.domain.card.dto.CardResponseDto;
import a107.cardmore.util.constant.MerchantCategory;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MapResponseDto {

    private String name;
    private MerchantCategory merchantCategory;
    private double latitude;
    private double longitude;
    private String address;
    private String placeUrl;
    List<CardMapResponseDto> cards;

}