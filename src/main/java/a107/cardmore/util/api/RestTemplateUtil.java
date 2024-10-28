package a107.cardmore.util.api;

import a107.cardmore.domain.bank.dto.CreateUserRequestDto;
import a107.cardmore.global.exception.BadRequestException;
import a107.cardmore.util.api.dto.account.CreateAccountResponseRestTemplateDto;
import a107.cardmore.util.api.dto.account.InquireAccountBalanceResponseRestTemplateDto;
import a107.cardmore.util.api.dto.auth.CheckAuthCodeResponseRestTemplateDto;
import a107.cardmore.util.api.dto.auth.OpenAccountAuthResponseRestTemplateDto;
import a107.cardmore.util.api.dto.card.*;
import a107.cardmore.util.api.dto.member.CreateMemberRequestRestTemplateDto;
import a107.cardmore.util.api.dto.member.CreateMemberResponseRestTemplateDto;
import a107.cardmore.util.api.dto.merchant.MerchantResponseRestTemplateDto;
import a107.cardmore.util.api.template.header.RequestHeader;
import a107.cardmore.util.api.template.response.RECListResponse;
import a107.cardmore.util.api.template.response.RECResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Component
@Slf4j
public class RestTemplateUtil {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${fintech.api.url}")
    private String url;
    @Value("${institution.code}")
    private String institutionCode;
    @Value("${fintech.app.no}")
    private String fintechAppNo;
    @Value("${api.key}")
    private String apiKey;

