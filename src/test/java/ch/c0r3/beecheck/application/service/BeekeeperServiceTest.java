package ch.c0r3.beecheck.application.service;

import ch.c0r3.beecheck.domain.model.Beekeeper;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class BeekeeperServiceTest {

    private static final Beekeeper MICHAEL = new Beekeeper(
            null, "Michael", "Reeve", null,
            null, null, null,
            null
    );
    private static final Beekeeper MICHEL = new Beekeeper(
            null, "Michel", "Müller", null,
            null, null, null,
            null
    );

    @Inject
    BeekeeperService testee;

    @BeforeEach
    void setUp() {
        testee.add(MICHAEL);
        testee.add(MICHEL);
    }

    @Test
    void queryByFirstNameOrLastName_findsExactByFirstname() {
        // Act
        var result = testee.queryByFirstNameOrLastName("Michael");

        // Assert
        assertEquals(1, result.size());
        assertEquals(MICHAEL, result.getFirst());
    }

    @Test
    void queryByFirstNameOrLastName_findsContainByFirstname() {
        // Act
        var result = testee.queryByFirstNameOrLastName("chael");

        // Assert
        assertEquals(1, result.size());
        assertEquals(MICHAEL, result.getFirst());
    }

    @Test
    void queryByFirstNameOrLastName_findsMultipleContainByFirstname() {
        // Act
        var result = testee.queryByFirstNameOrLastName("ich");

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(MICHAEL));
        assertTrue(result.contains(MICHEL));
    }

    @Test
    void queryByFirstNameOrLastName_findsExactByLastname() {
        // Act
        var result = testee.queryByFirstNameOrLastName("Reeve");

        // Assert
        assertEquals(1, result.size());
        assertEquals(MICHAEL, result.getFirst());
    }

    @Test
    void queryByFirstNameOrLastName_findsContainsByLastname() {
        // Act
        var result = testee.queryByFirstNameOrLastName("eeve");

        // Assert
        assertEquals(1, result.size());
        assertEquals(MICHAEL, result.getFirst());
    }

    @Test
    void queryByFirstNameOrLastName_findsMultipleContainsByLastname() {
        // Act
        var result = testee.queryByFirstNameOrLastName("e");

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(MICHAEL));
        assertTrue(result.contains(MICHEL));
    }


    @Test
    void queryByFirstNameOrLastName_doesntFindReturnsEmpty() {
        // Act
        var result = testee.queryByFirstNameOrLastName("Unknown");

        // Assert
        assertTrue(result.isEmpty());
    }

}