package a107.cardmore.domain.item.service;

import a107.cardmore.domain.item.dto.ItemRequestDto;
import a107.cardmore.domain.item.dto.ItemResponseDto;
import a107.cardmore.domain.item.entity.Item;
import a107.cardmore.domain.item.mapper.ItemMapper;
import a107.cardmore.domain.marker.entity.Marker;
import a107.cardmore.domain.marker.service.MarkerModuleService;
import a107.cardmore.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ItemService {

    private final ItemModuleService itemModuleService;
    private final MarkerModuleService markerModuleService;
    private final ItemMapper itemMapper;

    public ItemResponseDto saveItem(ItemRequestDto itemRequestDto){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Marker marker = markerModuleService.findById(itemRequestDto.getMarkerId());
        if(!userEmail.equals(marker.getUser().getEmail())){
            throw new BadRequestException("해당 유저의 마커가 아닙니다.");
        }
        Item item = itemMapper.toItem(itemRequestDto);
        item.updateMarker(markerModuleService.findById(itemRequestDto.getMarkerId()));
        return itemMapper.toItemResponseDto(itemModuleService.save(item));
    }

    public ItemResponseDto updateItem(Long id, ItemRequestDto itemRequestDto) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Item item = itemModuleService.findById(id);
        Marker marker = item.getMarker();
        if(!userEmail.equals(marker.getUser().getEmail())){
            throw new BadRequestException("해당 유저의 마커가 아닙니다.");
        }
        item.update(itemRequestDto);
        return itemMapper.toItemResponseDto(itemModuleService.save(item));
    }

    public ItemResponseDto changeCompleteState(Long id){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Item item = itemModuleService.findById(id);
        Marker marker = item.getMarker();
        if(!userEmail.equals(marker.getUser().getEmail())){
            throw new BadRequestException("해당 유저의 마커가 아닙니다.");
        }
        item.changeState();
        return itemMapper.toItemResponseDto(itemModuleService.save(item));
    }

    public void deleteItem(Long id){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Item item = itemModuleService.findById(id);
        Marker marker = item.getMarker();
        if(!userEmail.equals(marker.getUser().getEmail())){
            throw new BadRequestException("해당 유저의 마커가 아닙니다.");
        }
        itemModuleService.delete(id);
    }

}
