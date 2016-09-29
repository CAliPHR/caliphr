package com.ainq.caliphr.common.util.yaml;

import java.io.IOException;

import org.slf4j.LoggerFactory;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

import ch.qos.logback.classic.Logger;

public class YamlApplicationContextInitializerUtil {
	
	private final static Logger logger = (Logger) LoggerFactory.getLogger(YamlApplicationContextInitializerUtil.class);
	
	public static void loadYamlFileIfExists(String resourceName, ConfigurableApplicationContext applicationContext) throws IOException {

		Resource resource = applicationContext.getResource(resourceName);
		if (resource.exists()) {
			loadYamlResource(applicationContext, resource, null);
			for (String profile : applicationContext.getEnvironment().getActiveProfiles()) {
				loadYamlResource(applicationContext, resource, profile);
			}
		}
	}

	public static void loadYamlResource(ConfigurableApplicationContext applicationContext, Resource resource, String profile)
			throws IOException {
		
		String profileMsg = profile != null ? " (for profile " + profile + ")" : "";
		
		YamlPropertySourceLoader sourceLoader = new YamlPropertySourceLoader();
		PropertySource<?> yamlTestProperties = sourceLoader.load(resource.getDescription() + profileMsg, resource, profile);
		if (yamlTestProperties != null) {
			applicationContext.getEnvironment().getPropertySources().addFirst(yamlTestProperties);
			logger.debug("loaded " + resource + profileMsg);
		}
	}

}
