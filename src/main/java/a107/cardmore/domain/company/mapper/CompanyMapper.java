package a107.cardmore.domain.company.mapper;

import a107.cardmore.domain.company.dto.InquireCompanyResponseDto;
import a107.cardmore.domain.company.entity.Company;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CompanyMapper {
    InquireCompanyResponseDto toCompanyInfo(Company company);
}
