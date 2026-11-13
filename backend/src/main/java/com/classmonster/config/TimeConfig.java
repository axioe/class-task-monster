package com.classmonster.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;
import java.util.random.RandomGenerator;

/**
 * "오늘" 판단과 무작위 먹이 지급에 쓰는 공통 빈.
 * 테스트에서 고정된 시각/난수로 바꿀 수 있도록 빈으로 분리했다.
 */
@Configuration
public class TimeConfig {

    @Bean
    public Clock clock(AppProperties appProperties) {
        return Clock.system(ZoneId.of(appProperties.timezone()));
    }

    @Bean
    public RandomGenerator randomGenerator() {
        return RandomGenerator.getDefault();
    }
}
