package com.d3ai.backend.auth;

import org.springframework.stereotype.Service;

import com.d3ai.backend.config.JwtService;
import com.d3ai.backend.user.Role;
import com.d3ai.backend.user.User;
import com.d3ai.backend.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuth2AuthenticationService {
    private final UserRepository repository;
    private final JwtService jwtService;

    // This service will only be responsible for handling OAuth2 users
    public AuthenticationResponse authenticateOAuth2User(String email, String firstName, String lastName) {
        var existingUser = repository.findByEmail(email);
        String fullname = firstName + " " + lastName;

        if (existingUser.isPresent()) {
            var jwtToken = jwtService.generateToken(existingUser.get());
           
            
            String stripeCustomerId = existingUser.get().getStripeCustomerId();
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .message("User authenticated via OAuth2. Login successful.")
                    .email(email)
                    .fullname(fullname)
                    .stripeCustomerId(stripeCustomerId)
                    .build();
        }

        var user = User.builder()
                .email(email)
                .firstname(firstName)
                .lastname(lastName)
                .password("GoogleUser")  // OAuth2 users generally don't need a password
                .role(Role.USER)
                .stripeCustomerId("Non Stripe User")
                .build();
        repository.save(user);

        var jwtToken = jwtService.generateToken(user);
       
       
        var stripeCustomerId = user.getStripeCustomerId();
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .message("New user registered via OAuth2 and authenticated.")
                .email(email)
                .fullname(fullname)
                .stripeCustomerId(stripeCustomerId)
                .build();
    }
}
