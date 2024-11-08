package a107.cardmore.domain.redis;

import a107.cardmore.global.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import static a107.cardmore.util.constant.RedisPrefix.REFRESH_TOKEN;

@Slf4j
@Component
public class RefreshTokenRedisRepository extends BaseRedisRepository<String> {

    public RefreshTokenRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.prefix = REFRESH_TOKEN;
        this.defaultTtl = 60 * 60 * 24 * 30; // 30일
    }

    public Boolean hasKey(String token){
        Boolean hasKey = redisTemplate.hasKey(prefix + token);

        if (hasKey == null){
            throw new BadRequestException("Token not found");
        }
        return hasKey;
    }

}
