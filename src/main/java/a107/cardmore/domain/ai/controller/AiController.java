package a107.cardmore.domain.ai.controller;

import a107.cardmore.domain.ai.dto.AiResponseDto;
import a107.cardmore.domain.ai.service.AiService;
import a107.cardmore.util.base.BaseSuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Slf4j
public class AiController {

    private final AiService aiService;

    @GetMapping("/category")
    public BaseSuccessResponse<AiResponseDto> classifyCategory(@RequestParam("text") String input){
        log.info("AI 요청");
        return new BaseSuccessResponse<>(aiService.getAiPredictResponse(input));
    }

}
