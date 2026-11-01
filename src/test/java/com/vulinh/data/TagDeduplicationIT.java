package com.vulinh.data;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.testcontainers.postgresql.PostgreSQLContainer;

class TagDeduplicationIT {

  @Test
  void migrationRetainsOldestTagAndPreservesAllPostAssociations() throws Exception {
    try (var postgres = new PostgreSQLContainer("postgres:18.3-alpine3.23")) {
      postgres.start();
      try (var connection =
              DriverManager.getConnection(
                  postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
          var statement = connection.createStatement()) {
        var changelog = Path.of("src/main/resources/db/changelog");
        statement.execute(Files.readString(changelog.resolve("0003-create-tag-table.sql")));
        statement.execute(
            """
            CREATE TABLE post (
              id UUID PRIMARY KEY,
              title VARCHAR(5000) NOT NULL,
              slug VARCHAR(5000) NOT NULL,
              post_content TEXT NOT NULL
            )
            """);
        statement.execute(
            Files.readString(changelog.resolve("0004-create-post-tag-mapping-table.sql")));
        // The oldest timestamp intentionally has the greatest UUID prefix.
        statement.execute(
            """
            INSERT INTO tag(id, display_name) VALUES
              ('ffffffff-ffff-1fff-8190-000000000001', 'spring'),
              ('00000000-0000-1000-819f-000000000002', 'spring'),
              ('00000000-0000-1000-819f-000000000003', 'spring'),
              ('00000000-0000-1000-819f-000000000004', 'java'),
              ('ffffffff-ffff-1fff-8190-000000000005', 'unused'),
              ('00000000-0000-1000-819f-000000000006', 'unused');
            INSERT INTO post(id, title, slug, post_content) VALUES
              ('00000000-0000-0000-0000-000000000001', 'First post', 'first-post', 'First content'),
              ('00000000-0000-0000-0000-000000000002', 'Second post', 'second-post', 'Second content');
            INSERT INTO post_tag_mapping(post_id, tag_id) VALUES
              ('00000000-0000-0000-0000-000000000001', 'ffffffff-ffff-1fff-8190-000000000001'),
              ('00000000-0000-0000-0000-000000000001', '00000000-0000-1000-819f-000000000002'),
              ('00000000-0000-0000-0000-000000000001', '00000000-0000-1000-819f-000000000003'),
              ('00000000-0000-0000-0000-000000000002', '00000000-0000-1000-819f-000000000002'),
              ('00000000-0000-0000-0000-000000000002', '00000000-0000-1000-819f-000000000004');
            """);

        connection.setAutoCommit(false);
        statement.execute(
            Files.readString(changelog.resolve("0013-add-tag-display-name-unique.sql")));
        connection.commit();

        try (var result = statement.executeQuery("SELECT count(*) FROM tag")) {
          assertTrue(result.next());
          assertEquals(3, result.getInt(1));
        }
        try (var result =
            statement.executeQuery(
                """
            SELECT count(*) FROM post_tag_mapping
            WHERE tag_id = 'ffffffff-ffff-1fff-8190-000000000001'
            """)) {
          assertTrue(result.next());
          assertEquals(2, result.getInt(1));
        }
        try (var result = statement.executeQuery("SELECT count(*) FROM post_tag_mapping")) {
          assertTrue(result.next());
          assertEquals(3, result.getInt(1));
        }
        try (var result =
            statement.executeQuery(
                """
            SELECT count(*) FROM pg_indexes
            WHERE tablename = 'tag' AND indexname = 'tag_display_name_idx'
            """)) {
          assertTrue(result.next());
          assertEquals(0, result.getInt(1));
        }
        var duplicate =
            assertThrows(
                SQLException.class,
                () ->
                    statement.execute(
                        """
            INSERT INTO tag(id, display_name)
            VALUES ('00000000-0000-1000-819f-000000000007', 'spring')
            """));
        assertEquals("23505", duplicate.getSQLState());
        connection.rollback();
      }
    }
  }
}
