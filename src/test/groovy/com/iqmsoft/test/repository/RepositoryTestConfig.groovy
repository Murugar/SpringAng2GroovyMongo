package com.iqmsoft.test.repository

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories("com.iqmsoft.mongo.repository")
//@EnableJpaRepositories("com.iqmsoft.mongo.repository")
@EntityScan("com.iqmsoft.domain")
@EnableAutoConfiguration(exclude = [FlywayAutoConfiguration])
class RepositoryTestConfig {
}
