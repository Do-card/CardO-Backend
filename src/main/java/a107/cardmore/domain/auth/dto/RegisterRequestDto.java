package a107.cardmore.domain.auth.dto;

import a107.cardmore.domain.user.entity.User;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RegisterRequestDto {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String nickName;
    @Column(nullable = false)
    private LocalDate birthday;

    public User createUser(String password, String userKey, String accountNo) {
        return User.builder()
                .email(email)
                .password(password)
                .nickName(nickName)
                .birthday(birthday)
                .role("USER")
                .userKey(userKey)
                .accountNo(accountNo)
                .name(name)
                .build();
    }
}
