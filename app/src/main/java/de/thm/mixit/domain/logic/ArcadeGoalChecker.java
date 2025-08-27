package de.thm.mixit.domain.logic;


/**
 * The ArcadeGoalChecker is responsible for checking if a found word/element satisfies a
 * searched word inside the Arcade Mode.
 *
 * @author Justin Wolek
 */
public class ArcadeGoalChecker {

    /**
     * Checks if a word is the one which is searched for inside an Arcade Session. The word
     * is compared to multiple synonyms of the given goal word. Comparison is case-insensitive.
     *
     * @param goalWords An {@code ArrayList}, containing the actual goal word, as well as synonyms
     *                  of it.
     * @param word      The word to be checked against all goal-word variations.
     * @return          A {@code boolean} representing if the given word is the word
     *                  which should be found.
     */
    public static boolean matchesTargetElement(String[] goalWords, String word) {
        for (String w : goalWords) {
            if (w.equalsIgnoreCase(word)) return true;
        }
        return false;
    }
}
