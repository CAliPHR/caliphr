package com.ainq.caliphr.website.config;

import java.io.IOException;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import com.ainq.caliphr.common.util.yaml.YamlApplicationContextInitializerUtil;

public class WebsiteYamlFileApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {

		try {
			YamlApplicationContextInitializerUtil.loadYamlFileIfExists("classpath:config/website.yaml", applicationContext);

			for (String profile : applicationContext.getEnvironment().getActiveProfiles()) {
				YamlApplicationContextInitializerUtil.loadYamlFileIfExists("classpath:config/website-" + profile + ".yaml", applicationContext);
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
