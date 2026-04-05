package ch.c0r3.beecheck.application.service;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;

import java.io.File;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;


@QuarkusTest
class SectionParserTest {

    @Inject
    SectionParser testee;

    @Test
    void parseSectionFileCorrect() {
        // Arrange
        File testFile = getTestFile();

        // Act
        var result = testee.parseSectionFile(testFile);

        // Act
        assertEquals(1, result.size());

        var keeper = result.getFirst();
        assertEquals("Müller", keeper.lastname());
        assertEquals("Marcel", keeper.firstname());
        assertEquals(2, keeper.apiaries().size());
        assertEquals(2653862L, keeper.apiaries().getFirst().xCoordinate());
        assertEquals(46312733L, keeper.apiaries().get(1).xCoordinate());
    }

    private File getTestFile() {
        try {
            var fileURI = SectionParserTest.class.getResource("/importTestData.xlsx").toURI();
            return new File(fileURI);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}