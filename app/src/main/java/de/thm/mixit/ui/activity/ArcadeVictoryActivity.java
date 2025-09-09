package de.thm.mixit.ui.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import de.thm.mixit.BuildConfig;
import de.thm.mixit.R;
import de.thm.mixit.data.entity.Element;
import de.thm.mixit.data.repository.ElementRepository;
import de.thm.mixit.data.repository.GameStateRepository;
import nl.dionsegijn.konfetti.core.Angle;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.Spread;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.xml.KonfettiView;
import nl.dionsegijn.konfetti.xml.image.ImageUtil;

/**
 * Activity for the victory screen of an arcade game.
 * <p>
 * This Activity shows the achieved target word, the number of turns and the time passed
 * it took to reach said target word
 */
public class ArcadeVictoryActivity extends AppCompatActivity {

    private static final String TAG = ArcadeVictoryActivity.class.getSimpleName();
    public static final String EXTRA_GOAL_WORD = "targetWord";
    public static final String EXTRA_NUM_TURNS = "numTurns";
    public static final String EXTRA_PASSED_TIME = "playedTime";
    private Element targetWord;
    private int numTurns = -1;
    private long passedTime = -1;

    private KonfettiView konfettiView;
    private Shape.DrawableShape drawableShape;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GameStateRepository gameStateRepository = GameStateRepository.create(this,true);
        gameStateRepository.deleteSavedGameState();

        ElementRepository elementRepository = ElementRepository.create(this, true);
        elementRepository.reset();

        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_GOAL_WORD)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                targetWord = Objects.requireNonNull(intent.getExtras()).getSerializable(EXTRA_GOAL_WORD, Element.class);
            }
        } else {
            Log.e(TAG, "ArcadeVictoryActivity received Intent without the " + EXTRA_PASSED_TIME);
            throw new IllegalArgumentException("Target word was not received");
        }

        if (intent.hasExtra(EXTRA_NUM_TURNS)) {
            numTurns = Objects.requireNonNull(intent.getExtras()).getInt(EXTRA_NUM_TURNS);
        } else {
            Log.e(TAG, "ArcadeVictoryActivity received Intent without the " + EXTRA_PASSED_TIME);
            throw new IllegalArgumentException("Number of turns was not received");
        }

        if (intent.hasExtra(EXTRA_PASSED_TIME)) {
            passedTime = Objects.requireNonNull(intent.getExtras()).getLong(EXTRA_PASSED_TIME);
        } else {
            Log.e(TAG, "ArcadeVictoryActivity received Intent without the " + EXTRA_PASSED_TIME);
            throw new IllegalArgumentException("Passed time was not received");
        }

        setContentView(R.layout.activity_arcade_victory);

        TextView targetWordView = findViewById(R.id.text_arc_vic_target_word);
        targetWordView.setText(targetWord.toString());

        TextView numTurnsView = findViewById(R.id.text_arc_vic_turns);
        numTurnsView.setText(numTurns + "");

        long hours = (passedTime / (1000 * 60 * 60)) % 24;
        long minutes = (passedTime / (1000 * 60)) % 60;
        long seconds = (passedTime / 1000) % 60;

        String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        TextView passedTimeView = findViewById(R.id.text_arc_vic_time);
        passedTimeView.setText(time);

        // Konfetti
        final Drawable drawable =
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_plus);
        this.drawableShape = ImageUtil.loadDrawable(drawable, true, true);

        this.konfettiView = findViewById(R.id.konfetti_arc_vic);

        parade();

        Log.i(TAG, "ArcadeVictoryActivity was created");
    }

    /**
     * Handle clicking on the new game button to go to the game and create a new game
     * @param view  The view which received the click
     */
    public void onNewGameButtonClicked(View view){
        if(BuildConfig.DEBUG) Log.d(TAG, "New game button clicked");
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(GameActivity.EXTRA_IS_ARCADE, true);
        intent.putExtra(GameActivity.EXTRA_NEW_GAME, true);
        startActivity(intent);
    }

    /**
     * Handle clicking on the return button to return to the main menu
     * @param view  The view which received the click
     */
    public void onReturnButtonClicked(View view){
        if(BuildConfig.DEBUG) Log.d(TAG, "Return button clicked");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Configures and starts confetti to be shown inside the activity.
     * Reference <a href="https://github.com/DanielMartinus/Konfetti" />this public repository</a>
     * for further details.
     */
    private void startConfetti() {
        EmitterConfig emitterConfig = new Emitter(5, TimeUnit.SECONDS).perSecond(30);
        konfettiView.start(
                new PartyFactory(emitterConfig)
                        .angle(Angle.RIGHT - 45)
                        .spread(Spread.SMALL)
                        .shapes(Arrays.asList(Shape.Square.INSTANCE, Shape.Circle.INSTANCE, drawableShape))
                        .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                        .setSpeedBetween(10f, 30f)
                        .position(new Position.Relative(0.0, 0.5))
                        .build(),
                new PartyFactory(emitterConfig)
                        .angle(Angle.LEFT + 45)
                        .spread(Spread.SMALL)
                        .shapes(Arrays.asList(Shape.Square.INSTANCE, Shape.Circle.INSTANCE, drawableShape))
                        .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                        .setSpeedBetween(10f, 30f)
                        .position(new Position.Relative(1.0, 0.5))
                        .build()
        );
    }
}
