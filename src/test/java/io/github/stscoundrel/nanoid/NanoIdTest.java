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

import static org.assertj.core.api.Assertions.assertThat;

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
            assertThat(result.length()).isEqualTo(21);
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
            assertThat(result1.length()).isEqualTo(5);
            assertThat(result2.length()).isEqualTo(10);
            assertThat(result3.length()).isEqualTo(30);
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
    void generatesWithAlphabetAndSize() {
        try (Driver driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI());
             Session session = driver.session()) {

            String result = session.run("RETURN io.github.stscoundrel.nanoid.withAlphabetAndSize('xxx', 5) AS result").single().get("result").asString();

            // With limited alphabet of one letter, should be deterministic.
            assertThat(result).isEqualTo("xxxxx");
        }
    }
}