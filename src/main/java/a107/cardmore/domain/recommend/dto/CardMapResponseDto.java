package a107.cardmore.domain.recommend.dto;

import a107.cardmore.util.api.dto.card.CardProductResponseRestTemplateDto;
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
public class CardMapResponseDto {
    CardProductResponseRestTemplateDto card;
    String expireDate;
    String cardNo;
    String colorBackground;
    String colorTitle;
}
