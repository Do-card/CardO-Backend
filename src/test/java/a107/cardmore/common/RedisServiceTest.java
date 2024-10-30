package a107.cardmore.common;

import a107.cardmore.domain.redis.BaseRedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RedisServiceTest extends BaseTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @InjectMocks
    private MockRedisRepository mockRedisRepository;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    }

    @Test
    void 레디스_ID를_삭제한다() throws Exception {
        String id = "123";

        mockRedisRepository.delete(id);

        verify(redisTemplate).delete("test:123");
    }

    @Test
    void 레디스_ID로_찾는다() throws Exception {
        String id = "123";
        String data = "Test Data";

        given(redisTemplate.opsForValue().get("test:123")).willReturn(data);

        Optional<String> result = mockRedisRepository.findById(id);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(data);
    }

    @Test
    void 레디스_ID로_찾는데_없다() {
        String id = "123";

        given(redisTemplate.opsForValue().get("test:123")).willReturn(null);

        Optional<String> result = mockRedisRepository.findById(id);

        assertThat(result).isNotPresent();
    }
}
