package com.d3ai.backend.auth;

import java.util.Optional;

import com.d3ai.backend.user.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    private String token;
    private String message;
    private String email;
    private String fullname;
    private String stripeCustomerId;
}
