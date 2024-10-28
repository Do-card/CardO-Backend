package a107.cardmore.domain.company.service;

import a107.cardmore.domain.company.entity.Company;
import a107.cardmore.domain.company.repository.CompanyRepository;
import a107.cardmore.domain.user.entity.User;
import a107.cardmore.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CompanyModuleService {
    private final CompanyRepository companyRepository;

    public void saveCompanies(User user){
        Map<String, String> companyMap = new HashMap<>(Map.of(
                "신한카드", "1005",
                "하나카드", "1009",
                "KB국민카드", "1001",
                "현대카드", "1006",
                "삼성카드", "1002"
        ));

        companyMap.forEach((companyName, companyNumber) -> {
            companyRepository.save(Company.builder()
                    .user(user)
                    .name(companyName)   // 카드사 이름
                    .companyNo(companyNumber)  // 카드 번호
                    .build());
        });
    }

    public Company findUserCompany(String companyNumber, User user) {
        return companyRepository.findByUserAndCompanyNo(user, companyNumber)
                .orElseThrow(() -> new BadRequestException("사용자와 맞는 카드사 정보가 없습니다."));
    }

    public Company findCompany(Long companyId, User user){
        return companyRepository.findByIdAndUser(companyId, user)
                .orElseThrow(() -> new BadRequestException("사용자와 맞는 카드사 정보가 없습니다."));
    }

    public List<Company> findUserCompanies(User user) {
        return companyRepository.findAllByUser(user);
    }
}
