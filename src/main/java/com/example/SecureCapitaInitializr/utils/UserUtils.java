package com.example.SecureCapitaInitializr.utils;

import com.example.SecureCapitaInitializr.models.user.UserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtils {

    public static Long getCurrentUserId() {
        return ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getId();
    }
}
