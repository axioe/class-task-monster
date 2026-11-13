package com.classmonster;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * docs/sql/maintenance.sql 의 모든 문장이 실제 스키마(PostgreSQL 17 + Flyway)에서 실행되는지 검사한다.
 * 테이블 구조를 바꿨는데 문서의 SQL 을 고치지 않으면 이 테스트가 실패한다.
 * 테스트 트랜잭션은 끝나면 롤백되어 데이터가 남지 않는다.
 */
@SpringBootTest
@ActiveProfiles("prod")
@Transactional
class MaintenanceSqlTest {

    private static final Path SQL_FILE = Path.of("..", "docs", "sql", "maintenance.sql");
    private static final Pattern PARAMETER = Pattern.compile("(?<!:):([a-z_]+)");

    /** DBeaver 에서 입력하는 :파라미터 대신 넣을 샘플 값 (샘플 데이터 기준) */
    private static final Map<String, String> SAMPLE_VALUES = Map.ofEntries(
            Map.entry("classroom_id", "1"),
            Map.entry("student_id", "2"),
            Map.entry("assignment_id", "1"),
            Map.entry("mission_id", "1"),
            Map.entry("option_id", "1"),
            Map.entry("energy", "12"),
            Map.entry("new_name", "'5학년 2반'"),
            Map.entry("new_monster_name", "'몽글이'"),
            Map.entry("new_nickname", "'지우'"),
            Map.entry("comment", "'좋아요'"),
            Map.entry("message", "'테스트 소식'"),
            Map.entry("mission_date", "'2099-01-01'"),
            Map.entry("situation", "'테스트 상황'"),
            Map.entry("expression", "'Hello!'"),
            Map.entry("is_correct", "'true'"));

    private static final EmbeddedPostgres POSTGRES = startPostgres();

    @Autowired
    JdbcTemplate jdbcTemplate;

    private static EmbeddedPostgres startPostgres() {
        try {
            return EmbeddedPostgres.builder().start();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> POSTGRES.getJdbcUrl("postgres", "postgres"));
        registry.add("spring.datasource.username", () -> "postgres");
        registry.add("spring.datasource.password", () -> "postgres");
        registry.add("app.seed-data", () -> "true");
    }

    @AfterAll
    static void stopPostgres() throws IOException {
        POSTGRES.close();
    }

    @Test
    @DisplayName("관리용 SQL 모음의 모든 문장이 현재 스키마에서 실행된다")
    void allMaintenanceStatementsRun() throws IOException {
        List<String> statements = readStatements();
        assertThat(statements).hasSizeGreaterThan(10);

        for (String statement : statements) {
            String sql = fillParameters(statement);
            assertThatCode(() -> jdbcTemplate.execute(sql))
                    .as("실행 실패한 SQL:%n%s", statement)
                    .doesNotThrowAnyException();
        }
    }

    @Test
    @DisplayName("샘플 데이터는 일관성 점검 쿼리를 모두 통과한다")
    void seedDataPassesConsistencyChecks() {
        assertThat(jdbcTemplate.queryForList("""
                SELECT id FROM classroom
                WHERE stage <> CASE WHEN growth_energy >= 30 THEN 'GROWN' WHEN growth_energy >= 10 THEN 'BABY' ELSE 'EGG' END
                """)).isEmpty();
        assertThat(jdbcTemplate.queryForList("""
                SELECT m.id FROM expression_mission m
                WHERE NOT EXISTS (SELECT 1 FROM expression_option o WHERE o.mission_id = m.id AND o.correct)
                """)).isEmpty();
    }

    /** 주석을 제거하고 ; 로 끝나는 문장 단위로 나눈다. */
    private List<String> readStatements() throws IOException {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String line : Files.readAllLines(SQL_FILE, StandardCharsets.UTF_8)) {
            String trimmed = line.strip();
            if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                continue;
            }
            current.append(line).append('\n');
            if (trimmed.endsWith(";")) {
                statements.add(current.toString().strip().replaceAll(";$", ""));
                current.setLength(0);
            }
        }
        return statements;
    }

    private String fillParameters(String statement) {
        Matcher matcher = PARAMETER.matcher(statement);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String value = SAMPLE_VALUES.get(matcher.group(1));
            assertThat(value).as("샘플 값이 없는 파라미터 :%s", matcher.group(1)).isNotNull();
            matcher.appendReplacement(result, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
