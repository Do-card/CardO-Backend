package a107.cardmore.domain.auth.controller;

import a107.cardmore.domain.auth.dto.LoginRequestDto;
import a107.cardmore.domain.auth.dto.LoginResponseDto;
import a107.cardmore.domain.auth.dto.RegisterRequestDto;
import a107.cardmore.domain.auth.dto.RegisterResponseDto;
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
    public BaseSuccessResponse<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        log.info("로그인 API");
        return new BaseSuccessResponse<>(authService.login(loginRequestDto));
    }

    @PostMapping("/register")
    public BaseSuccessResponse<RegisterResponseDto> register(@RequestBody RegisterRequestDto registerRequestDto) {
        log.info("사용자 등록 API");
        return new BaseSuccessResponse<>(authService.registerUser(registerRequestDto));
    }
}
