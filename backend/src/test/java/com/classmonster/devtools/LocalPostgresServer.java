package com.classmonster.devtools;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * 로컬 개발용 PostgreSQL 17 서버 (Docker·관리자 권한 불필요).
 * <p>
 * - Gradle: {@code ./gradlew localDb} 로 실행, {@code ./gradlew localDbStop} 으로 종료
 * - 데이터: backend/.localdb/data (재시작해도 유지, 초기화하려면 폴더 삭제)
 * - 접속 정보: 루트 .env 의 POSTGRES_DB / POSTGRES_USER / POSTGRES_PASSWORD / POSTGRES_PORT
 *   → 백엔드 local 프로필과 같은 값을 사용하므로 그대로 붙는다.
 * <p>
 * 테스트 소스에 두어 운영 jar(bootJar)에는 포함되지 않는다.
 */
public final class LocalPostgresServer {

    private static final Pattern IDENTIFIER = Pattern.compile("^[a-z_][a-z0-9_]{0,62}$");
    private static final Path DATA_DIR = Path.of(".localdb", "data").toAbsolutePath();

    private LocalPostgresServer() {
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> env = loadEnv();
        int port = Integer.parseInt(env.getOrDefault("POSTGRES_PORT", "5432"));
        String database = identifier(env.getOrDefault("POSTGRES_DB", "classmonster"), "POSTGRES_DB");
        String user = identifier(env.getOrDefault("POSTGRES_USER", "classmonster"), "POSTGRES_USER");
        String password = env.getOrDefault("POSTGRES_PASSWORD", "classmonster");

        if (args.length > 0 && "stop".equals(args[0])) {
            stop();
            return;
        }

        if (isPortOpen(port)) {
            System.out.printf("이미 %d 포트에서 PostgreSQL(또는 다른 프로그램)이 실행 중이에요.%n", port);
            System.out.println("이전에 띄운 로컬 DB라면 그대로 사용하면 되고, 끄려면 ./gradlew localDbStop 을 실행하세요.");
            return;
        }

        Files.createDirectories(DATA_DIR.getParent());
        EmbeddedPostgres postgres = EmbeddedPostgres.builder()
                .setPort(port)
                .setDataDirectory(DATA_DIR)
                .setCleanDataDirectory(false)
                .setServerConfig("listen_addresses", "localhost")
                .setServerConfig("timezone", "Asia/Seoul")
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                postgres.close();
                System.out.println("로컬 PostgreSQL 을 종료했어요.");
            } catch (IOException e) {
                System.err.println("종료 중 오류: " + e.getMessage());
            }
        }));

        ensureUserAndDatabase(postgres, database, user, password);

        System.out.println();
        System.out.println("============================================================");
        System.out.println(" 로컬 PostgreSQL 17 실행 중 (Ctrl+C 로 종료)");
        System.out.printf("  JDBC URL : jdbc:postgresql://localhost:%d/%s%n", port, database);
        System.out.printf("  사용자    : %s (비밀번호는 .env 의 POSTGRES_PASSWORD)%n", user);
        System.out.printf("  데이터    : %s%n", DATA_DIR);
        System.out.println("  백엔드    : ./gradlew bootRun --args='--spring.profiles.active=local'");
        System.out.println("============================================================");

        Thread.currentThread().join();
    }

    /** 앱 전용 사용자와 DB 를 만든다. 이미 있으면 비밀번호만 .env 값으로 맞춘다. */
    private static void ensureUserAndDatabase(EmbeddedPostgres postgres, String database, String user, String password)
            throws SQLException {
        String quotedPassword = "'" + password.replace("'", "''") + "'";
        try (Connection conn = postgres.getPostgresDatabase().getConnection();
             Statement statement = conn.createStatement()) {
            if (exists(conn, "select 1 from pg_roles where rolname = ?", user)) {
                statement.execute("ALTER ROLE " + user + " WITH LOGIN PASSWORD " + quotedPassword);
            } else {
                statement.execute("CREATE ROLE " + user + " WITH LOGIN PASSWORD " + quotedPassword);
                System.out.println("사용자 " + user + " 를 만들었어요.");
            }
            if (!exists(conn, "select 1 from pg_database where datname = ?", database)) {
                statement.execute("CREATE DATABASE " + database + " OWNER " + user
                        + " ENCODING 'UTF8' LC_COLLATE 'C' LC_CTYPE 'C' TEMPLATE template0");
                System.out.println("데이터베이스 " + database + " 를 만들었어요.");
            }
        }
    }

    private static boolean exists(Connection conn, String sql, String value) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** 터미널을 닫아 남아 있는 서버를 pg_ctl 로 종료한다. */
    private static void stop() throws IOException, InterruptedException {
        if (!Files.exists(DATA_DIR.resolve("postmaster.pid"))) {
            System.out.println("실행 중인 로컬 PostgreSQL 이 없어요.");
            return;
        }
        Optional<Path> pgCtl = findPgCtl();
        if (pgCtl.isEmpty()) {
            System.out.println("pg_ctl 을 찾지 못했어요. ./gradlew localDb 를 한 번 실행한 뒤 다시 시도해 주세요.");
            return;
        }
        Process process = new ProcessBuilder(pgCtl.get().toString(), "stop", "-D", DATA_DIR.toString(), "-m", "fast")
                .inheritIO()
                .start();
        System.out.println(process.waitFor() == 0 ? "로컬 PostgreSQL 을 종료했어요." : "종료하지 못했어요. 위 메시지를 확인해 주세요.");
    }

    /** embedded-postgres 가 압축을 푼 위치(임시 폴더/embedded-pg)에서 pg_ctl 을 찾는다. */
    private static Optional<Path> findPgCtl() throws IOException {
        Path root = Path.of(System.getProperty("java.io.tmpdir"), "embedded-pg");
        if (!Files.isDirectory(root)) {
            return Optional.empty();
        }
        List<String> names = List.of("pg_ctl.exe", "pg_ctl");
        try (Stream<Path> paths = Files.walk(root, 4)) {
            return paths.filter(p -> names.contains(p.getFileName().toString()))
                    .filter(p -> p.getParent() != null && p.getParent().getFileName().toString().equals("bin"))
                    .max((a, b) -> Long.compare(a.toFile().lastModified(), b.toFile().lastModified()));
        }
    }

    private static boolean isPortOpen(int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("localhost", port), 300);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /** 루트 .env → backend/.env → 시스템 환경 변수 순으로 덮어쓴다. */
    private static Map<String, String> loadEnv() throws IOException {
        Map<String, String> env = new HashMap<>();
        for (Path file : List.of(Path.of("..", ".env"), Path.of(".env"))) {
            if (Files.isRegularFile(file)) {
                for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                    String trimmed = line.trim();
                    int eq = trimmed.indexOf('=');
                    if (trimmed.isEmpty() || trimmed.startsWith("#") || eq <= 0) {
                        continue;
                    }
                    env.put(trimmed.substring(0, eq).trim(), stripQuotes(trimmed.substring(eq + 1).trim()));
                }
            }
        }
        System.getenv().forEach((key, value) -> {
            if (key.startsWith("POSTGRES_")) {
                env.put(key, value);
            }
        });
        return env;
    }

    private static String stripQuotes(String value) {
        if (value.length() >= 2 && (value.startsWith("\"") && value.endsWith("\"")
                || value.startsWith("'") && value.endsWith("'"))) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    private static String identifier(String value, String name) {
        if (!IDENTIFIER.matcher(value).matches()) {
            throw new IllegalArgumentException(name + " 는 영문 소문자, 숫자, _ 만 사용할 수 있어요: " + value);
        }
        return value;
    }
}
