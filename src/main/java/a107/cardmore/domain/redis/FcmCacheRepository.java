package a107.cardmore.domain.redis;

import a107.cardmore.domain.fcm.entity.FCM;
import a107.cardmore.global.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

import static a107.cardmore.util.constant.RedisPrefix.FCM_TOKEN;

@Slf4j
@Component
public class FcmCacheRepository extends BaseRedisRepository<String> {

    public FcmCacheRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.prefix = FCM_TOKEN;
        this.defaultTtl = 60 * 5; // 5분
    }

    public void saveHistory(FCM token){
        save(generateKeyFromId(token.getToken()), new Date().toString());
    }

    public Boolean hasHistory(FCM token){
        Boolean hasKey = redisTemplate.hasKey(generateKeyFromId(token.getToken()));

        if (hasKey == null){
            throw new BadRequestException("Token not found");
        }
        return hasKey;
    }
}
