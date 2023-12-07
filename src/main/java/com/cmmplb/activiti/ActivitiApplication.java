package com.cmmplb.activiti;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

/**
 * @author penglibo
 * @createdate 2021-03-20
 */

@Slf4j
@SpringBootApplication
public class ActivitiApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(ActivitiApplication.class);
        ConfigurableApplicationContext context = builder.build().run(args);
        Environment env = context.getEnvironment();
        String serverPort = env.getProperty("server.port");
        log.info("\n----------------------------------------------------------\n" +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "Local: \t\t\thttp://localhost:{}\n\t" +
                        "Swagger-Doc: \thttp://localhost:{}/doc.html\n\t" +
                        "Profile(s): {}\n----------------------------------------------------------"
                , env.getProperty("spring.application.name"), serverPort, serverPort, env.getActiveProfiles()
        );
    }
}