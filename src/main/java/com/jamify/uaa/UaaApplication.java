package com.jamify.uaa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@EnableConfigurationProperties({LiquibaseProperties.class})
public class UaaApplication {
    private static final Logger log = LoggerFactory.getLogger(UaaApplication.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(UaaApplication.class);
        ConfigurableEnvironment env = app.run(args).getEnvironment();

        String protocol = "http";
        String port = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "/");

        // Get container IP address if available
        String hostAddress;
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            hostAddress = "localhost";
        }

        log.info("""
                        
                        ----------------------------------------------------------
                        Application '{}' is running!
                        
                        Local URLs:
                        - Local:\t\t{}://localhost:{}{}
                        - Host:\t{}://{}:{}{}
                        - Swagger UI:\t{}://localhost:{}/swagger-ui.html
                        
                        Active profiles: {}
                        ----------------------------------------------------------""",
                env.getProperty("spring.application.name"),
                protocol,
                port,
                contextPath,
                protocol,
                hostAddress,
                port,
                contextPath,
                protocol,
                port,
                String.join(", ", env.getActiveProfiles())
        );
    }
}