package com.d3ai.backend.user;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.d3ai.backend.config.JwtService;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

	 private final JwtService jwtService;

	    @Override
	    public void onAuthenticationSuccess(HttpServletRequest request, 
	                                        HttpServletResponse response, 
	                                        Authentication authentication) throws IOException, ServletException, java.io.IOException {

	        // Cast the authentication principal to CustomOAuth2User
	        if (authentication.getPrincipal() instanceof CustomOAuth2User) {
	            CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

	            // Retrieve custom attributes (JWT token and message)
	            String jwtToken = (String) customOAuth2User.getAttributes().get("jwtToken");
	            String message = (String) customOAuth2User.getAttributes().get("message");
	            String email = (String) customOAuth2User.getAttributes().get("email");
	            String fullname = (String) customOAuth2User.getAttributes().get("fullname");
	            String stripeCustomerId = (String) customOAuth2User.getAttributes().get("stripe_customer_id");

	            // You can now use the JWT token (e.g., send it in a redirect or response)
	            System.out.println("JWT Token: " + jwtToken);
	            System.out.println("Message: " + message);
	            System.out.println("Email: " + email);
	            System.out.println("Fullname: " + fullname);
	            System.out.println("stripeCustomerId: " + stripeCustomerId);

	            // Redirect with the token as a query parameter
	            response.sendRedirect("/?token=" + jwtToken + "&message=" + message + "&email=" + email + "&fullname=" + fullname + "&stripeCustomerId=" + stripeCustomerId);
	        } else {
	            // Fallback if the principal is not of type CustomOAuth2User
	            response.sendRedirect("/");
	        }
	    }
	}