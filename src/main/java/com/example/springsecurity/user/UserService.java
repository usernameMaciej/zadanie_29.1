package com.example.springsecurity.user;

import com.example.springsecurity.user.dto.UserCredentialsDto;
import com.example.springsecurity.user.dto.UserEditDto;
import com.example.springsecurity.user.dto.UserRegisterDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Transactional
    public void changeDataUser(UserEditDto userEditDto) {

        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        String email = currentUser.getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
        user.setFirstName(userEditDto.getFirstName());
        user.setLastName(userEditDto.getLastName());
        userRepository.save(user);
    }

    public Optional<UserEditDto> findByEmail(String email) {
        return userRepository.findByEmail(email).map(this::userToUserEditDto);
    }

    private UserEditDto userToUserEditDto(User user) {
        return new UserEditDto(user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail());
    }

    @Transactional
    public void changeUserRole(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
        Optional<UserRole> userRole = userRoleRepository.findByName("ADMIN");
        userRole.ifPresentOrElse(
                role -> user.getUserRoles().add(role),
                () -> { throw new NoSuchElementException(); }
        );
        userRepository.save(user);
    }
}
