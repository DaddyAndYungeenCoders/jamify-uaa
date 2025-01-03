package com.jamify.uaa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.ConfigurableEnvironment;

@SpringBootApplication
@EnableConfigurationProperties({LiquibaseProperties.class})
//@EnableOAuth2Client
public class UaaApplication {
	private static final Logger log = LoggerFactory.getLogger(UaaApplication.class);

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(UaaApplication.class);

		ConfigurableEnvironment env = app.run(args).getEnvironment();

		String protocol = "http";
		String host = "localhost";
		String port = env.getProperty("server.port");
		String contextPath = env.getProperty("server.servlet.context-path");
		if (contextPath == null || contextPath.isBlank()) {
			contextPath = "/";
		}
		log.info("""

                        ----------------------------------------------------------
                        \t\
                        Application '{}' is running ! Access URLs:\s
                        \t\
                        Local: \t\t{}://localhost:{}{}
                        \t\
                        External: \t{}://{}:{}{}
                        \t\
                        API Docs: \t{}://localhost:{}/swagger-ui.html
                        ----------------------------------------------------------
                        """,
				env.getProperty("spring.application.name"),
				protocol,
				port,
				contextPath,
				protocol,
				host,
				port,
				contextPath,
				protocol,
				port
		);
	}

}
