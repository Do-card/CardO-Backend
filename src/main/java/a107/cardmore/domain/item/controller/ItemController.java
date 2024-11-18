package a107.cardmore.domain.item.controller;

import a107.cardmore.domain.item.dto.ItemLocalTrendResponseDto;
import a107.cardmore.domain.item.dto.ItemRequestDto;
import a107.cardmore.domain.item.dto.ItemResponseDto;
import a107.cardmore.domain.item.dto.ItemTrendResponseDto;
import a107.cardmore.domain.item.service.ItemElasticService;
import a107.cardmore.domain.item.service.ItemService;
import a107.cardmore.util.base.BaseSuccessResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private final ItemElasticService itemElasticService;

    /**
     * 아이템 등록
     * @param itemRequestDto
     * @return
     */
    @PostMapping("")
    public BaseSuccessResponse<ItemResponseDto> createItem(@RequestBody ItemRequestDto itemRequestDto){
        return new BaseSuccessResponse<>(itemService.saveItem(itemRequestDto));
    }

    /**
     * 아이템 수정
     * @param id
     * @param itemRequestDto
     * @return
     */
    @PutMapping("/{id}")
    public BaseSuccessResponse<ItemResponseDto> updateItem(@PathVariable Long id, @RequestBody ItemRequestDto itemRequestDto){
        return new BaseSuccessResponse<>(itemService.updateItem(id, itemRequestDto));
    }

    /**
     * 아이템 상태 변경: 현재 활성화 상태면 완료로. 완료 상태면 활성화로 변경
     * @param id
     * @return
     */
    @PatchMapping("/{id}")
    public BaseSuccessResponse<ItemResponseDto> updateItem(@PathVariable Long id){
        return new BaseSuccessResponse<>(itemService.changeCompleteState(id));
    }

    /**
     * 아이템 삭제
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public BaseSuccessResponse<Void> deleteItem(@PathVariable Long id){
        itemService.deleteItem(id);
        return new BaseSuccessResponse<>(null);
    }


    @GetMapping("/trend")
    public BaseSuccessResponse<ItemTrendResponseDto> getTrend(){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return new BaseSuccessResponse<>(itemElasticService.getTrends(userEmail));
    }

    @GetMapping("/local-trend")
    public BaseSuccessResponse<List<ItemLocalTrendResponseDto>> getLocalTrends(@RequestParam String poiId){
        return new BaseSuccessResponse<>(itemElasticService.getLocalTrends(poiId));
    }


}
