package a107.cardmore.domain.fcm.repository;

import a107.cardmore.domain.fcm.entity.FCM;
import a107.cardmore.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FCMRepository extends JpaRepository<FCM,Long> {
    List<FCM> findAllByUserId(Long userId);
    FCM findByToken(String token);
    void deleteByUserAndTokenIn(User user, List<String> tokens);
}
