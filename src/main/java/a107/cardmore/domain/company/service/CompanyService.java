package a107.cardmore.domain.company.service;

import a107.cardmore.domain.card.dto.SelectedInfo;
import a107.cardmore.domain.company.dto.InquireCompanyResponseDto;
import a107.cardmore.domain.company.entity.Company;
import a107.cardmore.domain.company.mapper.CompanyMapper;
import a107.cardmore.domain.user.entity.User;
import a107.cardmore.domain.user.service.UserModuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class CompanyService {
    private final UserModuleService userModuleService;
    private final CompanyModuleService companyModuleService;
    private final CompanyMapper companyMapper;

    public List<InquireCompanyResponseDto> inquireUserCompany(String email){
        User user = userModuleService.getUserByEmail(email);

        List<Company> companyList = companyModuleService.findUserCompanies(user);

        for(Company company : companyList){
            log.info(company.getName());
        }

        return companyList.stream().map(companyMapper::toCompanyInfo).toList();
    }

    public List<InquireCompanyResponseDto> updateUserSelectedCompany(String email, List<SelectedInfo> selectedCompanies) {
        User user = userModuleService.getUserByEmail(email);

        for (SelectedInfo selectedInfo : selectedCompanies) {
            Company company = companyModuleService.findCompany(selectedInfo.getId(), user);
            company.changeIsSelected(selectedInfo.getIsSelected());
        }

        return companyModuleService.findUserCompanies(user).stream().map(companyMapper::toCompanyInfo).toList();
    }
}
