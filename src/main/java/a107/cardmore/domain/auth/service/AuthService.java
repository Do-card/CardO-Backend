package a107.cardmore.domain.auth.service;

import a107.cardmore.domain.auth.dto.RegisterRequestDto;
import a107.cardmore.domain.auth.dto.RegisterResponseDto;
import a107.cardmore.domain.auth.mapper.AuthMapper;
import a107.cardmore.domain.bank.entity.Bank;
import a107.cardmore.domain.bank.service.BankModuleService;
import a107.cardmore.domain.card.dto.CardColorInfo;
import a107.cardmore.domain.card.service.CardModuleService;
import a107.cardmore.domain.company.entity.Company;
import a107.cardmore.domain.company.service.CompanyModuleService;
import a107.cardmore.domain.user.entity.User;
import a107.cardmore.domain.user.repository.UserRepository;
import a107.cardmore.global.exception.BadRequestException;
import a107.cardmore.util.api.RestTemplateUtil;
import a107.cardmore.util.api.dto.card.CardProductResponseRestTemplateDto;
import a107.cardmore.util.api.dto.card.CardResponseRestTemplateDto;
import a107.cardmore.util.api.dto.card.CreateCardRequestRestTemplateDto;
import a107.cardmore.util.api.dto.member.CreateMemberRequestRestTemplateDto;
import a107.cardmore.util.api.dto.member.CreateMemberResponseRestTemplateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Service
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;
    private final RestTemplateUtil restTemplateUtil;
    private final CompanyModuleService companyModuleService;
    private final CardModuleService cardModuleService;
    private final BankModuleService bankModuleService;

    public RegisterResponseDto registerUser(RegisterRequestDto request) {
        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new BadRequestException("이미 존재하는 이메일 입니다.");
                });

        //유저 등록 여부 확인
        if(!bankModuleService.checkUser(request.getEmail())){
            CreateMemberRequestRestTemplateDto requestDto = new CreateMemberRequestRestTemplateDto();
            requestDto.setEmail(request.getEmail());
            CreateMemberResponseRestTemplateDto responseDto = restTemplateUtil.createMember(requestDto);

            String accountNo = restTemplateUtil.createAccount(responseDto.getUserKey(),"001-1-dcbb072360ee4e").getAccountNo();

            bankModuleService.saveBank(responseDto.getUserId(),responseDto.getUserKey(),accountNo);
        }

        // userKey 등록 가져와야 함
        Bank bank = bankModuleService.getUser(request.getEmail());
        String password = passwordEncoder.encode(request.getPassword());
        User user = userRepository.save(request.createUser(password, bank.getUserKey(), bank.getAccountNo()));

        // 카드사 5곳 저장
        companyModuleService.saveCompanies(user);

        // 개인 카드 저장
        List<CardResponseRestTemplateDto> cardResponseInfos = restTemplateUtil.inquireSignUpCreditCardList(user.getUserKey());

        //카드가 없는 경우
        if(cardResponseInfos.isEmpty()){
            List<String> cardProductList = Arrays.asList(
                    "1001-0c2e8ecd0c434e4",
                    "1001-1c74d779dade4ea",
                    "1005-77f96d3e1d414df",
                    "1005-ee7910828020409",
                    "1009-4c87cbe6fbef4e8",
                    "1006-473ecfa048ea400",
                    "1006-0a6bdfbd4a034f8",
                    "1002-20c63ccd7f9044e",
                    "1002-3a9d4cf6ecce433"
            );

            Collections.shuffle(cardProductList);
            Random random = new Random();

            for(int i=0;i<5;i++){
                CreateCardRequestRestTemplateDto requestDto = new CreateCardRequestRestTemplateDto();
                requestDto.setWithdrawalAccountNo(user.getAccountNo());
                requestDto.setWithdrawalDate(Integer.toString(random.nextInt(7) + 1));
                requestDto.setCardUniqueNo(cardProductList.get(i));
                restTemplateUtil.createCreditCard(user.getUserKey(),requestDto);
            }

            cardResponseInfos = restTemplateUtil.inquireSignUpCreditCardList(user.getUserKey());
        }

        cardResponseInfos.forEach(cardResponseInfo -> {
            Company company = companyModuleService.findUserCompany(cardResponseInfo.getCardIssuerCode(), user);
            cardModuleService.saveCard(company, cardResponseInfo);
        });

        return authMapper.toRegisterResponseDto(user);
    }

}
