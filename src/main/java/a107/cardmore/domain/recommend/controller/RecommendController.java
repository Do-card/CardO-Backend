package a107.cardmore.domain.recommend.controller;

import a107.cardmore.domain.user.service.UserModuleService;
import a107.cardmore.util.base.BaseSuccessResponse;
import a107.cardmore.domain.recommend.dto.CardRecommendResponseDto;
import a107.cardmore.domain.recommend.dto.MapRequestWrapperDto;
import a107.cardmore.domain.recommend.dto.MapResponseDto;
import a107.cardmore.domain.recommend.service.RecommendService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/recommends")
public class RecommendController {

    private final RecommendService recommendService;
    private final UserModuleService userModuleService;

    @PostMapping("/discount")
    public BaseSuccessResponse<List<MapResponseDto>> discount(@RequestBody MapRequestWrapperDto mapRequestWrapperDto) {
        Long userId = userModuleService.getUserIdByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return new BaseSuccessResponse<>(recommendService.discountCardRecommend(mapRequestWrapperDto.getMapRequestDtos(), userId));
    }

    @GetMapping("/new")
    public BaseSuccessResponse<List<CardRecommendResponseDto>> recommend() {
        Long userId = userModuleService.getUserIdByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return new BaseSuccessResponse<>(recommendService.recommendNewCard(userId));
    }

}