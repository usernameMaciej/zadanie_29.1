package com.example.springsecurity.security;

import com.example.springsecurity.user.UserCredentialsService;
import com.example.springsecurity.user.dto.UserCredentialsDto;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserCredentialsService userCredentialsService;

    public CustomUserDetailsService(UserCredentialsService userCredentialsService) {
        this.userCredentialsService = userCredentialsService;
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userCredentialsService.findCredentialsByEmail(username)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("Użytkownik z emailem: " + username + " nie istnieje"));
    }

    private UserDetails createUserDetails(UserCredentialsDto credentials) {
        return User.builder()
                .username(credentials.getEmail())
                .password(credentials.getPassword())
                .roles(credentials.getRoles().toArray(String[]::new))
                .build();
    }
}
