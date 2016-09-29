package com.ainq.caliphr.website.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.web.servlet.config.annotation.*;

import com.ainq.caliphr.website.config.intercept.BaseInterceptor;
import com.ainq.caliphr.website.config.intercept.SessionHandler;

@EnableWebMvc
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Bean
    public SpelAwareProxyProjectionFactory projectionFactory() {
        return new SpelAwareProxyProjectionFactory();
    }

    @Bean
    public SessionHandler sessionHandler() {
        return new SessionHandler();
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/web/auth/login").setViewName("auth/login");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations(
                "/resources/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // Utility interceptor which helps with the "active" link styles in the navigation.  --mm
        registry.addInterceptor(new BaseInterceptor());

        // Expire session after a period of time
        registry.addInterceptor(sessionHandler());
    }
}