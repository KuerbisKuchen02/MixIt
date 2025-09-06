package de.thm.mixit.data.source;

import com.openai.client.OpenAIClientAsync;
import com.openai.client.okhttp.OpenAIOkHttpClientAsync;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

import java.util.List;
import java.util.function.Consumer;

import de.thm.mixit.BuildConfig;
import de.thm.mixit.data.entities.Element;
import de.thm.mixit.data.exception.CombinationException;
import de.thm.mixit.data.exception.InvalidGoalWordException;
import de.thm.mixit.data.model.Result;

/**
 * Remote data source for accessing and combining elements using the OpenAI API.
 * <p>
 * This class handles the interaction with the OpenAI API to generate new elements
 * by combining two existing elements. It uses asynchronous calls to ensure that
 * the UI remains responsive while waiting for the API response.
 *
 * @author Jonathan Hildebrandt
 */
public class ElementRemoteDataSource {
    private final static OpenAIClientAsync client = new OpenAIOkHttpClientAsync.Builder()
            .apiKey(BuildConfig.API_KEY)
            .build();

    private final static String SYSTEM_PROMPT =
            "Wir spielen Infinite Craft. Du bist die Engine.\n" +
            "\n" +
            "Ich nenne dir immer zwei Elemente – " +
            "sie können gleich oder verschieden sein.\n" +
            "Du kombinierst sie kreativ zu einem neuen Element.\n" +
            "Antworte immer exakt im Format: <Emoji> <Bezeichnung>\n" +
            "Gib keine Erklärungen, keine Zusätze – " +
            "nur das eine neue Element mit genau einem passenden Emoji.\n";

    private final static String GOAL_WORD_PROMPT =
            "Arcade-Modus – Zielwort & Synonyme (Deutsch)\n" +
            "\n" +
            "Wir spielen Infinite Craft. Start-Elemente: Feuer, Wasser, Luft, Erde.\n" +
            "\n" +
            "Wähle ein **Zielwort**, das mit diesen Start-Elementen in 5–15 " +
                    "Minuten Spielzeit erreichbar ist (mittlere Schwierigkeit;" +
                    " weder trivial noch kryptisch; keine Eigennamen/Marken).\n" +
            "\n" +
            "Gib **ausschließlich** eine kommaseparierte Liste zurück:\n" +
            "<Zielwort>, <Variante1>, <Variante2>, ...\n" +
            "\n" +
            "Regeln:\n" +
            "- Sprache: Deutsch.\n" +
            "- Erster Eintrag = exakt das Zielwort, das angezeigt wird.\n" +
            "- Danach 3–7 **gleichwertige Bezeichnungen desselben Gegenstands**: " +
                    "echte Synonyme, Flexionsformen (Singular/Plural) oder " +
                    "gängige Zusammensetzungen/Schreibvarianten mit dem Zielwort als " +
                    "Kopf (z. B. Kerze, Kerzen, Wachskerze).\n" +
            "- **Strenger Bedeutungsrahmen (IS-A-Test):** Jedes Wort muss denselben " +
                    "Gegenstand bezeichnen wie das Zielwort („Ein <WORT> " +
                    "ist eine/ein <ZIELWORT>?“ → **Ja**).\n" +
            "- **NICHT erlaubt:** Oberbegriffe/Funktionen (z. B. Lichtquelle, Beleuchtung), " +
                    "Nachbarobjekte (z. B. Laterne, Lampe, Fackel), " +
                    "Teile/Material/Eigenschaften (z. B. Flamme, Docht, Wachs), Halter/Behälter " +
                    "(z. B. Kerzenhalter, Laterne).\n" +
            "- Keine Erklärungen, kein Zusatztext, **keine Emojis**, keine Anführungszeichen, " +
                    "**kein Punkt am Ende**.\n" +
            "- Keine Duplikate; jeweils **ein Leerzeichen nach jedem Komma**; " +
                    "Groß-/Kleinschreibung gemäß deutscher Rechtschreibung.\n" +
            "- **Wenn unsicher:** Nutze ausschließlich Flexions- und Kompositavarianten " +
                    "mit dem Zielwort als Bestandteil.\n" +
            "- Das Zielwort darf KEIN Wort aus der folgenden Liste sein: %s" +
            "\n" +
            "Beispielausgabe:\n" +
            "Kerze, Kerzen, Wachskerze\n";

