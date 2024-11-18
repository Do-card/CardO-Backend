package a107.cardmore.domain.item.mapper;

import a107.cardmore.domain.item.dto.ItemRequestDto;
import a107.cardmore.domain.item.dto.ItemResponseDto;
import a107.cardmore.domain.item.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ItemMapper {

    Item toItem(ItemRequestDto itemRequestDto);

    @Mapping(target = "markerId", source = "marker.id")
    ItemResponseDto toItemResponseDto(Item item);

}
