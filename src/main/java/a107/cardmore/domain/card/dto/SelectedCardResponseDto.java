package a107.cardmore.domain.card.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SelectedCardResponseDto {
    private List<SelectedInfo> cardsSelectedInfos = new ArrayList<>();
}
