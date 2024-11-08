package a107.cardmore.domain.category.service;

import a107.cardmore.domain.category.dto.CategoryResponseDto;
import a107.cardmore.domain.item.entity.Item;
import a107.cardmore.domain.item.service.ItemModuleService;
import a107.cardmore.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final RestTemplate restTemplate;
    private final ItemModuleService itemModuleService;

    @Value("${ai.url}")
    private String aiUrl;

    @Async
    public void setCategory(Item item){
        final String url = aiUrl + "?text=" + item.getName();
        CategoryResponseDto categories = restTemplate.getForEntity(url, CategoryResponseDto.class).getBody();

        if(categories == null){
            throw new BadRequestException("카테고리 분류 API 요청 중 오류가 발생하였습니다.");
        }
        item.updateCategory(categories.getMajorCategory(), categories.getSubCategory());
        itemModuleService.save(item);
    }
}
