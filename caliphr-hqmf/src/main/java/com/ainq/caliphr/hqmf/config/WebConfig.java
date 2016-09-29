package com.ainq.caliphr.hqmf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.ainq.caliphr.hqmf.config.intercept.SessionHandler;

@EnableWebMvc
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Bean
    public SessionHandler sessionHandler() { return new SessionHandler(); }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Expire session after a period of time
        registry.addInterceptor(sessionHandler());
    }

}