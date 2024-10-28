package a107.cardmore.domain.user.service;

import a107.cardmore.domain.user.entity.User;
import a107.cardmore.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserModuleService userModuleService;

    public String userNickName(String email) {
        User user = userModuleService.getUserByEmail(email);
        return user.getNickName();
    }
}
