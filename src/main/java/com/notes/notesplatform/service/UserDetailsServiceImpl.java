package com.notes.notesplatform.service;

import com.notes.notesplatform.model.User;
import com.notes.notesplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User.UserBuilder;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
/*    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (!user.isVerified()) {
            throw new UsernameNotFoundException("User email not verified");
        }

        String role=user.getRole();
        if (role == null || role.trim().isEmpty()) {
            role = "USER";
        }

        UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(user.getEmail());
        builder.password(user.getPassword());
        builder.password(user.getPassword());
           builder.roles("USER"); 

        return builder.build();
    }*/

        @Override
public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

    String role = user.getRole();
    if (role == null || role.trim().isEmpty()) {
        role = "USER";
    }

    // Ensure it looks like ROLE_ADMIN or ROLE_USER
    String finalRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;

    return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
            .password(user.getPassword())
            .authorities(finalRole) // This sets the authority correctly
            .build();
}
}
