package a107.cardmore.domain.auth.controller;

import a107.cardmore.domain.auth.dto.*;
import a107.cardmore.domain.auth.service.AuthService;
import a107.cardmore.util.base.BaseSuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public BaseSuccessResponse<TokenResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        log.info("로그인 API");
        return new BaseSuccessResponse<>(authService.login(loginRequestDto));
    }

    @PostMapping("/logout")
    public BaseSuccessResponse<Void> logout(){
        log.info("로그아웃 API");
        authService.logout();
        return new BaseSuccessResponse<>(null);
    }

    @PostMapping("/refresh")
    public BaseSuccessResponse<TokenResponseDto> refresh(@RequestBody RefreshRequestDto requestDto){
        log.info("refresh 토큰 재발급 API");
        return new BaseSuccessResponse<>(authService.refresh(requestDto));
    }

    @PostMapping("/register")
    public BaseSuccessResponse<RegisterResponseDto> register(@RequestBody RegisterRequestDto registerRequestDto) {
        log.info("사용자 등록 API");
        return new BaseSuccessResponse<>(authService.registerUser(registerRequestDto));
    }

    @GetMapping("/check")
    public BaseSuccessResponse<Void> checkAuth(){
        log.info("로그인 여부 확인");
        authService.checkAuth();
        return new BaseSuccessResponse<>(null);
    }
}
