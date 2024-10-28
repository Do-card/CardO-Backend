package a107.cardmore.domain.bank.service;

import a107.cardmore.domain.bank.entity.Bank;
import a107.cardmore.domain.bank.repository.BankRepository;
import a107.cardmore.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankModuleService {
    private final BankRepository bankRepository;

    public Boolean checkUser(String email){
        return bankRepository.findByEmail(email).isPresent();
    }

    public Bank getUser(String email){
        return bankRepository.findByEmail(email).orElseThrow(()->new BadRequestException("존재하지 않는 이메일입니다."));
    }

    public String getUserKeyByEmail(String email){
        return bankRepository.findByEmail(email).orElseThrow(()->new BadRequestException("존재하지 않는 이메일입니다.")).getUserKey();
    }

    @Transactional
    public void saveBank(String email, String userKey, String accountNo){
        Bank bank = Bank.builder()
                .email(email)
                .userKey(userKey)
                .accountNo(accountNo)
                .build();
        bankRepository.save(bank);
    }
}
