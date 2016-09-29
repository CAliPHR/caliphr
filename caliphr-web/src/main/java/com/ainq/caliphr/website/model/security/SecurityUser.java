package com.ainq.caliphr.website.model.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ainq.caliphr.common.model.security.ApplicationUser;
import com.ainq.caliphr.common.model.security.ApplicationUserSecurity;
import com.ainq.caliphr.website.service.security.SecurityRole;

public class SecurityUser extends ApplicationUser implements UserDetails {

    private static final long serialVersionUID = 1L;

    public SecurityUser(ApplicationUser user) {
        if (user != null) {
            this.setId(user.getId());
            this.setFirstName(user.getFirstName());
            this.setLastName(user.getLastName());
            this.setEmailAddress(user.getEmailAddress());
            this.setPasswordHash(user.getPasswordHash());
            this.setDateLastLogin(user.getDateLastLogin());
            this.setApplicationUserSecurities(user.getApplicationUserSecurities());
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        //
        //  Spring security requires roles to be prefixed with ROLE_...

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        Set<ApplicationUserSecurity> userRoles = this.getApplicationUserSecurities();
        if (userRoles == null || userRoles.size() == 0) {
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(SecurityRole.ROLE_PREFIX + SecurityRole.ROLE_USER.getSpringSecurityKey());
            authorities.add(authority);
        }
        else {
            // Skip over disabled records...
            userRoles.stream().filter(role -> role.getDateDisabled() == null).forEach(role -> {
                for (SecurityRole securityRole : SecurityRole.values()) {
                    if (securityRole.getRecordId().equals(role.getRole().getId())) {
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(SecurityRole.ROLE_PREFIX + securityRole.getSpringSecurityKey());
                        authorities.add(authority);
                    }
                }
            });

            // Add generic "user" role as well
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(SecurityRole.ROLE_PREFIX + SecurityRole.ROLE_USER.getSpringSecurityKey());
            authorities.add(authority);
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return super.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return super.getEmailAddress();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}