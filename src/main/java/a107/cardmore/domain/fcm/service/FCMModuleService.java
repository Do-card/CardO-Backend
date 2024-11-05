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
        if(fcmRepository.findByToken(fcm.getToken()) != null) {
            fcmRepository.saveToken(fcm);
        }
    }
}
