package a107.cardmore.domain.transaction.controller;

import a107.cardmore.domain.transaction.dto.CreateCreditCardTransactionRequestDto;
import a107.cardmore.domain.transaction.dto.CreateCreditCardTransactionResponseDto;
import a107.cardmore.domain.transaction.dto.InquireTransactionResponseDto;
import a107.cardmore.domain.transaction.service.TransactionService;
import a107.cardmore.domain.user.service.UserModuleService;
import a107.cardmore.util.base.BaseSuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {
    private final TransactionService transactionService;
    private final UserModuleService userModuleService;

    @GetMapping
    public BaseSuccessResponse<InquireTransactionResponseDto> getTransactionList(){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return new BaseSuccessResponse<>(transactionService.getTransactionList(email));
    }

    @PostMapping
    public BaseSuccessResponse<CreateCreditCardTransactionResponseDto> createCreditCardTransaction(
            @RequestBody CreateCreditCardTransactionRequestDto requestDto)
    {
        log.info("결제 요청 API");

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String userKey = userModuleService.getUserByEmail(email).getUserKey();

        return new BaseSuccessResponse<>(transactionService.createCreditCardTransaction(userKey,requestDto));
    }

}
