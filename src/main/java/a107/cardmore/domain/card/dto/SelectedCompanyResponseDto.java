package a107.cardmore.domain.card.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SelectedCompanyResponseDto {
    private List<SelectedInfo> companiesSelectedInfos;
}
