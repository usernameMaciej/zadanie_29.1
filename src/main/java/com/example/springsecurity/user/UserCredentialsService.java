package com.example.springsecurity.user;

import com.example.springsecurity.user.dto.UserCredentialsDto;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserCredentialsService {
    private final UserRepository userRepository;

    public UserCredentialsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserCredentialsDto> findCredentialsByEmail(String email) {
        return userRepository.findByEmail(email).map(this::toUserCredentialsDto);
    }

    private UserCredentialsDto toUserCredentialsDto(User user) {
        String email = user.getEmail();
        String password = user.getPassword();
        Set<String> roles = user.getUserRoles()
                .stream()
                .map(UserRole::getName)
                .collect(Collectors.toSet());
        return new UserCredentialsDto(email, password, roles);
    }
}
