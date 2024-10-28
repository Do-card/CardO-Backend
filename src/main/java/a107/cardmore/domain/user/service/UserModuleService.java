package a107.cardmore.domain.user.service;

import a107.cardmore.domain.user.entity.User;
import a107.cardmore.domain.user.repository.UserRepository;
import a107.cardmore.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserModuleService {
    private final UserRepository userRepository;

    public User getUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(() -> new BadRequestException("일치하는 회원이 없습니다."));
    }

    public Long getUserIdByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(() -> new BadRequestException("일치하는 회원이 없습니다.")).getId();
    }

    public String getUserKeyByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(() -> new BadRequestException("일치하는 회원이 없습니다.")).getUserKey();
    }
}
