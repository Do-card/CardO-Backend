package a107.cardmore.domain.card.service;

import a107.cardmore.domain.card.dto.CardColorInfo;
import a107.cardmore.domain.card.entity.Card;
import a107.cardmore.domain.card.repository.CardRepository;
import a107.cardmore.domain.company.entity.Company;
import a107.cardmore.domain.company.repository.CompanyRepository;
import a107.cardmore.domain.user.entity.User;
import a107.cardmore.global.exception.BadRequestException;
import a107.cardmore.util.api.dto.card.CardResponseRestTemplateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardModuleService {
    private final CardRepository cardRepository;
    private final CompanyRepository companyRepository;

    public void saveCard(Company company, CardResponseRestTemplateDto cardResponseInfo){
        CardColorInfo cardColorInfo = getColorWithCardUniqueNo(cardResponseInfo.getCardUniqueNo());

        cardRepository.save(Card.builder()
                        .company(company)
                        .cardNo(cardResponseInfo.getCardNo())
                        .cardUniqueNo(cardResponseInfo.getCardUniqueNo())
                        .cvc(cardResponseInfo.getCvc())
                        .cardExpiryDate(cardResponseInfo.getCardExpiryDate())
                        .colorBackground(cardColorInfo.getColorBackground())
                        .colorTitle(cardColorInfo.getColorTitle())
                        .limitRemaining(cardResponseInfo.getMaxBenefitLimit())
                        .performanceRemaining(cardResponseInfo.getBaselinePerformance())
                .build());
    }

    public CardColorInfo getColorWithCardUniqueNo(String cardUniqueNo) {
        Map<String, CardColorInfo> cardColorMap = new HashMap<>();

        // cardUniqueNo와 해당 색상 정보를 매핑
        cardColorMap.put("1001-0c2e8ecd0c434e4", new CardColorInfo("#D3CA9F", "#844301"));
        cardColorMap.put("1001-1c74d779dade4ea", new CardColorInfo("#CDB3FA", "#5B2188"));
        cardColorMap.put("1005-77f96d3e1d414df", new CardColorInfo("#FBB89D", "#FE4437"));
        cardColorMap.put("1005-ee7910828020409", new CardColorInfo("#7497F6", "#0543EC"));
        cardColorMap.put("1009-4c87cbe6fbef4e8", new CardColorInfo("#D8F068", "#00B451"));
        cardColorMap.put("1006-473ecfa048ea400", new CardColorInfo("#6BCEF5", "#014886"));
        cardColorMap.put("1006-0a6bdfbd4a034f8", new CardColorInfo("#FEF33F", "#F8BF00"));
        cardColorMap.put("1002-20c63ccd7f9044e", new CardColorInfo("#01563F", "#D5F169"));
        cardColorMap.put("1002-3a9d4cf6ecce433", new CardColorInfo("#F8BF00", "#DC8A02"));


        return cardColorMap.get(cardUniqueNo);
    }

    public List<Card> findSelectedCardsByUser(User user){
        List<Company> companies = companyRepository.findAllByUser(user);
        return companies.stream().flatMap(company -> cardRepository.findAllByCompanyAndIsSelectedTrue(company).stream()).collect(Collectors.toList());
    }

    public List<Card> findAllCardsByUser(User user){
        List<Company> companies = companyRepository.findAllByUser(user);
        return companies.stream().flatMap(company -> cardRepository.findAllByCompany(company).stream()).collect(Collectors.toList());
    }

    public Card findCardById(Long id){
        return cardRepository.findById(id).orElseThrow(() -> new BadRequestException("Card not found"));
    }

    public Card findCardByCardNo(String cardNo){
        return cardRepository.findByCardNo(cardNo).orElseThrow(() -> new BadRequestException("Card not found"));
    }
}
