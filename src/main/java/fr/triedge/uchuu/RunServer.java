package fr.triedge.uchuu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.util.Properties;

@SpringBootApplication
public class RunServer extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(RunServer.class);
        Properties prop = new Properties();
        prop.setProperty("springdoc.api-docs.path","/api-docs");
        app.setDefaultProperties(prop);
        app.run(args);
    }
}