    //정수형 UUID 생성
    private static String generateNumericUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replaceAll("[^0-9]", "");
    }

    //API 호출용 Header 생성
    private RequestHeader requestHeader(String name, String userKey){
        LocalDateTime today = LocalDateTime.now();

        String date = today.toString().split("T")[0].replace("-","");
        String time = today.toString().split("T")[1].substring(0,8).replace(":","");

        //시퀀스 or UUID or 사용자 아아디 추가
        String numericUUID = generateNumericUUID();

        String institutionTransactionUniqueNo = date + time + numericUUID.substring(0,(Math.min(numericUUID.length(), 6)));

        return RequestHeader.builder()
                .apiName(name)
                .apiKey(apiKey)
                .apiServiceCode(name)
                .transmissionDate(date)
                .transmissionTime(time)
                .fintechAppNo(fintechAppNo)
                .institutionCode(institutionCode)
                .institutionTransactionUniqueNo(institutionTransactionUniqueNo)
                .userKey(userKey)
                .build();
    }

    // 사용자 로그인 API
    // 사용자 생성
    public CreateMemberResponseRestTemplateDto createMember(CreateMemberRequestRestTemplateDto requestDto) {
        log.info("금융 API 사용자 생성 ");
        String uri = "member";

        String email = requestDto.getEmail();

        Map<String,Object>requestBody = new HashMap<>();

        requestBody.put("apiKey",apiKey);
        requestBody.put("userId",email);

        HttpEntity<Object> entity = new HttpEntity<>(requestBody);

        ResponseEntity<CreateMemberResponseRestTemplateDto> response = restTemplate.postForEntity(url + uri, entity, CreateMemberResponseRestTemplateDto.class);
        if(response.getBody() == null){
            throw new BadRequestException("API 요청 중 오류가 발생했습니다.");
        }

        return response.getBody();
    }

    // 수시입출금 계좌 생성
    public CreateAccountResponseRestTemplateDto createAccount(String userKey, String accountTypeUniqueNo) {
        final String name = "createDemandDepositAccount";
        log.info("금융 API 계좌 생성");

        String uri = "edu/demandDeposit/createDemandDepositAccount";

        Map<String,Object>requestBody = new HashMap<>();

        RequestHeader headers = requestHeader(name, userKey);

        requestBody.put("Header",headers);
        requestBody.put("accountTypeUniqueNo",accountTypeUniqueNo);

        HttpEntity<Object> entity = new HttpEntity<>(requestBody);

        ResponseEntity<RECResponse<CreateAccountResponseRestTemplateDto>> response =
                restTemplate.exchange(
                        url + uri,HttpMethod.POST ,entity,
                        new ParameterizedTypeReference<>(){}
                );

        if(response.getBody() == null){
            throw new BadRequestException("API 요청 중 오류가 발생했습니다.");
        }

        return response.getBody().getREC();
    }

    // 수시입출금 계좌 잔액 조회
    public InquireAccountBalanceResponseRestTemplateDto inquireAccountBalance(String userKey, String accountNo){
        final String name = "inquireDemandDepositAccountBalance";
        log.info("금융 API 수시입출금 계좌 잔액 조회");

        String uri = "edu/demandDeposit/inquireDemandDepositAccountBalance";

        Map<String,Object>requestBody = new HashMap<>();

        RequestHeader headers = requestHeader(name, userKey);

        requestBody.put("Header",headers);
        requestBody.put("accountNo",accountNo);

        HttpEntity<Object> entity = new HttpEntity<>(requestBody);

        ResponseEntity<RECResponse<InquireAccountBalanceResponseRestTemplateDto>> response =
                restTemplate.exchange(
                        url + uri,HttpMethod.POST ,entity,
                        new ParameterizedTypeReference<>(){}
                );

        if(response.getBody() == null){
            throw new BadRequestException("API 요청 중 오류가 발생했습니다.");
        }

        return response.getBody().getREC();
    }

    // 수시입출금 계좌 입금
    public void updateDemandDepositAccountDeposit(String userKey, String accountNo, Long transactionBalance, String transactionSummary){
        final String name = "updateDemandDepositAccountDeposit";
        log.info("수시입출금 입금 API");

        String uri = "edu/demandDeposit/updateDemandDepositAccountDeposit";

        Map<String,Object>requestBody = new HashMap<>();

        RequestHeader headers = requestHeader(name, userKey);

        requestBody.put("Header",headers);
        requestBody.put("accountNo",accountNo);
        requestBody.put("transactionBalance",transactionBalance);
        requestBody.put("transactionSummary",transactionSummary);

        HttpEntity<Object> entity = new HttpEntity<>(requestBody);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        url + uri,HttpMethod.POST ,entity,
                        String.class
                );

        if(response.getBody() == null){
            throw new BadRequestException("API 요청 중 오류가 발생했습니다.");
        }

    }

    //1원 인증 API
    //1원 송금
    public OpenAccountAuthResponseRestTemplateDto openAccountAuth(String userKey, String accountNo) {
        log.info("1원 송금 API");

        String uri = "edu/accountAuth/openAccountAuth";

        String name = "openAccountAuth";

        Map<String,Object> requestBody = new HashMap<>();

        RequestHeader headers = requestHeader(name,userKey);

        requestBody.put("Header",headers);
        requestBody.put("accountNo",accountNo);
        requestBody.put("authText","SSAFY");

        HttpEntity<Object> entity = new HttpEntity<>(requestBody);

        ResponseEntity<RECResponse<OpenAccountAuthResponseRestTemplateDto>> response
                = restTemplate.exchange(
                url + uri, HttpMethod.POST, entity,
                new ParameterizedTypeReference<>(){}
        );

        if(response.getBody() == null){
            throw new BadRequestException("API 요청 중 오류가 발생했습니다.");
        }

        return response.getBody().getREC();
    }

    // 1원 송금 인증
    public CheckAuthCodeResponseRestTemplateDto checkAuthCode(String userKey, String accountNo, String authText, String authCode) {
        log.info("1원 송금 인증 API");

        String uri = "edu/accountAuth/checkAuthCode";

        String name = "checkAuthCode";

        Map<String,Object> requestBody = new HashMap<>();

        RequestHeader headers = requestHeader(name,userKey);

        requestBody.put("Header",headers);
        requestBody.put("accountNo",accountNo);
        requestBody.put("authText",authText);
        requestBody.put("authCode",authCode);

        HttpEntity<Object> entity = new HttpEntity<>(requestBody);

        ResponseEntity<RECResponse<CheckAuthCodeResponseRestTemplateDto>> response
                = restTemplate.exchange(
                url + uri, HttpMethod.POST, entity,
                new ParameterizedTypeReference<>(){}
        );

        if(response.getBody() == null){
            throw new BadRequestException("API 요청 중 오류가 발생했습니다.");
        }

        return response.getBody().getREC();
    }
    
    //카드
    //가맹점 등록
    public List<MerchantResponseRestTemplateDto> createMerchant(CreateMerchantRequestRestTemplateDto requestDto) {
        log.info("가맹점 등록 API");

        String uri = "edu/creditCard/createMerchant";

        String name = "createMerchant";

        Map<String,Object> requestBody = new HashMap<>();

        RequestHeader headers = requestHeader(name, null);

        requestBody.put("Header",headers);
        requestBody.put("categoryId",requestDto.getCategoryId());
        requestBody.put("merchantName",requestDto.getMerchantName());

        HttpEntity<Object> entity = new HttpEntity<>(requestBody);

        ResponseEntity<RECListResponse<MerchantResponseRestTemplateDto>> response
                = restTemplate.exchange(
                url + uri, HttpMethod.POST, entity,
                new ParameterizedTypeReference<>(){}
        );

        if(response.getBody() == null){
            throw new BadRequestException("API 요청 중 오류가 발생했습니다.");
        }

        return response.getBody().getREC();
    }

    //카드사 조회
    public List<CardIssuerCodesListResponseRestTemplateDto> inquireCardIssuerCodesList() {
        log.info("카드사 조회 API");

        String uri = "edu/creditCard/inquireCardIssuerCodesList";

        String name = "inquireCardIssuerCodesList";

        Map<String,Object> requestBody = new HashMap<>();

        RequestHeader headers = requestHeader(name, null);

        requestBody.put("Header",headers);

        HttpEntity<Object> entity = new HttpEntity<>(requestBody);

        ResponseEntity<RECListResponse<CardIssuerCodesListResponseRestTemplateDto>> response
                = restTemplate.exchange(
                url + uri, HttpMethod.POST, entity,
                new ParameterizedTypeReference<>(){}
        );

        if(response.getBody() == null){
            throw new BadRequestException("API 요청 중 오류가 발생했습니다.");
        }

        return response.getBody().getREC();
    }

    //카드 상품 등록
    public CardProductResponseRestTemplateDto createCreditCardProduct(CreateCardProductRequestRestTemplateDto requestDto) {
        log.info("카드 상품 등록 API");

        String uri = "edu/creditCard/createCreditCardProduct";

        String name = "createCreditCardProduct";

        Map<String,Object> requestBody = new HashMap<>();

        RequestHeader headers = requestHeader(name, null);

        // ObjectMapper 인스턴스 생성
        ObjectMapper objectMapper = new ObjectMapper();

        requestBody.put("Header",headers);
        requestBody.put("cardIssuerCode",requestDto.getCardIssuerCode());
        requestBody.put("cardName",requestDto.getCardName());
        requestBody.put("baselinePerformance",requestDto.getBaselinePerformance());
        requestBody.put("maxBenefitLimit",requestDto.getMaxBenefitLimit());
        requestBody.put("cardDescription",requestDto.getCardDescription());

        // DTO를 JSON 문자열로 변환
        try {
            String cardBenefits = objectMapper.writeValueAsString(requestDto.getCardBenefits());

            requestBody.put("cardBenefits",requestDto.getCardBenefits());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        log.info(requestBody.toString());

        HttpEntity<Object> entity = new HttpEntity<>(requestBody);

        ResponseEntity<RECResponse<CardProductResponseRestTemplateDto>> response
                = restTemplate.exchange(
                url + uri, HttpMethod.POST, entity,
                new ParameterizedTypeReference<>(){}
        );

        if(response.getBody() == null){
            throw new BadRequestException("API 요청 중 오류가 발생했습니다.");
        }

        return response.getBody().getREC();
    }

    //카드 상품 조회
    public List<CardProductResponseRestTemplateDto> inquireCreditCardList() {
        log.info("카드 상품 조회 API");

        String uri = "edu/creditCard/inquireCreditCardList";

        String name = "inquireCreditCardList";

        Map<String,Object> requestBody = new HashMap<>();

        RequestHeader headers = requestHeader(name, null);

        requestBody.put("Header",headers);

        HttpEntity<Object> entity = new HttpEntity<>(requestBody);

        ResponseEntity<RECListResponse<CardProductResponseRestTemplateDto>> response
                = restTemplate.exchange(
                url + uri, HttpMethod.POST, entity,
                new ParameterizedTypeReference<>(){}
        );

        if(response.getBody() == null){
            throw new BadRequestException("API 요청 중 오류가 발생했습니다.");
        }

        return response.getBody().getREC();
    }

    //카드 등록
    public CardResponseRestTemplateDto createCreditCard(String userKey, CreateCardRequestRestTemplateDto requestDto) {
        log.info("카드 생성 API");

        String uri = "edu/creditCard/createCreditCard";

        String name = "createCreditCard";

        Map<String,Object> requestBody = new HashMap<>();

        RequestHeader headers = requestHeader(name, userKey);

        requestBody.put("Header",headers);
        requestBody.put("cardUniqueNo",requestDto.getCardUniqueNo());
        requestBody.put("withdrawalAccountNo",requestDto.getWithdrawalAccountNo());
        requestBody.put("withdrawalDate",requestDto.getWithdrawalDate());


        HttpEntity<Object> entity = new HttpEntity<>(requestBody);

        ResponseEntity<RECResponse<CardResponseRestTemplateDto>> response
                = restTemplate.exchange(
                url + uri, HttpMethod.POST, entity,
                new ParameterizedTypeReference<>(){}
        );

        if(response.getBody() == null){
            throw new BadRequestException("API 요청 중 오류가 발생했습니다.");
        }

        return response.getBody().getREC();
    }

    //내 카드 목록 조회
    public List<CardResponseRestTemplateDto> inquireSignUpCreditCardList(String userKey) {
        log.info("내 카드 목록 API");
        log.info("userKey->{}",userKey);

        String uri = "edu/creditCard/inquireSignUpCreditCardList";

        String name = "inquireSignUpCreditCardList";

        Map<String,Object> requestBody = new HashMap<>();

        RequestHeader headers = requestHeader(name, userKey);

        requestBody.put("Header",headers);

        HttpEntity<Object> entity = new HttpEntity<>(requestBody);

        ResponseEntity<RECListResponse<CardResponseRestTemplateDto>> response
                = restTemplate.exchange(
                url + uri, HttpMethod.POST, entity,
                new ParameterizedTypeReference<>(){}
        );

        if(response.getBody() == null){
            throw new BadRequestException("API 요청 중 오류가 발생했습니다.");
        }

        return response.getBody().getREC();
    }

    //가맹점 목록 조회
    public List<MerchantResponseRestTemplateDto> inquireMerchantList() {
        log.info("가맹점 목록 API");

        String uri = "edu/creditCard/inquireMerchantList";

        String name = "inquireMerchantList";

        Map<String,Object> requestBody = new HashMap<>();

        RequestHeader headers = requestHeader(name, null);

        requestBody.put("Header",headers);

        HttpEntity<Object> entity = new HttpEntity<>(requestBody);

        ResponseEntity<RECListResponse<MerchantResponseRestTemplateDto>> response
                = restTemplate.exchange(
                url + uri, HttpMethod.POST, entity,
                new ParameterizedTypeReference<>(){}
        );

        if(response.getBody() == null){
            throw new BadRequestException("API 요청 중 오류가 발생했습니다.");
        }

        return response.getBody().getREC();
    }

    //카드 결제
    public CreateCreditCardTransactionResponseRestTemplateDto createCreditCardTransaction(String userKey,CreateCreditCardTransactionRequestRestTemplateDto request) {
        log.info("카드 결제 API");

        String uri = "edu/creditCard/createCreditCardTransaction";

        String name = "createCreditCardTransaction";

        Map<String,Object> requestBody = new HashMap<>();

        RequestHeader headers = requestHeader(name, userKey);

        requestBody.put("Header",headers);
        requestBody.put("cardNo",request.getCardNo());
        requestBody.put("cvc",request.getCvc());
        requestBody.put("merchantId",request.getMerchantId());
        requestBody.put("paymentBalance",request.getPaymentBalance());

        HttpEntity<Object> entity = new HttpEntity<>(requestBody);

        ResponseEntity<RECResponse<CreateCreditCardTransactionResponseRestTemplateDto>> response
                = restTemplate.exchange(
                url + uri, HttpMethod.POST, entity,
                new ParameterizedTypeReference<>(){}
        );

        if(response.getBody() == null){
            log.info("Error -> {}",response.getBody().toString());
            throw new BadRequestException("API 요청 중 오류가 발생했습니다.");
        }

        return response.getBody().getREC();
    }

    //카드 결제 내역 조회
    public InquireCreditCardTransactionListResponseRestTemplateDto inquireCreditCardTransactionList(String userKey, InquireCreditCardTransactionListRequestRestTemplateDto request) {
        log.info("카드 결제 내역 조회 API");

        String uri = "edu/creditCard/inquireCreditCardTransactionList";

        String name = "inquireCreditCardTransactionList";

        Map<String,Object> requestBody = new HashMap<>();

        RequestHeader headers = requestHeader(name, userKey);

        requestBody.put("Header",headers);
        requestBody.put("cardNo",request.getCardNo());
        requestBody.put("cvc",request.getCvc());

        requestBody.put("startDate",request.getStartDate());
        requestBody.put("endDate",request.getEndDate());

        log.info("일자 -> {}",request.getStartDate());

        HttpEntity<Object> entity = new HttpEntity<>(requestBody);

        ResponseEntity<RECResponse<InquireCreditCardTransactionListResponseRestTemplateDto>> response
                = restTemplate.exchange(
                url + uri, HttpMethod.POST, entity,
                new ParameterizedTypeReference<>(){}
        );

        if(response.getBody() == null){
            throw new BadRequestException("API 요청 중 오류가 발생했습니다.");
        }

        return response.getBody().getREC();
    }


    //청구서 조회
    public List<InquireBillingStatementsResponseRestTemplateDto> inquireBillingStatements(String userKey, InquireBillingStatementsRequestRestTemplateDto request) {
        log.info("청구서 조회 API");

        String uri = "edu/creditCard/inquireBillingStatements";

        String name = "inquireBillingStatements";

        Map<String,Object> requestBody = new HashMap<>();

        RequestHeader headers = requestHeader(name, userKey);

        requestBody.put("Header",headers);
        requestBody.put("cardNo",request.getCardNo());
        requestBody.put("cvc",request.getCvc());

        requestBody.put("startMonth",request.getStartMonth());
        requestBody.put("endMonth",request.getEndMonth());

        HttpEntity<Object> entity = new HttpEntity<>(requestBody);

        ResponseEntity<RECListResponse<InquireBillingStatementsResponseRestTemplateDto>> response
                = restTemplate.exchange(
                url + uri, HttpMethod.POST, entity,
                new ParameterizedTypeReference<>(){}
        );

        if(response.getBody() == null){
            throw new BadRequestException("API 요청 중 오류가 발생했습니다.");
        }

        return response.getBody().getREC();
    }


}