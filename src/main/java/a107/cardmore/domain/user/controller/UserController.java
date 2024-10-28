package a107.cardmore.domain.user.controller;

import a107.cardmore.domain.user.service.UserService;
import a107.cardmore.util.base.BaseSuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public BaseSuccessResponse<String> userNickName(){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return new BaseSuccessResponse<>(userService.userNickName(userEmail));
    }
}
