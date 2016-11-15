package com.iqmsoft.config;

import lombok.Data;

import java.io.File;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@EnableConfigurationProperties
@EntityScan(basePackages = { "com.iqmsoft.domain" })
@ConfigurationProperties(prefix = AppConfig.PREFIX)
@EnableMongoRepositories(basePackages = {"com.iqmsoft.mongo.repository"})
@EnableJpaRepositories("com.iqmsoft.mongo.repository")
@Data
public class AppConfig {

    public static final String PREFIX = "iqmsoft";

    private String assetHost;
    private String assetManifestUrl;
    
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return new EmbeddedServletContainerCustomizer() {
            @Override
            public void customize(ConfigurableEmbeddedServletContainer container) {
                container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/index.html"));
                
                //container.setDocumentRoot(new File("src/main/resources/static"));
                
            }
        };
    }
}
