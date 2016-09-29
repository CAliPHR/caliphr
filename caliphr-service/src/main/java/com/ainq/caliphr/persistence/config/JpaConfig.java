package com.ainq.caliphr.persistence.config;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import com.ainq.caliphr.persistence.config.Constants.MainDatasourceProperty;
import com.ainq.caliphr.persistence.util.DatabaseEncyptionUtil;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.ainq.caliphr.persistence.model.obj.caliphrDb.repository")
@EntityScan(basePackages="com.ainq.caliphr.persistence.model.obj.caliphrDb")
@EnableCaching
public class JpaConfig {
	
	@Autowired
	private DatabaseEncyptionUtil databaseEncryptionUtil;
	
	@Autowired
	private ConfigurableEnvironment environment;
	

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(entityManagerFactory().getObject());
        return tm;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor persistenceManager() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean(destroyMethod = "shutdown")
    public HikariDataSource mainDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(environment.getRequiredProperty(MainDatasourceProperty.MAIN_DATASOURCE_JDBC_URL));
        dataSource.setUsername(environment.getRequiredProperty(MainDatasourceProperty.MAIN_DATASOURCE_USERNAME));
        
        String password = environment.getProperty(MainDatasourceProperty.MAIN_DATASOURCE_ENCRYPTED_PASSWORD);
		if (!StringUtils.isBlank(password)) {
			password = databaseEncryptionUtil.decryptPassword(password);
		}
		else {
			password = environment.getProperty(MainDatasourceProperty.MAIN_DATASOURCE_PLAINTEXT_PASSWORD);
		}
		if (StringUtils.isBlank(password)) {
			throw new IllegalStateException("either MAIN.DATASOURCE.PLAINTEXT.PASSWORD or MAIN.DATASOURCE.ENCRYPTED.PASSWORD must be specified");
		}
        
        dataSource.setPassword(password);
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setMaximumPoolSize(40);
        dataSource.setAutoCommit(true);
        return dataSource;
    }
    
    @Bean
    public LazyConnectionDataSourceProxy dataSource() {
        LazyConnectionDataSourceProxy ds = new LazyConnectionDataSourceProxy();
        ds.setTargetDataSource(mainDataSource());
        return ds;
    }

    @Bean
    public HibernateJpaVendorAdapter vendorAdapter()
    {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabasePlatform("");
        return vendorAdapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPersistenceUnitName("caliphr-persistence");
        em.setDataSource(dataSource());
        em.setJpaVendorAdapter(vendorAdapter());
        em.setJpaProperties(additionalProperties());

        return em;
    }

    @Bean
    public EhCacheManagerFactoryBean ehCacheCacheManager()
    {
	  // use the same ehcache configuration as the hibernate second level cache set up in the JPA config additionalProperties()
    	EhCacheManagerFactoryBean cacheManagerFactoryBean = new EhCacheManagerFactoryBean();
    	cacheManagerFactoryBean.setCacheManagerName("persistenceEhcacheModel");
    	cacheManagerFactoryBean.setAcceptExisting(true);
    	return cacheManagerFactoryBean;
    }
    
  	@Bean(name="cacheManager")
    public EhCacheCacheManager ehCacheManager() {
    	return new EhCacheCacheManager(ehCacheCacheManager().getObject());
    }

    private Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect","org.hibernate.dialect.PostgreSQL9Dialect");
        properties.setProperty("hibernate.flush_before_completion","true");
        properties.setProperty("hibernate.generate_statistics", "false");
//        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.current_session_context_class","org.springframework.orm.hibernate4.SpringSessionContext");
        properties.setProperty("hibernate.id.new_generator_mappings", "true");
        properties.setProperty("hibernate.id.optimizer.pooled.prefer_lo","true");
        properties.setProperty("hibernate.cache.use_second_level_cache", "true");
        properties.setProperty("hibernate.cache.use_query_cache", "true");
        properties.setProperty("javax.persistence.sharedCache.mode","ENABLE_SELECTIVE");
        properties.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory");
        properties.setProperty("net.sf.ehcache.configurationResourceName", "ehcache-model.xml");
        return properties;
    }

    //
    // Mailer configuration

    @Bean
    public ClassLoaderTemplateResolver xmlTemplateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setTemplateMode("XML");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setOrder(1);
        return templateResolver;
    }

    @Bean
    public ClassLoaderTemplateResolver emailTemplateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/email/");
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setOrder(1);
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        Set<ITemplateResolver> templatesResolvers = new HashSet<>();
        templatesResolvers.add(emailTemplateResolver());
        templatesResolvers.add(xmlTemplateResolver());
        templateEngine.setTemplateResolvers(templatesResolvers);
        return templateEngine;
    }

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(environment.getProperty(Constants.SmtpProperty.SMTP_HOST));
        mailSender.setPort(Integer.parseInt(environment.getProperty(Constants.SmtpProperty.SMTP_PORT)));
        mailSender.setUsername(environment.getProperty(Constants.SmtpProperty.SMTP_USER));
        mailSender.setPassword(environment.getProperty(Constants.SmtpProperty.SMTP_PASS));
        mailSender.setJavaMailProperties(mailProperties());
        return mailSender;
    }

    private Properties mailProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", environment.getProperty(Constants.SmtpProperty.SMTP_PROTOCOL));
        properties.setProperty("mail.smtp.auth", environment.getProperty(Constants.SmtpProperty.SMTP_AUTH));
        properties.setProperty("mail.smtp.starttls.enable", environment.getProperty(Constants.SmtpProperty.SMTP_STARTTLS));
        properties.setProperty("mail.debug", environment.getProperty(Constants.SmtpProperty.SMTP_DEBUG));
        return properties;
    }


}
