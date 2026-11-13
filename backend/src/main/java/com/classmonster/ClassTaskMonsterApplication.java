package com.classmonster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ClassTaskMonsterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClassTaskMonsterApplication.class, args);
    }
}
