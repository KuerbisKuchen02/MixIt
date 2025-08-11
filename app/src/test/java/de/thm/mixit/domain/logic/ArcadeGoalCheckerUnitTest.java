package de.thm.mixit.domain.logic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains unit tests for the ArcadeGoalChecker class.
 *
 * @author Justin Wolek
 */
public class ArcadeGoalCheckerUnitTest {

    @Test
    public void matchesTargetElement_correctElement_returnsTrue() {
        // Given
        ArrayList<String> goalWords = new ArrayList<>(
                List.of("Schokokuchen", "Schokoladenkuchen", "Schokotorte", "Schokoladentorte")
        );
        String word = "Schokokuchen";

        // When
        boolean isContained = ArcadeGoalChecker.matchesTargetElement(goalWords, word);

        // Then
        assertTrue(isContained);
    }

    @Test
    public void matchesTargetElement_wrongElement_returnsFalse() {
        // Given
        ArrayList<String> goalWords = new ArrayList<>(
                List.of("Schokokuchen", "Schokoladenkuchen", "Schokotorte", "Schokoladentorte")
        );
        String word = "Erdbeerkuchen";

        // When
        boolean isContained = ArcadeGoalChecker.matchesTargetElement(goalWords, word);

        // Then
        assertFalse(isContained);
    }
}
