package com.ainq.caliphr.hqmf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

@Configuration
@ComponentScan(basePackages = {"com.ainq.caliphr.hqmf.service, com.ainq.caliphr.hqmf.model, com.ainq.caliphr.hqmf.controller, com.ainq.caliphr.hqmf.util, com.ainq.caliphr.persistence"})
public class ApplicationConfig {

    @Bean
    public SpelAwareProxyProjectionFactory projectionFactory() {
        return new SpelAwareProxyProjectionFactory();
    }

}