package a107.cardmore.domain.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import static a107.cardmore.util.constant.RedisPrefix.ACCESS_TOKEN;

@Slf4j
@Component
public class RefreshTokenRedisRepository extends BaseRedisRepository<String> {

    public RefreshTokenRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.prefix = ACCESS_TOKEN;
        this.defaultTtl = 60 * 60 * 24 * 30; // 30일
    }

}
