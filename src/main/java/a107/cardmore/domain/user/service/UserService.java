package a107.cardmore.domain.user.service;

import a107.cardmore.domain.fcm.entity.FCM;
import a107.cardmore.domain.fcm.service.FCMModuleService;
import a107.cardmore.domain.fcm.service.FCMService;
import a107.cardmore.domain.user.dto.PositionRequestDto;
import a107.cardmore.domain.user.entity.User;
import a107.cardmore.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserModuleService userModuleService;
    private final FCMModuleService fcmModuleService;
    private final FCMService fcmService;

    public String userNickName(String email) {
        User user = userModuleService.getUserByEmail(email);
        return user.getNickName();
    }

    public String checkMarker(String email, PositionRequestDto requestDto) {
        User user = userModuleService.getUserByEmail(email);

        log.info("위도 경도 {} {}",requestDto.getLatitude(),requestDto.getLongitude());
        
        //TODO 마커 거리 계산해서 진행


        return "";
    }

    public void saveToken(String email, String token) {
        User user = userModuleService.getUserByEmail(email);

        log.info("토큰 : {}", token);

        FCM fcmToken = FCM.builder()
                .token(token)
                .user(user)
                .build();

        fcmModuleService.saveToken(fcmToken);
    }
}
