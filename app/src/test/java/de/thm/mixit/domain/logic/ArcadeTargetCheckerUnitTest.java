package de.thm.mixit.domain.logic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Contains unit tests for the ArcadeTargetChecker class.
 *
 * @author Justin Wolek
 */
public class ArcadeTargetCheckerUnitTest {

    @Test
    public void matchesTargetElement_correctElement_returnsTrue() {
        // Given
        String[] targetWords = {
                "Schokokuchen", "Schokoladenkuchen", "Schoolteacher", "Interscholastic"
        };
        String word = "Schokokuchen";

        // When
        boolean isContained = ArcadeTargetChecker.matchesTargetElement(targetWords, word);

        // Then
        assertTrue(isContained);
    }

    @Test
    public void matchesTargetElement_wrongElement_returnsFalse() {
        // Given
        String[] targetWords = {
                "Schokokuchen", "Schokoladenkuchen", "Schoolteacher", "Interscholastic"
        };
        String word = "Lebkuchen";

        // When
        boolean isContained = ArcadeTargetChecker.matchesTargetElement(targetWords, word);

        // Then
        assertFalse(isContained);
    }
}
