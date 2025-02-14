package com.flawlessyou.backend.Security.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.flawlessyou.backend.entity.user.User;
import com.flawlessyou.backend.entity.user.UserService;
import java.util.concurrent.ExecutionException;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService; 
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
            
            return UserDetailsImpl.build(user);
            
        } catch (ExecutionException | InterruptedException e) {
            throw new UsernameNotFoundException("Error accessing database", e);
        }
    }
}