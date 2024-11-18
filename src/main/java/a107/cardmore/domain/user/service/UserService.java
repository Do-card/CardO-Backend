package a107.cardmore.domain.user.service;

import a107.cardmore.domain.fcm.entity.FCM;
import a107.cardmore.domain.fcm.service.FCMModuleService;
import a107.cardmore.domain.marker.dto.MarkerNearbyRequestDto;
import a107.cardmore.domain.marker.dto.MarkerResponseDto;
import a107.cardmore.domain.marker.service.MarkerService;
import a107.cardmore.domain.user.dto.PositionRequestDto;
import a107.cardmore.domain.user.entity.User;
import a107.cardmore.util.api.RestTemplateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final UserModuleService userModuleService;
    private final FCMModuleService fcmModuleService;
    private final MarkerService markerService;
    private final RestTemplateUtil restTemplateUtil;

    public String userNickName(String email) {
        User user = userModuleService.getUserByEmail(email);
        return user.getNickName();
    }

    public void  checkMarker(String email, PositionRequestDto requestDto) {
        log.info("위도 경도 {} {}",requestDto.getLatitude(),requestDto.getLongitude());
        
        //TODO 마커 거리 계산해서 진행
        MarkerNearbyRequestDto markerDto = new MarkerNearbyRequestDto();
        markerDto.setLatitude(requestDto.getLatitude());
        markerDto.setLongitude(requestDto.getLongitude());

        List<MarkerResponseDto> markerList =  markerService.getNearbyMarkers(email,markerDto);

        for(MarkerResponseDto markerResponseDto : markerList) {
            log.info("가까운 마커 id: {}, 이름: {}, 위도: {}, 경도: {}", markerResponseDto.getId(), markerResponseDto.getName(), markerResponseDto.getLatitude(), markerResponseDto.getLongitude());
            restTemplateUtil.FCMPushMessage(markerResponseDto);
        }
    }

    public void saveToken(String email, String token) {
        User user = userModuleService.getUserByEmail(email);
        log.info("토큰 : {}", token);

        FCM fcmToken = fcmModuleService.findByUser(user);

        if (fcmToken == null) {
            fcmToken = FCM.builder()
                    .user(user)
                    .token(token)
                    .build();
        } else {
            fcmToken = fcmToken.toBuilder()
                    .token(token)
                    .build();
        }

        fcmModuleService.saveToken(fcmToken);
    }
}
