package a107.cardmore.domain.item.service;

import a107.cardmore.domain.item.dto.ItemRequestDto;
import a107.cardmore.domain.item.dto.ItemResponseDto;
import a107.cardmore.domain.item.entity.Item;
import a107.cardmore.domain.item.mapper.ItemMapper;
import a107.cardmore.domain.marker.service.MarkerModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ItemService {

    private final ItemModuleService itemModuleService;
    private final MarkerModuleService markerModuleService;
    private final ItemMapper itemMapper;

    public ItemResponseDto saveItem(ItemRequestDto itemRequestDto){
        Item item = itemMapper.toItem(itemRequestDto);
        item.updateMarker(markerModuleService.findById(itemRequestDto.getMarkerId()));
        return itemMapper.toItemResponseDto(itemModuleService.save(item));
    }

    public ItemResponseDto updateItem(Long id, ItemRequestDto itemRequestDto) {
        Item item = itemModuleService.findById(id);
        item.update(itemRequestDto);
        return itemMapper.toItemResponseDto(itemModuleService.save(item));
    }

    public ItemResponseDto changeCompleteState(Long id){
        Item item = itemModuleService.findById(id);
        item.changeState();
        return itemMapper.toItemResponseDto(itemModuleService.save(item));
    }

    public void deleteItem(Long id){
        itemModuleService.delete(id);
    }

}
