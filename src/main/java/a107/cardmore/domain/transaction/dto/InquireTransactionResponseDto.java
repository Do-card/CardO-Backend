package a107.cardmore.domain.transaction.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class InquireTransactionResponseDto {
    List<CategoryDto> categoryList;
    List<String> cardNameList;
    List<List<TransactionDto>> transactionList;

    public InquireTransactionResponseDto() {
        categoryList = new ArrayList<>();
        cardNameList = new ArrayList<>();
        transactionList = new ArrayList<>();

        cardNameList.add("전체");
        transactionList.add(new ArrayList<>());
    }
}
