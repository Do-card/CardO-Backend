package a107.cardmore.util.api;

import a107.cardmore.domain.fcm.entity.FCM;
import a107.cardmore.domain.fcm.service.FCMModuleService;
import a107.cardmore.domain.fcm.service.FCMService;
import a107.cardmore.domain.marker.dto.MarkerResponseDto;
import a107.cardmore.domain.redis.FcmAccessTokenRedisRepository;
import a107.cardmore.domain.redis.FcmCacheRepository;
import a107.cardmore.domain.user.entity.User;
import a107.cardmore.domain.user.service.UserModuleService;
import a107.cardmore.global.exception.BadRequestException;
import a107.cardmore.util.api.dto.FCM.FCMData;
import a107.cardmore.util.api.dto.FCM.Message;
import a107.cardmore.util.api.dto.FCM.Notification;
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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
@RequiredArgsConstructor
public class RestTemplateUtil {

    private final FCMService fcmService;
    private final FCMModuleService fcmModuleService;
    private final UserModuleService userModuleService;
    private final RestTemplate restTemplate;
    private final FcmAccessTokenRedisRepository fcmAccessTokenRedisRepository;
    private final FcmCacheRepository fcmCacheRepository;

    @Value("${fintech.api.url}")
    private String url;
    @Value("${fintech.api.key}")
    private String apiKey;
    @Value("${fintech.app.no}")
    private String fintechAppNo;
    @Value("${fintech.institution.code}")
    private String institutionCode;

    @Value("${fcm.private-key}")
    private String fcmPrivateKey;
    @Value("${fcm.client-email}")
    private String fcmClientEmail;

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

    //Firebase
    public void FCMPushMessage(MarkerResponseDto markerList){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userModuleService.getUserByEmail(email);
        final String FCMURL = "https://fcm.googleapis.com/v1/projects/card-o-ba82e/messages:send";

        List<FCM> fcmTokens = fcmService.getFCMList(user);
        List<String> disabledFcmTokens = new ArrayList<>();
        String fcmAccessToken = fcmAccessTokenRedisRepository.getAccessToken();
        if (fcmAccessToken == null){
            fcmAccessToken = refreshAccessToken();
            fcmAccessTokenRedisRepository.saveAccessToken(fcmAccessToken);
            log.info("accessToken 발급: {}", fcmAccessToken);
        }

        for (FCM fcmToken : fcmTokens) {
            try {
                if (fcmCacheRepository.hasHistory(fcmToken, markerList)){
                    log.info("fcm 메세지 전송 대기상태");
                    return;
                }

                fcmCacheRepository.saveHistory(fcmToken, markerList);
                log.info("fcm 메세지 전송 시작 token: {}", fcmToken.getToken());
                ResponseEntity<?> response = restTemplate.exchange(
                        FCMURL,
                        HttpMethod.POST,
                        getEntity(markerList, fcmAccessToken, fcmToken),
                        new ParameterizedTypeReference<>() {
                        }
                );
                log.info("fcm 메세지 전송 성공: {}", response.getBody());
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    // Access Token 만료 -> Refresh Token으로 재발급
                    String newAccessToken = refreshAccessToken();
                    fcmAccessTokenRedisRepository.saveAccessToken(newAccessToken);
                    log.info("accessToken 재발급");

                    // 재시도
                    fcmCacheRepository.saveHistory(fcmToken, markerList);
                    ResponseEntity<?> response = restTemplate.exchange(
                            FCMURL,
                            HttpMethod.POST,
                            getEntity(markerList, newAccessToken, fcmToken),
                            new ParameterizedTypeReference<>() {}
                    );
                    log.info("fcm 메세지 재전송 성공: {}", response.getBody());
                } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    log.warn("유효하지 않은 FCM 토큰입니다: {}", fcmToken);
                    disabledFcmTokens.add(fcmToken.getToken());
                } else {
                    log.error("FCM 메세지 전송 실패: {}", e.getMessage());
                }
            }
        }
        disableOrDeleteToken(user, disabledFcmTokens);
    }

    private void disableOrDeleteToken(User user, List<String> disabledFcmToken){
        if (disabledFcmToken.isEmpty()){
            return;
        }
        fcmModuleService.deleteToken(user, disabledFcmToken);
        log.info("만료된 FCM 토큰 삭제: {}", disabledFcmToken);
    }

    private HttpEntity<Object> getEntity(MarkerResponseDto markerList, String accessToken, FCM fcmToken){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        Message message = getMessage(markerList, fcmToken);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("message", message);
        return new HttpEntity<>(requestBody, headers);
    }

    private Message getMessage(MarkerResponseDto markerList, FCM fcmToken) {
        Message message = new Message();
        FCMData data = new FCMData();
        Notification notification = new Notification();

        data.setUrl("https://k11a402.p.ssafy.io/map");
        notification.setTitle(markerList.getPoiName() + "에서 할 일이 있어요");

        if(!markerList.getItems().isEmpty()){
            notification.setBody(markerList.getItems().get(0).getName() + " 외 " + (markerList.getItems().size() - 1) + "개의 할 일이 있어요");
        }
        else{
            notification.setBody("아직 " + markerList.getPoiName() + "에서 추가된 할 일이 없어요");
        }

        message.setToken(fcmToken.getToken());
        message.setNotification(notification);
        message.setData(data);
        return message;
    }

    // FCM OAuth2 토큰 재발급 요청
    private String refreshAccessToken(){
        String jwt = createJwt(fcmClientEmail, fcmPrivateKey);

        Map<String, String> requestBody = Map.of(
                "grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer",
                "assertion", jwt
        );
        // JWT를 이용한 Access Token 발급 요청
        JsonNode response = restTemplate.postForObject(
                "https://oauth2.googleapis.com/token",
                new HttpEntity<>(requestBody),
                JsonNode.class
        );
        return response.get("access_token").asText();
    }

    private String createJwt(String clientEmail, String privateKeyPem) {
        try {
            long now = System.currentTimeMillis();
            Date expirationTime = new Date(now + TimeUnit.HOURS.toMillis(1));

            String privateKeyContent = privateKeyPem.replace("\n", "")
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "");

            byte[] keyBytes = Base64.getDecoder().decode(privateKeyContent);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            PrivateKey privateKey = java.security.KeyFactory.getInstance("RSA").generatePrivate(keySpec);

            return Jwts.builder()
                    .issuer(clientEmail)
                    .subject(clientEmail)
                    .setAudience("https://oauth2.googleapis.com/token")
                    .issuedAt(new Date(now))
                    .expiration(expirationTime)
                    .claim("scope", "https://www.googleapis.com/auth/firebase.messaging")
                    .signWith(privateKey, SignatureAlgorithm.RS256)
                    .compact();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ignored){
            throw new BadRequestException("FCM Access토큰 발급에 실패했습니다.");
        }
    }
}