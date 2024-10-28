package a107.cardmore.domain.card.dto;

import a107.cardmore.domain.card.entity.Card;
import a107.cardmore.domain.company.entity.Company;
import a107.cardmore.global.exception.BadRequestException;
import a107.cardmore.util.api.dto.card.CardProductResponseRestTemplateDto;
import a107.cardmore.util.api.dto.card.CardResponseRestTemplateDto;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyCardListResponseDto {
    private Long companyId;
    private String companyNo;
    private String companyName;
    private Boolean isSelected;
    private List<CardResponseDto> cards = new ArrayList<>();

    public void addCards(Company company, List<Card> userCompanyCards, List<CardProductResponseRestTemplateDto> cardInfos) {
        this.companyId = company.getId();
        this.companyName = company.getName();
        this.companyNo = company.getCompanyNo();
        this.isSelected = company.getIsSelected();

        for(Card userCard : userCompanyCards) {
            CardProductResponseRestTemplateDto cardProduct = cardInfos.stream().filter(card -> card.getCardUniqueNo().equals(userCard.getCardUniqueNo()))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException("card 정보가 없습니다."));

            this.cards.add(new CardResponseDto(userCard, cardProduct));
        }
    }
}
