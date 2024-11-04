package a107.cardmore.bank.service;

import a107.cardmore.common.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class BankServiceTest extends BaseTest {

    @Test
    void 테스트를_실행한다() throws Exception {
        // given
        String A = "1";
        String B = "1";

        // when


        // then
        assertThat(A.equals(B));


    }
}
