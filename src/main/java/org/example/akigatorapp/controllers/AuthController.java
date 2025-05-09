package org.example.akigatorapp.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.example.akigatorapp.message.request.LoginForm;
import org.example.akigatorapp.message.request.SignUpForm;
import org.example.akigatorapp.models.Role;
import org.example.akigatorapp.models.RoleName;
import org.example.akigatorapp.models.UserEntity;
import org.example.akigatorapp.repositories.RoleRepository;
import org.example.akigatorapp.repositories.UserRepository;
import org.example.akigatorapp.security.jwt.JwtProvider;
import org.example.akigatorapp.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final EmailService emailService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder, JwtProvider jwtProvider, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.emailService = emailService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new SignUpForm());
        return "register";
    }

    @PostMapping("/signup")
    public String registerUser(@ModelAttribute("user") @Valid SignUpForm signUpRequest, Model model) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            model.addAttribute("error", "Username is already taken.");
            return "register";
        }

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            model.addAttribute("error", "Username is already registered.");
            return "register";
        }

        UserEntity user = new UserEntity(
                signUpRequest.getName(),
                signUpRequest.getSurname(),
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword())
        );

        Set<Role> roles = new HashSet<>();
        Optional<Role> userRole = roleRepository.findByName(RoleName.ROLE_USER);

        userRole.ifPresent(roles::add);
        user.setRoles(roles);
        userRepository.save(user);

        return "redirect:/auth/success";
    }

    @GetMapping("/success")
    public String successPage() {
        return "success";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @PostMapping("/signin")
    public String login(@ModelAttribute("loginForm") LoginForm loginRequest, Model model, HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            return "redirect:/auth/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid username or password.");
            return "login";
        }
    }

    @GetMapping("/dashboard")
    public String dashboardPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName());

        if (isAuthenticated) {
            model.addAttribute("username", auth.getName());
        } else {
            model.addAttribute("username", "Guest");
        }

        model.addAttribute("isAuthenticated", isAuthenticated);
        return "dashboard";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/auth/login?logout=true";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage(Model model) {
        return "forgot_password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            userRepository.save(user);
            String resetLink = "http://localhost:8080/auth/reset-password?token=" + token;
            emailService.sendResetPasswordEmail(user.getEmail(), resetLink);
        }
        model.addAttribute("message", "If your email exists, you will receive a password reset link.");
        return "forgot_password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam("token") String token, Model model) {
        Optional<UserEntity> userOptional = userRepository.findByResetToken(token);
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "Invalid or expired reset token.");
            return "reset_password";
        }
        model.addAttribute("token", token);
        return "reset_password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String newPassword,
                                       Model model) {
        Optional<UserEntity> userOptional = userRepository.findByResetToken(token);
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "Invalid or expired reset token.");
            return "reset_password";
        }

        UserEntity user = userOptional.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);

        model.addAttribute("message", "Your password has been successfully reset. You can now log in.");
        return "reset_password";
    }
}
