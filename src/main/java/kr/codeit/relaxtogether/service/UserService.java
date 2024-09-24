package kr.codeit.relaxtogether.service;

import static kr.codeit.relaxtogether.exception.ErrorCode.EMAIL_ALREADY_EXISTS;

import kr.codeit.relaxtogether.dto.user.request.EmailCheckRequest;
import kr.codeit.relaxtogether.dto.user.request.JoinUserRequest;
import kr.codeit.relaxtogether.dto.user.request.UpdateUserRequest;
import kr.codeit.relaxtogether.dto.user.response.UserDetailsResponse;
import kr.codeit.relaxtogether.entity.User;
import kr.codeit.relaxtogether.exception.ApiException;
import kr.codeit.relaxtogether.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public boolean checkEmail(EmailCheckRequest emailCheckRequest) {
        if (userRepository.existsByEmail(emailCheckRequest.getEmail())) {
            throw new ApiException(EMAIL_ALREADY_EXISTS);
        }
        return true;
    }

    public UserDetailsResponse getUserDetails(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(RuntimeException::new);
        return UserDetailsResponse.of(user);
    }

    @Transactional
    public void signup(JoinUserRequest joinUserRequest) {
        userRepository.save(joinUserRequest.toEntity(passwordEncoder));
    }

    @Transactional
    public void update(UpdateUserRequest updateUserRequest, String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(RuntimeException::new);
        user.update(updateUserRequest.getCompanyName(), updateUserRequest.getProfileImage());
    }
}
