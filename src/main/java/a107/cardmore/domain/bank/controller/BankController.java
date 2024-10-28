package a107.cardmore.domain.bank.controller;

import a107.cardmore.domain.bank.dto.*;
import a107.cardmore.domain.bank.service.BankModuleService;
import a107.cardmore.domain.bank.service.BankService;
import a107.cardmore.util.api.dto.card.*;
import a107.cardmore.util.api.dto.member.CreateMemberResponseRestTemplateDto;
import a107.cardmore.util.api.dto.merchant.MerchantResponseRestTemplateDto;
import a107.cardmore.util.base.BaseSuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/banks")
@RequiredArgsConstructor
@Slf4j
public class BankController {

    private final BankService bankService;

    @PostMapping("/user")
    public BaseSuccessResponse<CreateMemberResponseRestTemplateDto> createUser(@RequestBody CreateUserRequestDto requestDto) {
        log.info("사용자 등록 API");
        return new BaseSuccessResponse<>(bankService.createUser(requestDto));
    }

    @PostMapping("/merchant")
    public BaseSuccessResponse<List<MerchantResponseRestTemplateDto>> createMerchant(@RequestBody CreateMerchantRequestDto requestDto){
        log.info("가맹점 등록 API");
        return new BaseSuccessResponse<>(bankService.createMerchant(requestDto));
    }

    @GetMapping("/merchant")
    public BaseSuccessResponse<List<MerchantResponseRestTemplateDto>> inquireMerchantList(){
        log.info("가맹점 조회 API");
        return new BaseSuccessResponse<>(bankService.inquireMerchant());
    }

    @PostMapping("/creditCardProduct")
    public BaseSuccessResponse<CardProductResponseRestTemplateDto> createCreditCardProduct(@RequestBody CreateCardProductRequestDto requestDto){
        log.info("카드 상품 등록 API");
        return new BaseSuccessResponse<>(bankService.createCreditCardProduct(requestDto));
    }

    @GetMapping("/creditCardProduct")
    public BaseSuccessResponse<List<CardProductResponseRestTemplateDto>> inquireCreditCardProduct(){
        log.info("카드 상품 목록 조회 API");
        return new BaseSuccessResponse<>(bankService.inquireCreditCardProduct());
    }

    @GetMapping("/cardCompany")
    public BaseSuccessResponse<List<CardIssuerCodesListResponseRestTemplateDto>> inquireCardIssuerCodesList(){
        log.info("카드사 목록 조회 API");
        return new BaseSuccessResponse<>(bankService.inquireCardIssuerCodesList());
    }

    @PostMapping("/creditCard/{email}")
    public BaseSuccessResponse<CardResponseRestTemplateDto> createCreditCard(@PathVariable("email") String email, @RequestBody CreateCardRequestDto requestBankDto){
        log.info("카드 등록 API");

        return new BaseSuccessResponse<>(bankService.createCreditCard(email,requestBankDto));
    }

    @GetMapping("/creditCard/{email}")
    public BaseSuccessResponse<List<CardResponseRestTemplateDto>> inquireCreditCard(@PathVariable("email") String email){
        log.info("내 카드 목록 조회 API");
        return new BaseSuccessResponse<>(bankService.inquireSignUpCreditCardList(email));
    }

    @PostMapping("/transaction/{email}")
    public BaseSuccessResponse<CreateCreditCardTransactionResponseRestTemplateDto> createCreditCardTransaction(@PathVariable("email") String email,@RequestBody CreateCreditCardTransactionRequestDto requestBankDto){
        log.info("카드 결제 API");
        return new BaseSuccessResponse<>(bankService.createCreditCardTransaction(email,requestBankDto));
    }

    @PostMapping("/transaction/{email}/inquire")
    public BaseSuccessResponse<InquireCreditCardTransactionListResponseRestTemplateDto> inquireTransaction(@PathVariable("email") String email,@RequestBody InquireCreditCardTransactionListRequestDto requestDto){
        log.info("카드 결제 내역 조회 API");
        return new BaseSuccessResponse<>(bankService.inquireCreditCardTransactionList(email,requestDto));
    }

    @PostMapping("/billing/{email}")
    public BaseSuccessResponse<List<InquireBillingStatementsResponseRestTemplateDto>> inquireBilling(@PathVariable("email") String email,@RequestBody InquireBillingStatementsRequestDto requestBankDto){
        log.info("청구서 조회 API");
        return new BaseSuccessResponse<>(bankService.inquireBillingStatements(email,requestBankDto));
    }
}

