package com.microservice.test.test_app.context;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.runner.RuleBookRunner;

@Configuration
@ComponentScan(basePackages = { "com.microservice.test.test_app.*" })
@EnableJpaRepositories(basePackages = { "com.microservice.test.test_app.repository" })
@PropertySource(value = { "classpath:application.properties" })
public class SpringConfiguration {

	@Autowired
	private Environment env;

	@Bean
	@Autowired
	public DataSource dataSource(DatabasePopulator populator) {
		final DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
		dataSource.setUrl(env.getProperty("jdbc.url"));
		dataSource.setUsername(env.getProperty("jdbc.username"));
		dataSource.setPassword(env.getProperty("jdbc.password"));
		DatabasePopulatorUtils.execute(populator, dataSource);
		return dataSource;
	}

	@Bean
	@Autowired
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(final DataSource dataSource) {
		final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource);
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setGenerateDdl(Boolean.TRUE);
		vendorAdapter.setShowSql(Boolean.TRUE);
		factory.setDataSource(dataSource);
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPackagesToScan("com.microservice.test.test_app.entity");
		Properties jpaProperties = new Properties();
		jpaProperties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
		jpaProperties.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
		factory.setJpaProperties(jpaProperties);
		return factory;
	}

	@Bean
	@Autowired
	public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory.getObject());
	}

	@Bean
	@Autowired
	public DatabasePopulator databasePopulator() {
		final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.setContinueOnError(false);
		populator.addScript(new ClassPathResource("products.sql"));
		return populator;
	}

	@Bean
	@Autowired
	public RuleBook<?> ruleBook() {
		RuleBook<?> ruleBook = new RuleBookRunner("com.microservice.test.test_app.rules");
		return ruleBook;
	}
}