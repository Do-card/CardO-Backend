package a107.cardmore.domain.transaction.service;

import a107.cardmore.domain.bank.dto.InquireCreditCardTransactionListRequestDto;
import a107.cardmore.domain.card.dto.CardColorInfo;
import a107.cardmore.domain.card.entity.Card;
import a107.cardmore.domain.card.service.CardModuleService;
import a107.cardmore.domain.company.service.CompanyModuleService;
import a107.cardmore.domain.discount.entity.Discount;
import a107.cardmore.domain.discount.service.DiscountModuleService;
import a107.cardmore.domain.transaction.dto.*;
import a107.cardmore.domain.transaction.mapper.TransactionMapper;
import a107.cardmore.domain.user.entity.User;
import a107.cardmore.domain.user.service.UserModuleService;
import a107.cardmore.global.exception.BadRequestException;
import a107.cardmore.util.api.RestTemplateUtil;
import a107.cardmore.util.api.dto.card.CardProductResponseRestTemplateDto;
import a107.cardmore.util.api.dto.card.CardResponseRestTemplateDto;
import a107.cardmore.util.api.dto.card.Transaction;
import a107.cardmore.util.api.dto.merchant.MerchantResponseRestTemplateDto;
import a107.cardmore.util.constant.MerchantCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final RestTemplateUtil restTemplateUtil;
    private final TransactionMapper transactionMapper;
    private final UserModuleService userModuleService;
    private final CompanyModuleService companyModuleService;
    private final CardModuleService cardModuleService;
    private final DiscountModuleService discountModuleService;

    private String getMerchantCategory(String categoryName){
        for(MerchantCategory merchantCategory : MerchantCategory.values()){
            if(merchantCategory.getValue().equalsIgnoreCase(categoryName)){
                return merchantCategory.getKey();
            }
        }
        return null;
    }

    public InquireTransactionResponseDto getTransactionList(String email) {
        //ResponseDto
        InquireTransactionResponseDto inquireTransactionResponse = new InquireTransactionResponseDto();

        //User
        User user = userModuleService.getUserByEmail(email);

        //내 카드 목록
        List<Card> myCardList = cardModuleService.findSelectedCardsByUser(user);
        List<CardResponseRestTemplateDto> inquireCardList = restTemplateUtil.inquireSignUpCreditCardList(user.getUserKey());

        //추가한 카드 목록
        List<CardResponseRestTemplateDto> cardList = new ArrayList<>();
        for(CardResponseRestTemplateDto cardDto : inquireCardList) {
            for(Card card : myCardList) {
                if(Objects.equals(cardDto.getCardNo(), card.getCardNo())) {
                    cardList.add(cardDto);
                    break;
                }
            }
        }

        //결제 내역 요청을 위한 Request Dto
        InquireCreditCardTransactionListRequestDto requestDto = new InquireCreditCardTransactionListRequestDto();

        //기본 날짜 추가
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endDate = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        requestDto.setStartDate(startDate);
        requestDto.setEndDate(endDate);

        //카드별로 결제 내역 가져오기
        for(CardResponseRestTemplateDto card : cardList) {
            requestDto.setCardNo(card.getCardNo());
            requestDto.setCvc(card.getCvc());

            List<Transaction> transactions = restTemplateUtil.inquireCreditCardTransactionList(
                    user.getUserKey(),
                    transactionMapper.toInquireCreditCardTransactionListRequestRestTemplateDto(requestDto)
            ).getTransactionList();

            List<TransactionDto> transactionList = new ArrayList<>();

            //카드 이름 목록 추가
            inquireTransactionResponse.getCardNameList().add(card.getCardName());

            if(transactions != null) {
                
                //결제 내역 추가
                transactionList.addAll(transactions.stream()
                        .map(
                                s->{
                                    TransactionDto t = transactionMapper.toTransactionDto(s);

                                    CardColorInfo cardColorInfo = cardModuleService.getColorWithCardUniqueNo(card.getCardUniqueNo());

                                    t.setCardName(card.getCardName());
                                    t.setColorBackground(cardColorInfo.getColorBackground());
                                    t.setColorTitle(cardColorInfo.getColorTitle());

                                    return t;
                                }
                        )
                        .toList());
            }

            inquireTransactionResponse.getTransactionList().get(0).addAll(transactionList);
            inquireTransactionResponse.getTransactionList().add(transactionList);
        }

        Map<String, Long> categoryBalanceMap = inquireTransactionResponse.getTransactionList().get(0).stream()
                .collect(Collectors.groupingBy(
                        TransactionDto::getCategoryName, // 카테고리 이름으로 그룹화
                        Collectors.summingLong(TransactionDto::getTransactionBalance) // balance 합산
                ));

        // 카테고리 이름별 balance 출력 (혹은 처리)
        categoryBalanceMap.forEach((category, balance) -> {
            CategoryDto categoryDto = new CategoryDto();
            categoryDto.setName(category);
            categoryDto.setBalance(balance);

            inquireTransactionResponse.getCategoryList().add(categoryDto);
        });


        //카드 결제 내역 정렬
        for(List<TransactionDto> transactionDtoList : inquireTransactionResponse.getTransactionList()){
            transactionDtoList.sort(Comparator.comparing(TransactionDto::getTransactionDate).reversed()); // transactionDate 기준 정렬
        }

        return inquireTransactionResponse;
    }

    @Transactional
    public CreateCreditCardTransactionResponseDto createCreditCardTransaction(String userKey, CreateCreditCardTransactionRequestDto requestDto){

        Card card = cardModuleService.findCardByCardNo(requestDto.getCardNo());
        CardProductResponseRestTemplateDto cardProduct = restTemplateUtil.inquireCreditCardList().stream().filter(s->s.getCardUniqueNo().equals(card.getCardUniqueNo())).findFirst().orElseThrow(()->new BadRequestException("존재하지 않는 카드입니다."));

        //가맹점 할인 적용 여부
        List<MerchantResponseRestTemplateDto> merchantList = restTemplateUtil.inquireMerchantList();
        MerchantResponseRestTemplateDto merchant = merchantList.stream().filter(s-> Objects.equals(s.getMerchantId(), requestDto.getMerchantId())).findFirst().orElseThrow(()->new BadRequestException("존재하지 않는 가맹점입니다."));

        if(cardProduct.getCardBenefitsInfo().stream().filter(s->s.getCategoryId().equals(merchant.getCategoryId())).findFirst().orElse(null) != null){
            Long totalDiscountPrice = discountModuleService.findPriceByCategoryAndCardId(merchant.getCategoryId(),card.getId());

            //카드 할인 한도 적용 여부
            if(totalDiscountPrice<cardProduct.getMaxBenefitLimit()){
                //할인 횟수 증가
                card.changeLimitRemaining(card.getLimitRemaining()+1);

                //할인 금액 계산
                double discountRate = cardProduct.getCardBenefitsInfo().stream().filter(s->s.getCategoryName().equals(merchant.getCategoryName())).findFirst().orElseThrow(()->new BadRequestException("카테고리가 존재하지 않습니다.")).getDiscountRate();
                Long discountPrice = (long) (requestDto.getPaymentBalance() * discountRate / 100);
                Long price = totalDiscountPrice +discountPrice <cardProduct.getMaxBenefitLimit()? discountPrice : cardProduct.getMaxBenefitLimit();

                Discount discount = Discount.builder()
                        .card(card)
                        .price(price)
                        .paymentDate(LocalDate.now())
                        .categoryId(merchant.getCategoryId())
                        .merchantCategory(getMerchantCategory(merchant.getCategoryId()))
                        .user(card.getCompany().getUser())
                        .build();

                discountModuleService.saveDiscount(discount);
            }
        }

        return transactionMapper.toCreateCreditCardTransactionResponseDto(restTemplateUtil.createCreditCardTransaction(userKey,transactionMapper.toCreateCreditCardTransactionRequestRestTemplateDto(requestDto)));
    }

}