    // TODO insert a regex to validate the element format <Emoji> <Description>
    private static boolean isValidElement(String element) {
        return true;
    }

    private static boolean isValidGoalResponse(String response) {
        return response.matches("^([a-zA-Z 0-9ÜüÄäÖöß-]+, )+([a-zA-Z 0-9ÜüÄäÖöß-]+)$");
    }

    /**
     * Combines two elements using the OpenAI API and returns the result via a callback.
     * @param element1 - the first element to combine
     * @param element2 - the second element to combine
     * @param callback - a callback that will be called with the result of the combination
     * @throws RuntimeException if the OpenAI API returns:
     * - no choices
     * - empty content
     * - an invalid element format
     */
    public static void combine(String element1, String element2,
                               Consumer<Result<Element>> callback) {
        ChatCompletionCreateParams createParams = ChatCompletionCreateParams.builder()
                .addDeveloperMessage(SYSTEM_PROMPT)
                .addUserMessage(element1 + " + " + element2)
                .model(ChatModel.CHATGPT_4O_LATEST)
                .build();

        client.chat().completions().create(createParams).handle(
                (chatCompletion, throwable) -> {
                    // When an error has occurred when calling the OpenAI API, the response in
                    // chatCompletion is null and throwable contains an error.
                    if (throwable != null) {
                        callback.accept(Result.failure(
                                new CombinationException("Internal error", throwable)));
                        return null;
                    } else if (chatCompletion.choices().isEmpty()) {
                        callback.accept(Result.failure(
                                new CombinationException("No choices returned from OpenAI API")
                        ));
                    } else if (chatCompletion.choices().get(0).message().content().isEmpty()) {
                        callback.accept(Result.failure(
                                new CombinationException("Empty content returned from OpenAI API")
                        ));
                    }

                    String content = chatCompletion.choices().get(0).message().content().get();

                    if (!isValidElement(content)) {
                        callback.accept(Result.failure(
                                new CombinationException("Invalid element format: " + content)
                        ));

                        return null;
                    }

                    String emoji = content.substring(0, content.indexOf(' '));
                    String name = content.substring(content.indexOf(' ') + 1);
                    callback.accept(Result.success(new Element(name, emoji)));

                    return null;
                });
    }

    /**
     * Generates a new goal word and its synonyms using the OpenAI API.
     * The result is returned via a callback.
     *
     * @param callback - a callback that will be called with the result of the goal word generation
     * @throws RuntimeException if the OpenAI API returns:
     * - no choices
     * - an empty content
     */
    public static void generateNewGoalWord(List<String> lastGoalWords,
                                           Consumer<Result<String[]>> callback) {
        ChatCompletionCreateParams createParams = ChatCompletionCreateParams.builder()
                .addDeveloperMessage(String.format(GOAL_WORD_PROMPT, lastGoalWords.toString()))
                .model(ChatModel.CHATGPT_4O_LATEST)
                .build();

        client.chat().completions().create(createParams).handle(
                (chatCompletion, throwable) -> {
                    if (throwable != null) {
                        callback.accept(Result.failure(
                                new InvalidGoalWordException("Internal error", throwable)));
                        return null;
                    }

                    if (chatCompletion.choices().isEmpty()) {
                        callback.accept(Result.failure(
                                new InvalidGoalWordException("No choices returned from OpenAI API")
                        ));
                        return null;
                    }

                    var content = chatCompletion.choices().get(0).message().content();

                    if (content.isPresent()) {
                        if (isValidGoalResponse(content.get())) {
                            String[] words = content.get().split(", ");
                            callback.accept(Result.success(words));
                        } else {
                            callback.accept(Result.failure(
                                    new InvalidGoalWordException(
                                            "Invalid goal word response format: " + content.get())
                            ));
                        }

                    } else {
                        callback.accept(Result.failure(
                                new InvalidGoalWordException(
                                        "Empty content returned from OpenAI API")
                        ));
                    }

                    return null;
                });
    }
}

