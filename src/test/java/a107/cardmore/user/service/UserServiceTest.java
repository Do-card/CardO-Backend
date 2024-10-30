package a107.cardmore.user.service;

import a107.cardmore.common.BaseTest;
import a107.cardmore.domain.user.entity.User;
import a107.cardmore.domain.user.repository.UserRepository;
import a107.cardmore.domain.user.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UserServiceTest extends BaseTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Test
    void 유저를_생성한다() throws Exception {
        // given && when
        User user = User.builder()
                .name("박진우")
                .email("park@ssafy.com")
                .password("1111")
                .accountNo("25")
                .nickName("park")
                .role("VIP")
                .userKey("2AE94F325B164AC334ED")
                .build();

        userRepository.save(user);

        // then
        Assertions.assertNotNull(user.getId());
        assertThat(user.getName()).isEqualTo("박진우");
        assertThat(user.getEmail()).isEqualTo("park@ssafy.com");
        assertThat(user.getPassword()).isEqualTo("1111");
        assertThat(user.getAccountNo()).isEqualTo(user.getAccountNo());
        assertThat(user.getUserKey()).isEqualTo(user.getUserKey());
    }
}
