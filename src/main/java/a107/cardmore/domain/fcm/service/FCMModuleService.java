package a107.cardmore.domain.fcm.service;

import a107.cardmore.domain.fcm.entity.FCM;
import a107.cardmore.domain.fcm.repository.FCMRepository;
import a107.cardmore.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class FCMModuleService {
    private final FCMRepository fcmRepository;

    public FCM findByUser(User user){
        List<FCM> fcms = fcmRepository.findAllByUserId(user.getId());

        if (fcms.isEmpty()){
            return null;
        }
        return fcms.get(0);
    }

    public void saveToken(FCM fcm){
        //TODO 다른 유저 아이디로 이미 등록된 토큰인 경우 예외 처리
        if(fcmRepository.findByToken(fcm.getToken()) == null) {
            fcmRepository.save(fcm);
        }
    }

    @Transactional
    public void deleteToken(User user, List<String> tokens){
        fcmRepository.deleteByUserAndTokenIn(user, tokens);
    }
}
