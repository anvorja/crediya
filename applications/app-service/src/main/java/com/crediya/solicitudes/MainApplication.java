package com.crediya.solicitudes;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
    // Bean Registrados - DEBUG
    @Bean
    public CommandLineRunner debugBeans(ApplicationContext ctx) {
        return args -> {
            System.out.println("=== BEANS REGISTRADOS ===");
            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                if (beanName.contains("UseCase") || beanName.contains("Solicitud")) {
                    Object bean = ctx.getBean(beanName);
                    System.out.println("Bean: " + beanName + " -> " + bean.getClass().getSimpleName());
                }
            }
            System.out.println("=========================");
        };
    }
}
