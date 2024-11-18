package a107.cardmore.domain.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import static a107.cardmore.util.constant.RedisPrefix.FCM_TOKEN;

@Slf4j
@Component
public class FcmAccessTokenRedisRepository extends BaseRedisRepository<String> {

    private final String ACCESS_TOKEN = "access_token";

    public FcmAccessTokenRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.prefix = FCM_TOKEN;
        this.defaultTtl = 60 * 55; // 55분
    }

    public void saveAccessToken(String accessToken){
        save(ACCESS_TOKEN, accessToken);
    }

    public String getAccessToken(){
        return findById(ACCESS_TOKEN).orElse(null);
    }

}
