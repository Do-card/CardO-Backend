package a107.cardmore.domain.redis;

import a107.cardmore.global.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import static a107.cardmore.util.constant.RedisPrefix.BLACKLIST;

@Slf4j
@Component
public class BlacklistTokenRedisRepository extends BaseRedisRepository<String> {

    public BlacklistTokenRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.prefix = BLACKLIST;
        this.defaultTtl = 60 * 60 * 24 * 7; //7일
    }

    public void save(String token, long ttl){
        save(token, "EXPIRED", ttl);
    }

    public Boolean hasKey(String token){
        Boolean hasKey = redisTemplate.hasKey(prefix + token);

        if (hasKey == null){
            throw new BadRequestException("Token not found");
        }
        return hasKey;
    }

}
