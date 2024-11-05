package a107.cardmore.domain.ai.service;

import a107.cardmore.domain.ai.dto.AiResponseDto;
import a107.cardmore.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
public class AiService {

    private final RestTemplate restTemplate;

    @Value("${AI_URL}")
    String aiUrl;

    public AiResponseDto getAiPredictResponse(String input){

        String url = aiUrl + "?text=" + input;
        // Response
        ResponseEntity<AiResponseDto> response = restTemplate.getForEntity(url, AiResponseDto.class);

        if(response.getBody()==null){
            throw new BadRequestException("카테고리 분류 API 요청 중 오류가 발생하였습니다.");
        }

        return response.getBody();
    }
}
