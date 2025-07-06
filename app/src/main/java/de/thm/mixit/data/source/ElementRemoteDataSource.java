package de.thm.mixit.data.source;

import com.openai.client.OpenAIClientAsync;
import com.openai.client.okhttp.OpenAIOkHttpClientAsync;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

import java.util.function.Consumer;

import de.thm.mixit.BuildConfig;
import de.thm.mixit.data.entities.Element;

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

    // TODO insert a regex to validate the element format <Emoji> <Description>
    private static boolean isValidElement(String element) {
        return true;
        // return element.matches("");
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
                               Consumer<Element> callback) {
        ChatCompletionCreateParams createParams = ChatCompletionCreateParams.builder()
                .addDeveloperMessage(SYSTEM_PROMPT)
                .addUserMessage(element1 + " + " + element2)
                .model(ChatModel.GPT_4O)
                .build();

       client.chat().completions().create(createParams).thenAccept(
               chatCompletion -> {
            if (chatCompletion.choices().isEmpty()) {
                throw new RuntimeException("No choices returned from OpenAI API");
            }

            if (chatCompletion.choices().get(0).message().content().isEmpty())
                throw new RuntimeException("Empty content returned from OpenAI API");

            String content = chatCompletion.choices().get(0).message().content().get();

            if (!isValidElement(content)) {
                throw new RuntimeException("Invalid element format: " + content);
            }

            String emoji = content.substring(0, content.indexOf(' '));
            String name = content.substring(content.indexOf(' ') + 1);

            callback.accept(new Element(name, emoji));
        });
    }
}
