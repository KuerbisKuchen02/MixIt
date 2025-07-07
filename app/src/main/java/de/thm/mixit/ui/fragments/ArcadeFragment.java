package de.thm.mixit.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

import de.thm.mixit.R;

/**
 * Fragment class which holds information for the arcade game mode.
 *
 * @author Jannik Heimann
 */

public class ArcadeFragment extends Fragment {

    private static final String TAG = ArcadeFragment.class.getSimpleName();
    private final Handler handler = new Handler();
    private TextView tv_target_element;
    private String target_element = "Schokokuchen"; // TODO replace once backend method is available
    private TextView tv_time_since_start;
    private long startTime;
    private int turns = 0;
    private TextView tv_turns;


    /**
     * This method is responsible to update the textview displaying the passed time.
     * It will be called every second.
     */
    private final Runnable updateTimerRunnable = new Runnable() {
        @Override
        public void run() {
            long elapsedMillis = System.currentTimeMillis() - startTime;

            int seconds = (int) (elapsedMillis / 1000) % 60;
            int minutes = (int) (elapsedMillis / (1000 * 60)) % 60;
            int hours = (int) (elapsedMillis / (1000 * 60 * 60));

            String time = String.format(Locale.getDefault(),
                    getString(R.string.arcade_fragment_time), hours, minutes, seconds);
            tv_time_since_start.setText(time);

            // Handler calls it again every second
            handler.postDelayed(this, 1000);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_arcade, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO move logic to GameViewModel once it is available
        // Bind timer TextView and start the timer to display
        tv_time_since_start = view.findViewById(R.id.textview_time_since_start);
        startTime = System.currentTimeMillis();
        Log.d(TAG, "Subscribe timer callback");
        handler.post(updateTimerRunnable);

        // Bind the turns TextView and set the default value
        tv_turns = view.findViewById(R.id.textview_turns);
        tv_turns.setText(String.format(Locale.getDefault(),
                getString(R.string.arcade_fragment_turns), turns));

        // Bind the target element TextView and set the default value
        tv_target_element = view.findViewById(R.id.textview_target_element);
        tv_target_element.setText(String.format(Locale.getDefault(),
                getString(R.string.arcade_fragment_goal), target_element));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "Remove timer callback");
        handler.removeCallbacks(updateTimerRunnable);
    }

    public void increaseTurnCounter() {
        Log.d(TAG, "Increasing turn counter.");
        turns++;
        tv_turns.setText(String.format(Locale.getDefault(),
                getString(R.string.arcade_fragment_turns), turns));
    }
}
