package com.classmonster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

// JWT 로 직접 인증하므로 Spring Boot 기본 인메모리 사용자(임시 비밀번호)는 만들지 않는다.
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@ConfigurationPropertiesScan
public class ClassTaskMonsterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClassTaskMonsterApplication.class, args);
    }
}
