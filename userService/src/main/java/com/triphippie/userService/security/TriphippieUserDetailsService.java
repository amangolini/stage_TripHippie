package com.triphippie.userService.security;

import com.triphippie.userService.model.user.Role;
import com.triphippie.userService.model.user.User;
import com.triphippie.userService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class TriphippieUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public TriphippieUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private static Collection<? extends GrantedAuthority> mapToAuthorities(Role role) {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        /*return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), mapToAuthorities(user.getRole()));*/
        return new TriphippieUserDetails(
                user.getId(), user.getUsername(), user.getPassword(), mapToAuthorities(user.getRole())
        );
    }
}
