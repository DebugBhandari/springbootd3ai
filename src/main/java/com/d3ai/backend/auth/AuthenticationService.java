package com.d3ai.backend.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.d3ai.backend.config.JwtService;
import com.d3ai.backend.user.Role;
import com.d3ai.backend.user.User;
import com.d3ai.backend.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
    	if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }
        var user = User.builder()
                .email(request.getEmail())
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("User".equals(request.getRole()) ? Role.USER : Role.ADMIN)
                .stripeCustomerId("Non Stripe User")
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var email = user.getEmail();
        var fullname = user.getFirstname()+ " " + user.getLastname();
        var stripeCustomerId = user.getStripeCustomerId();
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .message("Registration via JWT successful.")
                .email(email)
                .fullname(fullname)
                .stripeCustomerId(stripeCustomerId)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        var jwtToken = jwtService.generateToken(user);
        var email = user.getEmail();
        var fullname = user.getFirstname()+ " " + user.getLastname();
        var stripeCustomerId = user.getStripeCustomerId();
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .message("Logged in via JWT successful.")
                .email(email)
                .fullname(fullname)
                .stripeCustomerId(stripeCustomerId)
                .build();
    }
}
