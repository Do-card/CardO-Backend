package a107.cardmore.domain.user.controller;

import a107.cardmore.domain.user.dto.FCMTokenRequestDto;
import a107.cardmore.domain.user.dto.PositionRequestDto;
import a107.cardmore.domain.user.service.UserService;
import a107.cardmore.util.base.BaseSuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public BaseSuccessResponse<String> userNickName(){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return new BaseSuccessResponse<>(userService.userNickName(userEmail));
    }

    @PostMapping("/position")
    public BaseSuccessResponse<Void> userPosition(@RequestBody PositionRequestDto requestDto) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.checkMarker(userEmail, requestDto);

        return new BaseSuccessResponse<>(null);
    }

    @PostMapping("/token")
    public BaseSuccessResponse<Void> saveToken(@RequestBody FCMTokenRequestDto requestDto){

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.saveToken(userEmail, requestDto.getToken());

        return new BaseSuccessResponse<>(null);
    }
}
