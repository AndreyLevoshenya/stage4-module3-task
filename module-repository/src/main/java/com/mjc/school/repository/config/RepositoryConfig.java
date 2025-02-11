package com.mjc.school.repository.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories
@EnableTransactionManagement
@EntityScan(basePackages = "com.mjc.school.repository.model")
@PropertySource("classpath:application-repository.properties")
public class RepositoryConfig {
}
