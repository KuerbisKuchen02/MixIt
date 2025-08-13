package de.thm.mixit.ui.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.time.Duration;
import java.util.Objects;

import de.thm.mixit.BuildConfig;
import de.thm.mixit.R;
import de.thm.mixit.data.entities.Element;

/**
 * Activity for the victory screen of an arcade game.
 * <p>
 * This Activity shows the achieved goal word, the number of turns and the time passed
 * it took to reach said goal word
 */
public class ArcadeVictoryActivity extends AppCompatActivity {

    private static final String TAG = ArcadeVictoryActivity.class.getSimpleName();
    public static final String EXTRA_GOAL_WORD = "goalWord";
    public static final String EXTRA_NUM_TURNS = "numTurns";
    public static final String EXTRA_PASSED_TIME = "playedTime";
    private Element goalWord;
    private int numTurns = -1;
    private long passedTime = -1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_GOAL_WORD)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                goalWord = Objects.requireNonNull(intent.getExtras()).getSerializable(EXTRA_GOAL_WORD, Element.class);
            }
        } else {
            Log.e(TAG, "ArcadeVictoryActivity received Intent without the " + EXTRA_PASSED_TIME);
            throw new IllegalArgumentException("Goal word was not received");
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

        TextView goalWordView = findViewById(R.id.text_arc_vic_goal_word);
        goalWordView.setText(goalWord.toString());

        TextView numTurnsView = findViewById(R.id.text_arc_vic_turns);
        numTurnsView.setText(numTurns + "");

        long hours = (passedTime / (1000 * 60 * 60)) % 24;
        long minutes = (passedTime / (1000 * 60)) % 60;
        long seconds = (passedTime / 1000) % 60;

        String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        TextView passedTimeView = findViewById(R.id.text_arc_vic_time);
        passedTimeView.setText(time);

        Log.i(TAG, "ArcadeVictoryActivity was created");
    }


    public void onNewGameButtonClicked(View view){
        if(BuildConfig.DEBUG) Log.d(TAG, "New game button clicked");
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(GameActivity.EXTRA_IS_ARCADE, true);
        intent.putExtra(GameActivity.EXTRA_NEW_GAME, true);
        startActivity(intent);
    }

    public void onReturnButtonClicked(View view){
        if(BuildConfig.DEBUG) Log.d(TAG, "Return button clicked");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }



}
