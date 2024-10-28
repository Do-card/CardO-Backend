package a107.cardmore.domain.company.controller;

import a107.cardmore.domain.card.dto.SelectedInfo;
import a107.cardmore.domain.company.dto.InquireCompanyResponseDto;
import a107.cardmore.domain.company.service.CompanyService;
import a107.cardmore.util.base.BaseSuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
@Slf4j
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    public BaseSuccessResponse<List<InquireCompanyResponseDto>> inquireCompanyList(){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return new BaseSuccessResponse<>(companyService.inquireUserCompany(userEmail));
    }

    @PutMapping
    public BaseSuccessResponse<List<InquireCompanyResponseDto>> updateCompany(@RequestBody List<SelectedInfo> requestDto) {
        log.info(requestDto.toString());
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return new BaseSuccessResponse<>(companyService.updateUserSelectedCompany(userEmail,requestDto));
    }
}
