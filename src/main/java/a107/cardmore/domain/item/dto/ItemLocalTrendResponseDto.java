package a107.cardmore.domain.item.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemLocalTrendResponseDto {

   private String category;

   public ItemLocalTrendResponseDto(String category) {
      this.category = category;
   }
}
