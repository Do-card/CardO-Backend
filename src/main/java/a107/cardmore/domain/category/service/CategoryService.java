package a107.cardmore.domain.category.service;

import a107.cardmore.domain.category.dto.CategoryResponseDto;
import a107.cardmore.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final RestTemplate restTemplate;

    @Value("${ai.url}")
    private String aiUrl;

    public CategoryResponseDto getAiPredictResponse(String input){
        final String url = aiUrl + "?text=" + input;
        ResponseEntity<CategoryResponseDto> response = restTemplate.getForEntity(url, CategoryResponseDto.class);

        if(response.getBody()==null){
            throw new BadRequestException("카테고리 분류 API 요청 중 오류가 발생하였습니다.");
        }

        return response.getBody();
    }
}
