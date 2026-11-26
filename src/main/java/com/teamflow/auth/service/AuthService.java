
package com.teamflow.auth.service;

import com.teamflow.common.exception.InvalidCredentialsException;
import com.teamflow.user.dto.LoginRequest;
import com.teamflow.user.entity.User;
import com.teamflow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User authenticate(LoginRequest request) {

        String email = request.getEmail()
                .trim()
                .toLowerCase(java.util.Locale.ROOT);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid email or password"
                        )
                );

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if (!passwordMatches) {
            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        return user;
    }
}
