package a107.cardmore.domain.item.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemResponseDto {

   private Long id;
   private Long markerId;
   private String name;
   private String majorCategory;
   private String category;
   private Boolean isDone;
   private Boolean isDeleted;

}
