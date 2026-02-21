package com.notes.notesplatform.service;

import com.notes.notesplatform.model.User;
import com.notes.notesplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable; // Add this
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    // This "users" cache stores the UserDetails object using email as the key
    @Cacheable(value = "users", key = "#email") 
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // This print statement will now only appear ONCE in our logs per user session
        System.out.println("Database hit for user: " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        String role = user.getRole();
        if (role == null || role.trim().isEmpty()) {
            role = "USER";
        }

        String finalRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;

        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(finalRole)
                .build();
    }
}
