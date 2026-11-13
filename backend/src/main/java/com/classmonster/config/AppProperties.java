package com.classmonster.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * application.yml 의 app.* 설정.
 */
@ConfigurationProperties(prefix = "app")
public record AppProperties(String timezone, Cors cors, Monster monster, boolean seedData) {

    public record Cors(List<String> allowedOrigins) {
    }

    /** energyPerFood: 먹이 1개당 오르는 성장 에너지 (기본 1:1) */
    public record Monster(int energyPerFood) {
    }
}
