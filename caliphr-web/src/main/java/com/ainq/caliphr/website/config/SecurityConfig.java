package com.ainq.caliphr.website.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.ainq.caliphr.website.service.security.LoginSuccessHandler;
import com.ainq.caliphr.website.service.security.SecurityRole;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder registry) throws Exception {
        registry.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new LoginSuccessHandler("/web/auth/confirm");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // Ensure HTTPS
        http.requiresChannel().anyRequest().requiresSecure();

        // Ensure the session is killed if browser is closed!
        http.sessionManagement().sessionFixation().none();

        http.authorizeRequests()

                /*
                    Need to leave off the "ROLE_" prefix for the ant matchers, since Spring Security automatically prepends the string.
                 */

                // Admin Requests (if we have any...)
                .antMatchers("/admin/**").hasRole(SecurityRole.ROLE_ADMIN.getSpringSecurityKey())

                // Utility pages (for testing)
                .antMatchers("/util/**").hasRole(SecurityRole.ROLE_TESTER.getSpringSecurityKey())

                // any AJAX request should be locked down
                .antMatchers("/api/**").hasRole(SecurityRole.ROLE_USER.getSpringSecurityKey())

                // qrda 1/3 export
                .antMatchers("/extract/**").hasRole(SecurityRole.ROLE_USER.getSpringSecurityKey())

                // Open requests
                .antMatchers("/about").permitAll()
                .antMatchers("/contact").permitAll()
                .antMatchers("/").permitAll()
                .antMatchers("/faq").permitAll()

                // Application user requests
                .antMatchers("/web/auth/confirm").hasRole(SecurityRole.ROLE_USER.getSpringSecurityKey())
                .antMatchers("/web/auth/password/reset").permitAll()
                .antMatchers("/web/auth/password/success").permitAll()
                .and()

                // Login form
                .formLogin()
                    .loginPage("/web/auth/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler(successHandler())
                        .permitAll()
                        .and()
                    .logout()
                    	.logoutRequestMatcher(new AntPathRequestMatcher("/web/auth/logout"))
                        .logoutSuccessUrl("/web/auth/logout/success")
                .permitAll();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}