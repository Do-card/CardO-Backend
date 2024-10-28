package a107.cardmore.domain.recommend.service;

import a107.cardmore.domain.bank.service.BankModuleService;
import a107.cardmore.domain.card.dto.CardBenefitResponseDto;
import a107.cardmore.domain.card.dto.CardColorInfo;
import a107.cardmore.domain.card.dto.CardResponseDto;
import a107.cardmore.domain.card.entity.Card;
import a107.cardmore.domain.card.repository.CardRepository;
import a107.cardmore.domain.card.service.CardModuleService;
import a107.cardmore.domain.recommend.dto.CardMapResponseDto;
import a107.cardmore.domain.recommend.dto.CardRecommendResponseDto;
import a107.cardmore.domain.recommend.dto.MapRequestDto;
import a107.cardmore.domain.recommend.dto.MapResponseDto;
import a107.cardmore.domain.user.entity.User;
import a107.cardmore.domain.user.repository.UserRepository;
import a107.cardmore.domain.user.service.UserModuleService;
import a107.cardmore.util.api.RestTemplateUtil;
import a107.cardmore.util.api.dto.card.CardBenefitsInfo;
import a107.cardmore.util.api.dto.card.CardProductResponseRestTemplateDto;
import a107.cardmore.util.api.dto.card.CardResponseRestTemplateDto;
import a107.cardmore.util.api.dto.card.InquireCreditCardTransactionListRequestRestTemplateDto;
import a107.cardmore.util.api.dto.card.InquireCreditCardTransactionListResponseRestTemplateDto;
import a107.cardmore.util.api.dto.card.Transaction;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendService {

    private static final Logger log = LoggerFactory.getLogger(RecommendService.class);
    private final RestTemplateUtil restTemplateUtil;
    private final CardModuleService cardModuleService;
    private final CardRepository cardRepository;
    private final BankModuleService bankModuleService;
    private final UserModuleService userModuleService;
    private final UserRepository userRepository;

    public List<MapResponseDto> discountCardRecommend(List<MapRequestDto> mapRequestDtoList, Long userId) {
        User user = userRepository.findById(userId).orElse(null); // Todo: moduleService로 바꾸기
        List<CardResponseRestTemplateDto> userCardList = restTemplateUtil.inquireSignUpCreditCardList(user.getUserKey()); //유저의 카드 목록
        List<CardResponseDto> userCardListInfo = getUserCardListInfo(userCardList, userId); //혜택까지 담긴 유저의 카드 목록

        List<MapResponseDto> placeWithCard = new ArrayList<>();
        
        // 카드별 할인율 계산
        for (MapRequestDto place : mapRequestDtoList) {
            Map<CardResponseDto, Double> discountRate = new HashMap<>();

            for (CardResponseDto card : userCardListInfo) {
                List<CardBenefitResponseDto> cardBenefitsInfo = card.getCardBenefits();

                for (CardBenefitResponseDto benefitsInfo : cardBenefitsInfo) {
                    if (benefitsInfo.getCategoryId().equals(place.getMerchantCategory().getValue())) {
                        discountRate.put(card, benefitsInfo.getDiscountRate());
                    }
                }
            }

            // discountRate 정렬
            List<CardResponseDto> topCards = discountRate.entrySet().stream()
                .sorted(Map.Entry.<CardResponseDto, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey) // 카드만 추출
                .collect(Collectors.toList());

            // MapResponseDto로 매핑
            MapResponseDto mapResponseDto = MapResponseDto.builder()
                .name(place.getName())
                .merchantCategory(place.getMerchantCategory())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .address(place.getAddress())
                .placeUrl(place.getPlaceUrl())
                .build();

            if (!topCards.isEmpty()) {
                CardResponseDto topCard = topCards.get(0); // 상위 첫 번째 카드

                // topCard의 혜택 정보를 돌면서 일치하는 카테고리를 찾음
                topCard.getCardBenefits().stream()
                    .filter(benefitsInfo -> benefitsInfo.getCategoryId().equals(place.getMerchantCategory().getValue()))
                    .findFirst() // 일치하는 첫 번째 혜택 정보 찾기
                    .ifPresent(benefitsInfo -> {
                        // merchantCategory와 discountRate를 합친 문자열 생성
                        String discountRateString = benefitsInfo.getMerchantCategory() + " " + benefitsInfo.getDiscountRate().intValue() + "%";
                        // mapResponseDto에 discountDescription 설정
//                        mapResponseDto.setDiscountRate(discountRateString);
                    });

                // 각 카드를 CardMapResponseDto로 변환하여 새로운 리스트에 저장
                List<CardMapResponseDto> updatedCards = new ArrayList<>();
                for (CardResponseDto card : topCards) {
                    CardMapResponseDto cardMapResponseDto = CardMapResponseDto.builder()
                        .card(CardProductResponseRestTemplateDto.fromCardResponseDto(card)) // CardProductResponseRestTemplateDto로 변환
                        .cardNo(card.getCardNo())
                        .expireDate(card.getCardExpiryDate())
                        .colorBackground(card.getColorBackground())
                        .colorTitle(card.getColorTitle())
                        .build();

                    updatedCards.add(cardMapResponseDto);
                }

                mapResponseDto.setCards(updatedCards); // 새로운 CardMapResponseDto 리스트 설정
                placeWithCard.add(mapResponseDto); // 최종 MapResponseDto를 placeWithCard에 추가
            }
        }

        return placeWithCard;
    }

    /**
     * 유저의 모든 카드목록중 선택한 카드를 카드 상품과 매핑하여 리턴해주는 메서드
     * @param userCardList : 금융 API에 저장되어있는 유저의 카드 목록
     * @return isSelected가 true인 카드들을 카드 상품과 매핑하여 혜택정보까지 담은 CardResponseDto 리턴
     */
    public List<CardResponseDto> getUserCardListInfo(List<CardResponseRestTemplateDto> userCardList, Long userId) {
        // userId로 조회한 카드 리스트
        List<Card> userOwnedCards = cardRepository.findCardsByUserId(userId); //TODO: moduleService로 교체

        // 모든 카드목록 조회
        List<CardProductResponseRestTemplateDto> creditCardList = restTemplateUtil.inquireCreditCardList();

        return creditCardList.stream()
            .filter(creditCard -> userCardList.stream()
                .anyMatch(userCard -> userCard.getCardUniqueNo().equals(creditCard.getCardUniqueNo())))
            .flatMap(creditCard -> {
                // 유저가 소유한 카드와 매칭되는 카드 찾기
                Optional<Card> matchedCardOpt = userOwnedCards.stream()
                    .filter(userCard -> userCard.getCardUniqueNo().equals(creditCard.getCardUniqueNo()))
                    .findFirst();

                // 카드와 카드 상품 정보를 바탕으로 CardResponseDto 생성
                return matchedCardOpt.stream().map(matchedCard -> CardResponseDto.builder()
                    .cardId(matchedCard.getId())
                    .companyId(matchedCard.getCompany().getCompanyNo())
                    .companyName(matchedCard.getCompany().getName())
                    .cardNo(matchedCard.getCardNo())
                    .cardUniqueNo(matchedCard.getCardUniqueNo())
                    .cvc(matchedCard.getCvc())
                    .cardExpiryDate(matchedCard.getCardExpiryDate())
                    .cardName(creditCard.getCardName())
                    .cardDescription(creditCard.getCardDescription())
                    .colorBackground(matchedCard.getColorBackground())
                    .colorTitle(matchedCard.getColorTitle())
                    .isSelected(matchedCard.getIsSelected())
                    .limitRemaining(matchedCard.getLimitRemaining())
                    .performanceRemaining(matchedCard.getPerformanceRemaining())
                    .cardTypeCode(creditCard.getCardTypeCode())  // 추가된 필드
                    .cardTypeName(creditCard.getCardTypeName())  // 추가된 필드
                    .cardBenefits(creditCard.getCardBenefitsInfo().stream()
                        .map(
                            CardBenefitResponseDto::new) // CardBenefitsInfo -> CardBenefitResponseDto 변환
                        .collect(Collectors.toList()))
                    .build());
            })
            .collect(Collectors.toList());
    }


    /**
     * 사용자에게 새로운 카드를 추천해주는 로직
     * 사용자의 소비내역을 기반으로 하여 가장 많이 할인 받을 수 있는 금액의 카드를 추
     */
    public List<CardRecommendResponseDto> recommendNewCard(Long userId){
        User user = userRepository.findById(userId).orElse(null);   //Todo: moduleService로 바꾸기
        //모든 카드 리스트
        List<CardProductResponseRestTemplateDto> cardList = restTemplateUtil.inquireCreditCardList();
        //유저 카드 리스트
        List<CardResponseRestTemplateDto> userCardList = restTemplateUtil.inquireSignUpCreditCardList(user.getUserKey());
        //혜택까지 담긴 유저 카드 리스트
        List<CardResponseDto> userCardListInfo = getUserCardListInfo(userCardList, userId);

        Map<CardProductResponseRestTemplateDto, Integer> discountMap = new HashMap<>(); //카드정보, 할인금액 저장
        //유저 카드 순회 시작
        int originalDiscountMoney = 0; //현재 할인받고 있는 금액
        for (CardResponseDto card : userCardListInfo) {
//            String startMonth = getLastMonthStart(); //저번 달 시작일
//            String endMonth = getLastMonthEnd();//저번 달 끝
            //원래는 저번 달 조회가 맞는데 데이터가 없으므로 30일 전으로 로직 변경
            String startMonth = getThirtyDaysAgo(); //30일 전
            String endMonth = getToday();//현재
            InquireCreditCardTransactionListRequestRestTemplateDto cardInfo //청구 카드 정보
                = new InquireCreditCardTransactionListRequestRestTemplateDto(card.getCardNo(),card.getCvc(),startMonth,endMonth);

            //소비 정보
            InquireCreditCardTransactionListResponseRestTemplateDto consumeInfo = restTemplateUtil.inquireCreditCardTransactionList(
                user.getUserKey(), cardInfo);

            //해당 카드의 소비 내역
            List<Transaction> transactionList = consumeInfo.getTransactionList();
            if(transactionList==null) {
                continue;
            }

            for (Transaction t : transactionList) {
                log.info("결제내역: {}", t.toString());
            }

            List<CardBenefitResponseDto> CardBenefitResponseInfo = card.getCardBenefits();
            for(Transaction transaction: transactionList){
                String categoryId = transaction.getCategoryId();
                for(CardBenefitResponseDto benefitResponseInfo : CardBenefitResponseInfo){
                    if(benefitResponseInfo.getCategoryId().equals(categoryId)){
                        originalDiscountMoney += (int) ((transaction.getTransactionBalance() * benefitResponseInfo.getDiscountRate()) / 100);
                        break;
                    }
                }
            }

            // 카드별 소비 돌면서 추천해줄 카드랑 비교하기
            for(CardProductResponseRestTemplateDto newCard: cardList){
                int discountMoney = 0;
                List<CardBenefitsInfo> cardBenefitsInfo = newCard.getCardBenefitsInfo();
                for(Transaction transaction: transactionList){
                    String categoryId = transaction.getCategoryId();
                    //카드 혜택 돌면서 결제 내역의 카테고리와 같은지 확인
                    for(CardBenefitsInfo benefitsInfo : cardBenefitsInfo){
                        if(benefitsInfo.getCategoryId().equals(categoryId)){
                            discountMoney += (int) ((transaction.getTransactionBalance() * benefitsInfo.getDiscountRate()) / 100);
                            break;
                        }
                    }
                }
                discountMap.put(newCard, discountMap.getOrDefault(newCard, 0) + discountMoney);
            }
        }
        // 할인 금액 기준으로 상위 3개의 카드 추출
        List<Map.Entry<CardProductResponseRestTemplateDto, Integer>> sortedDiscountList = new ArrayList<>(discountMap.entrySet());
        sortedDiscountList.sort((entry1, entry2) -> entry2.getValue() - entry1.getValue()); // 내림차순 정렬

        // 상위 3개 카드 추출
        List<CardRecommendResponseDto> top3Recommendations = new ArrayList<>();
        int count = 0;
        for (Map.Entry<CardProductResponseRestTemplateDto, Integer> entry : sortedDiscountList) {
            if (count == 3) {
                break;
            }
            CardProductResponseRestTemplateDto card = entry.getKey();
            int discount = entry.getValue();

            CardColorInfo colorInfo = cardModuleService.getColorWithCardUniqueNo(
                card.getCardUniqueNo());
            top3Recommendations.add(new CardRecommendResponseDto(card, originalDiscountMoney,discount,colorInfo.getColorBackground(),colorInfo.getColorTitle()));
            count++;
        }

        log.info("원래 할인 금액 최종= {}", originalDiscountMoney);
        for (CardRecommendResponseDto recommendation : top3Recommendations) {
            log.info("추천 카드: {}, 할인 금액: {}", recommendation.getCard().getCardName(), recommendation.getDiscountMoney());
        }

        return top3Recommendations;
    }

    public String getLastMonthStart() {
        // 현재 날짜로부터 1개월 전의 YearMonth 객체 생성
        YearMonth lastMonth = YearMonth.now().minusMonths(1);

        // 해당 월의 첫 번째 날짜를 가져오기
        LocalDate firstDate = lastMonth.atDay(1);

        // 과거 달을 YYYYMMDD 형식으로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return firstDate.format(formatter);
    }

    public String getLastMonthEnd() {
        // 현재 날짜로부터 1개월 전의 YearMonth 객체 생성
        YearMonth lastMonth = YearMonth.now().minusMonths(1);

        // 해당 월의 마지막 날짜를 가져오기
        LocalDate lastDate = lastMonth.atEndOfMonth();

        // 과거 달을 YYYYMMDD 형식으로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return lastDate.format(formatter);
    }

    public static String getToday() {
        // 오늘 날짜 가져오기
        LocalDate today = LocalDate.now();

        // 오늘 날짜를 YYYYMMDD 형식으로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return today.format(formatter);
    }

    // 오늘로부터 30일 전의 날짜를 YYYYMMDD 형식으로 리턴하는 메서드
    public static String getThirtyDaysAgo() {
        // 오늘로부터 30일 전의 날짜 가져오기
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);

        // 30일 전 날짜를 YYYYMMDD 형식으로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return thirtyDaysAgo.format(formatter);
    }

}
