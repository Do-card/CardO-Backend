package a107.cardmore.domain.fcm.service;

import a107.cardmore.domain.fcm.entity.FCM;
import a107.cardmore.domain.fcm.repository.FCMRepository;
import a107.cardmore.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FCMService {

    private final FCMRepository fcmRepository;

    public List<FCM> getFCMList(User user){
        return fcmRepository.findAllByUserId(user.getId());
    }
}
