package com.d3ai.backend.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
	@NotNull(message = "Email is mandatory")
	@Email(message = "Invalid email format")
    @NotBlank(message = "Email is mandatory")
    private String email;
	@NotNull(message = "Password is mandatory")
    @NotBlank(message = "Password is mandatory")
    String password;
}
