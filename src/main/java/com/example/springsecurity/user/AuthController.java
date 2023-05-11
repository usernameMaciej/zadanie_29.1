package com.example.springsecurity.user;

import com.example.springsecurity.user.dto.UserEditDto;
import com.example.springsecurity.user.dto.UserRegisterDto;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    String login() {
        return "login";
    }

    @GetMapping("/admin")
    String admin(Model model) {
        model.addAttribute("allUserEmails", userService.findAllUserEmails());
        return "admin";
    }

    @GetMapping("/admin/change-user-role")
    String changeUserRole(@RequestParam String email) {
        userService.changeUserRole(email);
        return "redirect:/admin";
    }

    @GetMapping("/register")
    String register(Model model) {
        model.addAttribute("user", new UserRegisterDto());
        return "register";
    }

    @PostMapping("/register")
    String registerForm(UserRegisterDto userRegisterDto) {
        userService.register(userRegisterDto);
        return "register-success";
    }

    @GetMapping("/secured")
    String secured(Model model, Authentication authentication) {
        String email = authentication.getName();
        UserEditDto userEditDto = userService.findByEmail(email).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
        model.addAttribute("user", userEditDto);
        return "secured";
    }

    @PostMapping("/change-data-user")
    String changeDataUser(UserEditDto userEditDto) {
        userService.changeDataUser(userEditDto);
        return "redirect:/secured";
    }

}
