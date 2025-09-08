package de.thm.mixit.domain.logic;


/**
 * The ArcadeTargetChecker is responsible for checking if a found word/element satisfies a
 * searched word inside the Arcade Mode.
 *
 * @author Justin Wolek
 */
public class ArcadeTargetChecker {

    /**
     * Checks if a word is the one which is searched for inside an Arcade Session. The word
     * is compared to multiple synonyms of the given target word. Comparison is case-insensitive.
     *
     * @param targetWords An {@code ArrayList}, containing the actual target word,
     *                    as well as synonyms of it.
     * @param word      The word to be checked against all target-word variations.
     * @return          A {@code boolean} representing if the given word is the word
     *                  which should be found.
     */
    public static boolean matchesTargetElement(String[] targetWords, String word) {
        for (String w : targetWords) {
            if (w.equalsIgnoreCase(word)) return true;
        }
        return false;
    }
}
