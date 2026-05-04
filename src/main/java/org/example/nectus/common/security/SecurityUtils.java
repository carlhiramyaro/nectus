package org.example.nectus.common.security;

import org.example.nectus.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private SecurityUtils(){}

    public static User getCurrentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()){
            throw new RuntimeException("No authenticated user found");
        }

        return (User) auth.getPrincipal();

    }
}
