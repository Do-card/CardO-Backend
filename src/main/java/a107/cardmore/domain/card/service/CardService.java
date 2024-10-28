package a107.cardmore.domain.card.service;

import a107.cardmore.domain.card.dto.CardResponseDto;
import a107.cardmore.domain.card.dto.CompanyCardListResponseDto;
import a107.cardmore.domain.card.dto.SelectedInfo;
import a107.cardmore.domain.card.entity.Card;
import a107.cardmore.domain.company.entity.Company;
import a107.cardmore.domain.company.service.CompanyModuleService;
import a107.cardmore.domain.user.entity.User;
import a107.cardmore.domain.user.service.UserModuleService;
import a107.cardmore.global.exception.BadRequestException;
import a107.cardmore.util.api.RestTemplateUtil;
import a107.cardmore.util.api.dto.card.CardProductResponseRestTemplateDto;
import a107.cardmore.util.api.dto.card.CardResponseRestTemplateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class CardService {
    private final RestTemplateUtil restTemplateUtil;
    private final UserModuleService userModuleService;
    private final CompanyModuleService companyModuleService;
    private final CardModuleService cardModuleService;

    public List<CompanyCardListResponseDto> getUserAllCardInfo(String email){
        User user  = userModuleService.getUserByEmail(email);
        List<CardResponseRestTemplateDto> myCards = restTemplateUtil.inquireSignUpCreditCardList(user.getUserKey());
        List<CardProductResponseRestTemplateDto> cards = restTemplateUtil.inquireCreditCardList();

        // myCards와 Card Repository update
        updateUserCards(myCards, cardModuleService.findAllCardsByUser(user), user);

        // 카드사 정보 list로 만들어 놓기
        List<Card> userCards = cardModuleService.findAllCardsByUser(user);
        List<CompanyCardListResponseDto> companyCards = new ArrayList<>();

        for(Company company : companyModuleService.findUserCompanies(user)){
            CompanyCardListResponseDto companyCard = new CompanyCardListResponseDto();

            // 같은 카드사 카드 정보 저장
            List<Card> userCompanyCards = userCards.stream()
                    .filter(userCard -> userCard.getCompany().equals(company))
                    .collect(Collectors.toList());

            companyCard.addCards(company, userCompanyCards, cards);
            companyCards.add(companyCard);
        }
        return companyCards;
    }

    public void updateUserSelectedCard(List<SelectedInfo> selectedCards, String email) {
        User user  = userModuleService.getUserByEmail(email);

        for (SelectedInfo selectedInfo : selectedCards) {
            Card card = cardModuleService.findCardById(selectedInfo.getId());
            if(card.getCompany().getUser() != user) throw new BadRequestException("타회원의 카드 정보는 수정할 수 없습니다.");
        }

        for (SelectedInfo selectedInfo : selectedCards) {
            Card card = cardModuleService.findCardById(selectedInfo.getId());
            card.changeIsSelected(selectedInfo.getIsSelected());
        }
    }

    public List<CardResponseDto> getUserSelectedCardInfo(String email) {
        User user  = userModuleService.getUserByEmail(email);

        List<Card> userCard = cardModuleService.findSelectedCardsByUser(user);
        List<CardProductResponseRestTemplateDto> cards = restTemplateUtil.inquireCreditCardList();

        List<CardResponseDto> mySelectedCards = new ArrayList<>();

        for(Card card : userCard){
//            System.out.println("cardId : " + card.getId());
            if(!card.getIsSelected()) continue;
            for(CardProductResponseRestTemplateDto restCard : cards){
                if(restCard.getCardUniqueNo().equals(card.getCardUniqueNo())){
                    mySelectedCards.add(new CardResponseDto(card, restCard));
                }
            }
        }

        return mySelectedCards;
    }

    private void updateUserCards(List<CardResponseRestTemplateDto> myCards, List<Card> userCards, User user){
        List<String> existingCardNos = userCards.stream()
                .map(Card::getCardNo)
                .collect(Collectors.toList());

        myCards.stream()
                .filter(myCard -> !existingCardNos.contains(myCard.getCardNo()))
                .forEach(myCard -> {
                    // userCards에서 myCard의 cardNo와 같은 값을 가진 Company 찾기
//                    System.out.println("myCard : " + myCard.getCardNo());
                    Company company = companyModuleService.findUserCompany(myCard.getCardIssuerCode(), user);
                    cardModuleService.saveCard(company, myCard);
                });
    }
}
