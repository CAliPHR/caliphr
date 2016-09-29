package com.ainq.caliphr.website.utility;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.ainq.caliphr.website.model.security.SecurityUser;

/**
 * Created by mmelusky on 9/8/2015.
 */
public class SecurityHelper {
    /**
     * Returns the domain User object for the currently logged in user, or null
     * if no User is logged in.
     *
     * @return User object for the currently logged in user, or null if no User
     * is logged in.
     */
    public static UserDetails getUserDetails() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof SecurityUser) {
                return (SecurityUser) principal;
            } else {
                return null;
            }
        } catch (Exception ex) {
            // If security context holder ever fails
            return null;
        }
    }

    /**
     * Utility method to determine if the current user is logged in /
     * authenticated.
     * <p>
     * Equivalent of calling:
     * <p>
     * <code>getCurrentUser() != null</code>
     *
     * @return if user is logged in
     */
    public static boolean isLoggedIn() {
        return getUserDetails() != null;
    }

    /**
     * Creates a password hash for storage.
     *
     * @param newPassword
     * @return hash
     */
    public static String createPasswordHash(String newPassword) {
        return new BCryptPasswordEncoder().encode(newPassword);
    }

    public static Boolean passwordHasMatches(String rawPassword, String encodedPassword) {
        return new BCryptPasswordEncoder().matches(rawPassword, encodedPassword);
    }
}
