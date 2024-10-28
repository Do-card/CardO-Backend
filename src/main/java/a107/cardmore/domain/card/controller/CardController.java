package a107.cardmore.domain.card.controller;

import a107.cardmore.domain.card.dto.CardResponseDto;
import a107.cardmore.domain.card.dto.CompanyCardListResponseDto;
import a107.cardmore.domain.card.dto.SelectedCompanyResponseDto;
import a107.cardmore.domain.card.dto.SelectedCardResponseDto;
import a107.cardmore.domain.card.service.CardService;
import a107.cardmore.domain.company.service.CompanyService;
import a107.cardmore.util.base.BaseSuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
@Slf4j
public class CardController {
    private final CardService cardService;
    private final CompanyService companyService;

    @GetMapping("")
    public BaseSuccessResponse<List<CardResponseDto>> getUserSelectedCardInfo(){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return new BaseSuccessResponse<>(cardService.getUserSelectedCardInfo(userEmail));
    }

    @PostMapping("/company")
    public BaseSuccessResponse<Void> updateUserSelectedCompany(@RequestBody SelectedCompanyResponseDto selectedCompanyResponseDto){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        companyService.updateUserSelectedCompany(userEmail, selectedCompanyResponseDto.getCompaniesSelectedInfos());

        return new BaseSuccessResponse<>(null);
    }

    @PostMapping("/card")
    public BaseSuccessResponse<Void> updateUserSelectedCard(@RequestBody SelectedCardResponseDto selectedCardResponseDto){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        cardService.updateUserSelectedCard(selectedCardResponseDto.getCardsSelectedInfos(), userEmail);

        return new BaseSuccessResponse<>(null);
    }

    @GetMapping("/all")
    public BaseSuccessResponse<List<CompanyCardListResponseDto>> getUserAllCardInfo(){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return new BaseSuccessResponse<>(cardService.getUserAllCardInfo(userEmail));
    }




}
