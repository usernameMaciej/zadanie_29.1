package com.example.springsecurity.user;

import com.example.springsecurity.user.dto.UserEditDto;
import com.example.springsecurity.user.dto.UserRegisterDto;
import com.example.springsecurity.user.dto.UserRoleWithEmailDto;
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

    private static final String USER_ROLE = "USER";
    private static final String ADMIN_ROLE = "ADMIN";

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserRoleWithEmailDto> findAllUserRoleEmails() {
        return userRepository.findAll()
                .stream()
                .map(this::userToUserRoleWithEmailDto)
                .collect(Collectors.toList());
    }

    private UserRoleWithEmailDto userToUserRoleWithEmailDto(User user) {
        // nie mam pojecia jak to sprawdzic przez tego booleana chociazby czy jest adminem czy userem
        return new UserRoleWithEmailDto(user.getEmail(), user.getUserRoles().);
    }

    @Transactional
    public void register(UserRegisterDto userRegisterDto) {
        User user = new User();
        user.setFirstName(userRegisterDto.getFirstName());
        user.setLastName(userRegisterDto.getLastName());
        user.setEmail(userRegisterDto.getEmail());
        String passwordHash = passwordEncoder.encode(userRegisterDto.getPassword());
        user.setPassword(passwordHash);

        Optional<UserRole> userRole = userRoleRepository.findByName(USER_ROLE);
        userRole.ifPresentOrElse(
                role -> user.getUserRoles().add(role),
                () -> {
                    throw new NoSuchElementException();
                }
        );
        userRepository.save(user);
    }

    @Transactional
    public void changeDataUser(String firstName, String lastName) {

        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        String email = currentUser.getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
        user.setFirstName(firstName);
        user.setLastName(lastName);
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
    public void changeRole(UserRoleWithEmailDto userRoleWithEmailDto) {
        User user = userRepository.findByEmail(userRoleWithEmailDto.getEmail()).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
        Optional<UserRole> adminRole = userRoleRepository.findByName(ADMIN_ROLE);

        if (!userRoleWithEmailDto.isAdmin()) {
            adminRole.ifPresentOrElse(
                    role -> user.getUserRoles().add(role),
                    () -> {
                        throw new NoSuchElementException();}
            );
        } else {
            adminRole.ifPresentOrElse(
                    role -> user.getUserRoles().remove(role),
                    () -> { throw new NoSuchElementException();}
            );
            userRepository.save(user);
        }
        userRepository.save(user);
    }
}