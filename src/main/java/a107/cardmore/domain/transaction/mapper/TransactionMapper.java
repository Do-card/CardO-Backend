package a107.cardmore.domain.transaction.mapper;

import a107.cardmore.domain.bank.dto.InquireCreditCardTransactionListRequestDto;
import a107.cardmore.domain.transaction.dto.CreateCreditCardTransactionRequestDto;
import a107.cardmore.domain.transaction.dto.CreateCreditCardTransactionResponseDto;
import a107.cardmore.domain.transaction.dto.TransactionDto;
import a107.cardmore.util.api.dto.card.*;
import org.mapstruct.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionMapper {
    CreateCreditCardTransactionRequestRestTemplateDto toCreateCreditCardTransactionRequestRestTemplateDto(CreateCreditCardTransactionRequestDto requestDto);
    CreateCreditCardTransactionResponseDto toCreateCreditCardTransactionResponseDto(CreateCreditCardTransactionResponseRestTemplateDto responseDto);

    @Mappings({
            @Mapping(source = "startDate", target = "startDate", qualifiedByName = "localDateToStringWithoutHyphen"),
            @Mapping(source = "endDate", target = "endDate", qualifiedByName = "localDateToStringWithoutHyphen")
    })
    InquireCreditCardTransactionListRequestRestTemplateDto toInquireCreditCardTransactionListRequestRestTemplateDto(InquireCreditCardTransactionListRequestDto requestDto);
    TransactionDto toTransactionDto(Transaction responseDto);

    // LocalDate -> String 변환 메서드 정의
    @Named("localDateToStringWithoutHyphen")
    default String localDateToStringWithoutHyphen(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.BASIC_ISO_DATE); // 기본적인 yyyyMMdd 포맷으로 변환
    }
}
