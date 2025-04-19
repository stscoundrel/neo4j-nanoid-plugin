package io.github.stscoundrel.nanoid;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NanoIdTest {

    private Neo4j embeddedDatabaseServer;

    @BeforeAll
    void initializeNeo4j() {
        this.embeddedDatabaseServer = Neo4jBuilders.newInProcessBuilder()
                .withDisabledServer()
                .withFunction(NanoId.class)
                .build();
    }

    @AfterAll
    void closeNeo4j() {
        this.embeddedDatabaseServer.close();
    }

    @Test
    void generatesRandom() {
        try (Driver driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI());
             Session session = driver.session()) {

            String result = session.run("RETURN io.github.stscoundrel.nanoid.random() AS result").single().get("result").asString();

            // Should've returned default sized ID string.
            assertThat(result.length()).withFailMessage("Expected nanoId of length 21, but got %d", result.length()).isEqualTo(21);
        }
    }

    @Test
    void generatesDifferentIds() {
        try (Driver driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI());
             Session session = driver.session()) {

            String first = session.run("RETURN io.github.stscoundrel.nanoid.random() AS result").single().get("result").asString();
            String second = session.run("RETURN io.github.stscoundrel.nanoid.random() AS result").single().get("result").asString();

            assertThat(first).isNotEqualTo(second);
        }
    }

    @Test
    void generatesWithSize() {
        try (Driver driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI());
             Session session = driver.session()) {

            String result1 = session.run("RETURN io.github.stscoundrel.nanoid.withSize(5) AS result").single().get("result").asString();
            String result2 = session.run("RETURN io.github.stscoundrel.nanoid.withSize(10) AS result").single().get("result").asString();
            String result3 = session.run("RETURN io.github.stscoundrel.nanoid.withSize(30) AS result").single().get("result").asString();

            // Should've returned IDs of expected sizes.
            assertThat(result1.length()).withFailMessage("Expected nanoId of length 5, but got %d", result1.length()).isEqualTo(5);
            assertThat(result2.length()).withFailMessage("Expected nanoId of length 10, but got %d", result2.length()).isEqualTo(10);
            assertThat(result3.length()).withFailMessage("Expected nanoId of length 30, but got %d", result3.length()).isEqualTo(30);
        }
    }

    @Test
    void usesDefaultSizeWhenOmitted() {
        try (Driver driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI());
             Session session = driver.session()) {

            String result = session.run("RETURN io.github.stscoundrel.nanoid.withSize() AS result")
                    .single().get("result").asString();

            assertThat(result.length()).isEqualTo(21);
        }
    }

    @Test
    void failsWithNegativeSize() {
        try (Driver driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI());
             Session session = driver.session()) {

            assertThatThrownBy(() ->
                    session.run("RETURN io.github.stscoundrel.nanoid.withSize(-5) AS result").single()
            ).hasMessageContaining("Size must be greater than 0");
        }
    }

    @Test
    void generatesWithAlphabet() {
        try (Driver driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI());
             Session session = driver.session()) {

            String result = session.run("RETURN io.github.stscoundrel.nanoid.withAlphabet('aaa') AS result").single().get("result").asString();

            // With limited alphabet of one letter, should be deterministic.
            assertThat(result).isEqualTo("aaaaaaaaaaaaaaaaaaaaa");
        }
    }

    @Test
    void failsWithEmptyAlphabet() {
        try (Driver driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI());
             Session session = driver.session()) {

            assertThatThrownBy(() ->
                    session.run("RETURN io.github.stscoundrel.nanoid.withAlphabet('') AS result").single()
            ).hasMessageContaining("Alphabet must not be empty");
        }
    }

    @Test
    void generatesWithAlphabetAndSize() {
        try (Driver driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI());
             Session session = driver.session()) {

            String result = session.run("RETURN io.github.stscoundrel.nanoid.withAlphabetAndSize('xxx', 5) AS result").single().get("result").asString();

            // With limited alphabet of one letter, should be deterministic.
            assertThat(result).isEqualTo("xxxxx");
        }
    }

    @Test
    void generatesValidNanoIdsWithVariousAlphabetsAndSizes() {
        try (Driver driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI());
             Session session = driver.session()) {

            // Define test cases with various alphabets and sizes
            Map<String, Integer> testCases = Map.of(
                    "ABC", 5,
                    "xyz", 10,
                    "123", 8,
                    "a", 7,
                    "A1B2C3D4", 12
            );

            for (Map.Entry<String, Integer> entry : testCases.entrySet()) {
                String alphabet = entry.getKey();
                int size = entry.getValue();

                String result = session
                        .run(String.format("RETURN io.github.stscoundrel.nanoid.withAlphabetAndSize('%s', %d) AS result", alphabet, size))
                        .single()
                        .get("result")
                        .asString();

                assertThat(result.length())
                        .withFailMessage("Alphabet '%s': Expected length %d, but got %d", alphabet, size, result.length())
                        .isEqualTo(size);

                assertThat(result.codePoints()
                        .mapToObj(cp -> new String(Character.toChars(cp)))
                        .allMatch(alphabet::contains))
                        .withFailMessage("Alphabet '%s': Result '%s' contains invalid characters", alphabet, result)
                        .isTrue();
            }
        }
    }
}