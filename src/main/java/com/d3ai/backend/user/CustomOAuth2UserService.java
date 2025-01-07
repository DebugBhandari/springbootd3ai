package com.d3ai.backend.user;


import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import com.d3ai.backend.user.CustomOAuth2User;
import org.springframework.stereotype.Service;

import com.d3ai.backend.auth.AuthenticationResponse;
import com.d3ai.backend.auth.OAuth2AuthenticationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

   

	private final OAuth2AuthenticationService oauth2AuthenticationService;
    
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = delegate.loadUser(userRequest);
        
        String email = oauth2User.getAttribute("email");
        String firstName = oauth2User.getAttribute("given_name");
        String lastName = oauth2User.getAttribute("family_name");

        // Authenticate the user and get the JWT token
        AuthenticationResponse response = oauth2AuthenticationService.authenticateOAuth2User(email, firstName, lastName);

        // Wrap the original OAuth2User in the CustomOAuth2User class
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(
        		 oauth2User.getAuthorities(), // Pass user authorities
        	        oauth2User.getAttributes(),  // Pass user attributes
        	        "email"                      // Specify the key for the principal name
        );
        
        // Add the JWT token as a custom attribute
        customOAuth2User.addAttribute("jwtToken", response.getToken());
        customOAuth2User.addAttribute("message", response.getMessage());
        customOAuth2User.addAttribute("fullname", response.getFullname());
        customOAuth2User.addAttribute("stripe_customer_id", response.getStripeCustomerId());

        return customOAuth2User;
    }
}