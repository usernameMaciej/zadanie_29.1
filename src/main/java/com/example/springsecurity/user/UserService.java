package com.example.springsecurity.user;

import com.example.springsecurity.user.dto.UserCredentialsDto;
import com.example.springsecurity.user.dto.UserRegisterDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<String> findAllUserEmails() {
        return userRepository.findAllUsersByUserRoles_Name("USER")
                .stream()
                .map(User::getEmail)
                .collect(Collectors.toList());
    }

    @Transactional
    public void register(UserRegisterDto userRegisterDto) {
        User user = new User();
        user.setFirstName(userRegisterDto.getFirstName());
        user.setLastName(userRegisterDto.getLastName());
        user.setEmail(userRegisterDto.getEmail());
        String passwordHash = passwordEncoder.encode(userRegisterDto.getPassword());
        user.setPassword(passwordHash);

        Optional<UserRole> userRole = userRoleRepository.findByName("USER");
        userRole.ifPresentOrElse(
                role -> user.getUserRoles().add(role),
                () -> { throw new NoSuchElementException(); }
        );
        userRepository.save(user);
    }

    public Optional<UserCredentialsDto> findByEmail(String email) {
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

    @Transactional
    public void changeDataUser(UserRegisterDto userRegisterDto) {
        User user = userRepository.findByEmail(userRegisterDto.getEmail()).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
        user.setFirstName(userRegisterDto.getFirstName());
        user.setLastName(userRegisterDto.getLastName());
        String passwordHash = passwordEncoder.encode(userRegisterDto.getPassword());
        user.setPassword(passwordHash);
        userRepository.save(user);
    }

    @Transactional
    public void changeUserRole(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
        //? ?
    }
}
