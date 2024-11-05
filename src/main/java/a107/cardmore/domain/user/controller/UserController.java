package a107.cardmore.domain.user.controller;

import a107.cardmore.domain.fcm.service.FCMModuleService;
import a107.cardmore.domain.fcm.service.FCMService;
import a107.cardmore.domain.user.dto.FCMTokenRequestDto;
import a107.cardmore.domain.user.dto.PositionRequestDto;
import a107.cardmore.domain.user.service.UserService;
import a107.cardmore.util.base.BaseSuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final FCMModuleService fcmModuleService;
    private final FCMService fcmService;

    @GetMapping
    public BaseSuccessResponse<String> userNickName(){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return new BaseSuccessResponse<>(userService.userNickName(userEmail));
    }

    @PostMapping("/position")
    public BaseSuccessResponse<String> userPosition(PositionRequestDto requestDto) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        return new BaseSuccessResponse<>(userService.checkMarker(userEmail, requestDto));
    }

    @PostMapping("/token")
    public BaseSuccessResponse<Void> saveToken(FCMTokenRequestDto requestDto){
        log.info("Save token : {}", requestDto.getToken());

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.saveToken(userEmail, requestDto.getToken());

        return new BaseSuccessResponse<>(null);
    }
}
