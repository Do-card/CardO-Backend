package a107.cardmore.domain.redis;


import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;

import static java.util.concurrent.TimeUnit.SECONDS;

public abstract class BaseRedisRepository<T> {

    protected RedisTemplate<String, T> redisTemplate;
    protected String prefix;
    protected long defaultTtl;

    public void save(String id, T data, long ttl){
        redisTemplate.opsForValue().set(generateKeyFromId(id), data, ttl, SECONDS);
    }

    public void save(String id, T data){
        save(id, data, defaultTtl);
    }

    public Boolean delete(String id){
        return redisTemplate.delete(generateKeyFromId(id));
    }

    public Optional<T> findById(String id){
        return Optional.ofNullable(redisTemplate.opsForValue().get(generateKeyFromId(id)));
    }

    protected String generateKeyFromId(String id){
        return prefix + id;
    }

}
