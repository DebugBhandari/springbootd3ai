package com.d3ai.backend.stripe;

import com.stripe.model.checkout.Session;
import com.stripe.Stripe;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.stripe.param.checkout.SessionCreateParams;
import com.d3ai.backend.user.User;
import com.d3ai.backend.user.UserRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class StripeController {

    private final UserRepository userRepository;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;
    
    @Value("${stripe.price.id}")
    private String stripePriceId;
    
    @Value("${base.url}")
    private String baseUrl;

    @Value("${client.url}")
    private String clientUrl;

    @PostConstruct
    public void StripeController() {
        Stripe.apiKey = stripeSecretKey;
    }
    
   
    

    @GetMapping("/prebook")
    public ResponseEntity<Map<String, String>> prebook() throws Exception {
    	 // Retrieve the logged-in user's email from Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUserEmail = null;

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                loggedInUserEmail = ((UserDetails) principal).getUsername(); // Assuming email is the username
            } else if (principal instanceof String) {
                loggedInUserEmail = (String) principal; // For cases where principal is directly the email
            }
        }

        if (loggedInUserEmail == null) {
            throw new RuntimeException("Logged-in user's email could not be retrieved");
        }

		// Create Stripe Checkout session
        Session session = Session.create(
            new com.stripe.param.checkout.SessionCreateParams.Builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addLineItem(
                    new SessionCreateParams.LineItem.Builder()
                        .setPrice(stripePriceId)
                        .setQuantity(1L)
                        .build()
                )
                .setCustomerCreation(SessionCreateParams.CustomerCreation.ALWAYS)
                .setSuccessUrl(baseUrl + "/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(baseUrl + "/cancel")
                .setCustomerEmail(loggedInUserEmail)
                .build()
        );
        
       

        // Return a JSON response with the session URL
        Map<String, String> response = new HashMap<>();
        response.put("url", session.getUrl());
       
        return ResponseEntity.ok(response);
    }
    @GetMapping("/success")
    public RedirectView success(@RequestParam("session_id") String sessionId) throws Exception {
        // Retrieve the session from Stripe
        Session session = Session.retrieve(sessionId);
        
       

        // Get the customer email from the session
        String email = session.getCustomerDetails().getEmail();
        System.out.println("Customer Email: " + email);

        // Fetch user from the database based on email
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            System.err.println("User not found");
            return new RedirectView(clientUrl); // Redirect back to client if user not found
        }

        User user = userOptional.get();

        // Update user with the Stripe customer ID
        String stripeCustomerId = session.getCustomer(); // Get Stripe customer ID
        updateUserWithStripe(user.getId(), stripeCustomerId);

        return new RedirectView(clientUrl+"/orders?stripeCustomerId="+stripeCustomerId); // Redirect back to the client
    }

    @GetMapping("/cancel")
    public RedirectView cancel() {
        return new RedirectView(clientUrl);
    }

    // Method to update the user in the database with the Stripe customer ID
    private void updateUserWithStripe(Integer userId, String stripeCustomerId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setStripeCustomerId(stripeCustomerId); // Update the Stripe customer ID
            userRepository.save(user); // Save the updated user
            System.out.println("User updated with Stripe Customer ID: " + stripeCustomerId);
        });
    }
}
