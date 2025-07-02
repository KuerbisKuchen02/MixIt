package de.thm.mixit.data.source;

import com.openai.client.OpenAIClientAsync;
import com.openai.client.okhttp.OpenAIOkHttpClientAsync;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

import de.thm.mixit.BuildConfig;

public class ElementRemote {
    private final static OpenAIClientAsync client = new OpenAIOkHttpClientAsync.Builder()
            .apiKey(BuildConfig.apiKey)
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

    /**
     * Combines two elements using the OpenAI API and returns the result via a callback.
     * @param element1 - the first element to combine
     * @param element2 - the second element to combine
     * @param callback - a callback that will be called with the result of the combination
     * @throws RuntimeException if the OpenAI API returns no choices or empty content
     */
    public static void combine(String element1, String element2, Consumer<String> callback) {
        ChatCompletionCreateParams createParams = ChatCompletionCreateParams.builder()
                .addDeveloperMessage(SYSTEM_PROMPT)
                .addUserMessage(element1 + " + " + element2)
                .model(ChatModel.GPT_4O)
                .build();

       client.chat().completions().create(createParams).thenAccept(chatCompletion -> {
            if (chatCompletion.choices().isEmpty()) {
                throw new RuntimeException("No choices returned from OpenAI API");
            }

            if (chatCompletion.choices().get(0).message().content().isEmpty())
                throw new RuntimeException("Empty content returned from OpenAI API");

            callback.accept(chatCompletion.choices().get(0).message().content().get());
        });
    }
}
