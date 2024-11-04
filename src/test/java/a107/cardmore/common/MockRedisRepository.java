package a107.cardmore.common;

import a107.cardmore.domain.redis.BaseRedisRepository;
import org.springframework.data.redis.core.RedisTemplate;

public class MockRedisRepository extends BaseRedisRepository<String> {

    public MockRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.prefix = "test:";
        this.defaultTtl = 60;
    }
}
