package com.d3ai.backend.config;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.d3ai.backend.user.CustomOAuth2UserService;
import com.d3ai.backend.user.OAuth2LoginSuccessHandler;

import org.springframework.security.config.Customizer;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    
  

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults -> {})
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                		 // Allow authentication for public API routes (like login, registration, etc.)
                    .requestMatchers("/api/v1/auth/**", "/loginPage","/login", "/registerPage", "/success", "/cancel", "/orders", "/oauth2/**").permitAll()

                        // Allow static files and the index.html page for React
                    .requestMatchers("/", "/index.html","/assets/**",  "/static/**", "/favicon.ico", "/manifest.json").permitAll()

                        // React frontend routes; adjust as needed for your routes
                   
                    .anyRequest().authenticated()
                    )
                .oauth2Login(oauth2 -> oauth2
                		.loginPage("/oauth2/authorization/google") // Default login page
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService)) // Custom user service
                        .successHandler(oAuth2LoginSuccessHandler) 
                    )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
 

}
