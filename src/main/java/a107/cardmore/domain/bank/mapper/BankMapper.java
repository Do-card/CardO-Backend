package a107.cardmore.domain.bank.mapper;

import a107.cardmore.domain.bank.dto.*;
import a107.cardmore.util.api.dto.card.*;
import a107.cardmore.util.api.dto.member.CreateMemberRequestRestTemplateDto;
import a107.cardmore.util.api.dto.member.CreateMemberResponseRestTemplateDto;
import org.mapstruct.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BankMapper {
    @Mapping(source = "merchantCategory.value", target = "categoryId")
    CreateMerchantRequestRestTemplateDto toCreateMerchantRequestRestTemplateDto(CreateMerchantRequestDto requestDto);

    @Mappings ({
            @Mapping(source = "cardCompany.value", target = "cardIssuerCode"),
            @Mapping(source = "cardBenefits", target = "cardBenefits")
    })
    CreateCardProductRequestRestTemplateDto toCreateCardProductRequestRestTemplateDto(CreateCardProductRequestDto requestDto);

    @Mapping(source = "merchantCategory.value", target = "categoryId")
    CardBenefitsInfo toCardBenefitsInfo(CardBenefit cardBenefit);

    CreateCardRequestRestTemplateDto toCreateCardRequestRestTemplateDto(CreateCardRequestDto requestDto);
    CreateCreditCardTransactionRequestRestTemplateDto toCreateCreditCardTransactionRequestRestTemplateDto(CreateCreditCardTransactionRequestDto requestDto);

    @Mappings({
            @Mapping(source = "startDate", target = "startDate", qualifiedByName = "localDateToStringWithoutHyphen"),
            @Mapping(source = "endDate", target = "endDate", qualifiedByName = "localDateToStringWithoutHyphen")
    })
    InquireCreditCardTransactionListRequestRestTemplateDto toInquireCreditCardTransactionListRequestRestTemplateDto(InquireCreditCardTransactionListRequestDto requestDto);

    // LocalDate -> String 변환 메서드 정의
    @Named("localDateToStringWithoutHyphen")
    default String localDateToStringWithoutHyphen(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.BASIC_ISO_DATE); // 기본적인 yyyyMMdd 포맷으로 변환
    }


    @Mappings({
            @Mapping(source = "startMonth", target = "startMonth", qualifiedByName = "localDateToStringWithoutHyphenAndYearMonth"),
            @Mapping(source = "endMonth", target = "endMonth", qualifiedByName = "localDateToStringWithoutHyphenAndYearMonth")
    })
    InquireBillingStatementsRequestRestTemplateDto toInquireBillingStatementsRequestRestTemplateDto(InquireBillingStatementsRequestDto requestDto);

    // LocalDate -> String 변환 메서드 정의
    @Named("localDateToStringWithoutHyphenAndYearMonth")
    default String localDateToStringWithoutHyphenAndYearMonth(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ofPattern("yyyyMM")); // 기본적인 yyyyMMdd 포맷으로 변환
    }

    CreateMemberRequestRestTemplateDto toCreateMemberRequestRestTemplateDto(CreateUserRequestDto requestDto);
}
