package com.d3ai.backend.user;

import org.springframework.security.oauth2.core.user.DefaultOAuth2User;


import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class CustomOAuth2User extends DefaultOAuth2User {
    private final Map<String, Object> customAttributes;

    // Constructor
    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes,
                            String nameAttributeKey) {
        super(authorities, attributes, nameAttributeKey);
        this.customAttributes = new HashMap<>(attributes); // Make attributes modifiable
    }

    // Override getAttributes to include custom attributes
    @Override
    public Map<String, Object> getAttributes() {
        return customAttributes;
    }

    // Method to add custom attributes dynamically
    public void addAttribute(String key, Object value) {
        customAttributes.put(key, value);
    }
}