package de.thm.mixit.domain.logic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Contains unit tests for the ArcadeGoalChecker class.
 *
 * @author Justin Wolek
 */
public class ArcadeGoalCheckerUnitTest {

    @Test
    public void matchesTargetElement_correctElement_returnsTrue() {
        // Given
        String[] goalWords = {
                "Schokokuchen", "Schokoladenkuchen", "Schokotorte", "Schokoladentorte"
        };
        String word = "Schokokuchen";

        // When
        boolean isContained = ArcadeGoalChecker.matchesTargetElement(goalWords, word);

        // Then
        assertTrue(isContained);
    }

    @Test
    public void matchesTargetElement_wrongElement_returnsFalse() {
        // Given
        String[] goalWords = {
                "Schokokuchen", "Schokoladenkuchen", "Schokotorte", "Schokoladentorte"
        };
        String word = "Erdbeerkuchen";

        // When
        boolean isContained = ArcadeGoalChecker.matchesTargetElement(goalWords, word);

        // Then
        assertFalse(isContained);
    }
}
