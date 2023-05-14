package com.example.springsecurity.user;

import com.example.springsecurity.user.dto.UserEditDto;
import com.example.springsecurity.user.dto.UserRegisterDto;
import com.example.springsecurity.user.dto.UserRoleWithEmailDto;
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
    String login(@RequestParam(required = false) String registered, Model model) {
        boolean message = false;
        if (registered != null) {
            message = true;
        }
        model.addAttribute("message", message);
        return "login";
    }

    @PostMapping("/register")
    String registerForm(UserRegisterDto userRegisterDto) {
        userService.register(userRegisterDto);
        return "redirect:/login?registered=true";
    }

    @GetMapping("/register")
    String register(Model model) {
        model.addAttribute("user", new UserRegisterDto());
        return "register";
    }

    @GetMapping("/admin")
    String admin(Model model) {
        model.addAttribute("users", userService.findAllUserRoleEmails());
        return "admin";
    }

    @GetMapping("/admin/change-role")
    String assignAdminRole(@RequestParam UserRoleWithEmailDto userRoleWithEmailDto) {
        userService.changeRole(userRoleWithEmailDto);
        return "redirect:/admin";
    }

    @GetMapping("/secured")
    String secured(@RequestParam(required = false) String success, Model model, Authentication authentication) {
        boolean message = false;
        if (success != null) {
            message = true;
        }
        model.addAttribute("message", message);
        String email = authentication.getName();
        UserEditDto userEditDto = userService.findByEmail(email).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
        model.addAttribute("user", userEditDto);
        return "secured";
    }

    @PostMapping("/change-data-user")
    String changeDataUser(String firstName, String lastName) {
        userService.changeDataUser(firstName, lastName);
        return "redirect:/secured?success";
    }

}
