package org.example.akigatorapp.controllers.rest;

import jakarta.validation.Valid;
import org.example.akigatorapp.message.request.LoginForm;
import org.example.akigatorapp.message.request.SignUpForm;
import org.example.akigatorapp.message.response.JwtResponse;
import org.example.akigatorapp.models.Role;
import org.example.akigatorapp.models.RoleName;
import org.example.akigatorapp.models.UserEntity;
import org.example.akigatorapp.repositories.RoleRepository;
import org.example.akigatorapp.repositories.UserRepository;
import org.example.akigatorapp.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthRESTController {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final DaoAuthenticationProvider daoAuthenticationProvider;

    @Autowired
    public AuthRESTController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider, DaoAuthenticationProvider daoAuthenticationProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.daoAuthenticationProvider = daoAuthenticationProvider;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody @Valid SignUpForm signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken.");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Username is already registered.");
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

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> login(@RequestBody @Valid LoginForm loginRequest) {
        try {
            Authentication authentication = daoAuthenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtProvider.generateJwtToken(authentication);

            return ResponseEntity.ok(new JwtResponse(jwt));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        }
    }
}
