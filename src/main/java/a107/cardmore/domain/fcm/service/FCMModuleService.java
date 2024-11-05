package a107.cardmore.domain.fcm.service;

import a107.cardmore.domain.fcm.entity.FCM;
import a107.cardmore.domain.fcm.repository.FCMRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class FCMModuleService {
    private final FCMRepository fcmRepository;

    public void saveToken(FCM fcm){
        //TODO 다른 유저 아이디로 이미 등록된 토큰인 경우 예외 처리
        if(fcmRepository.findByToken(fcm.getToken()) == null) {
            fcmRepository.save(fcm);
        }
    }
}
