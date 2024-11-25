package com.github.lernejo.korekto.grader.api;

import com.github.lernejo.korekto.grader.api.http.Http;
import com.github.lernejo.korekto.toolkit.GradingConfiguration;
import com.github.lernejo.korekto.toolkit.GradingContext;
import com.github.lernejo.korekto.toolkit.partgrader.MavenContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class LaunchingContext extends GradingContext implements MavenContext {

    public final TestData testData = new TestData(getRandomSource());
    private final int pgPort;
    private boolean compilationFailed;
    private boolean testFailed;
    public List<String> part2Errors;

    public LaunchingContext(GradingConfiguration configuration, int pgPort) {
        super(configuration);
        this.pgPort = pgPort;
    }

    public static long serverStartTime() {
        return Long.parseLong(System.getProperty("server_start_timeout", "20"));
    }

    public static Map<String, String> basicHeader(TestData.UserDef user) {
        return Map.of("Authorization", Http.basic(user.email(), user.password()));
    }

    public String pgUrl() {
        return "jdbc:postgresql://localhost:" + pgPort + "/postgres";
    }

    @Override
    public boolean hasCompilationFailed() {
        return compilationFailed;
    }

    @Override
    public boolean hasTestFailed() {
        return testFailed;
    }

    @Override
    public void markAsCompilationFailed() {
        compilationFailed = true;
    }

    @Override
    public void markAsTestFailed() {
        testFailed = true;
    }

    public void initdb() {
        try (Connection connection = DriverManager.getConnection(pgUrl(), "postgres", "example");
             PreparedStatement dropStm = connection.prepareStatement("DROP SCHEMA public CASCADE");
             PreparedStatement createStm = connection.prepareStatement("CREATE SCHEMA public")) {
            dropStm.execute();
            createStm.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to reset db: " + e.getMessage(), e);
        }
    }
}
