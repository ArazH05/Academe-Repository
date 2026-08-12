package com.arazhafez.academe.security;

import com.arazhafez.academe.entity.User;
import com.arazhafez.academe.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    //Repository used to look up users
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        //Find the user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        //Convert our User entity into Spring Security's UserDetails object
        return new org.springframework.security.core.userdetails.User(

                user.getEmail(),
                user.getPassword(),

                //Spring Security expects authorities like ROLE_ADMIN
                List.of(new SimpleGrantedAuthority(
                        "ROLE_" + user.getRole().name()
                ))
        );
    }
}