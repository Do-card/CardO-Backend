package a107.cardmore.domain.auth.dto;

import a107.cardmore.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RegisterRequestDto {
    @NotNull
    private String name;
    @NotNull
    private String email;
    @NotNull
    private String password;
    @NotNull
    private String nickName;

    public User createUser(String password, String userKey, String accountNo) {
        return User.builder()
                .email(email)
                .password(password)
                .nickName(nickName)
                .role("USER")
                .userKey(userKey)
                .accountNo(accountNo)
                .name(name)
                .build();
    }
}
