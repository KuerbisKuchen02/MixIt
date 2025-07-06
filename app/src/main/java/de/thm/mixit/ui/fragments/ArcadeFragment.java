package de.thm.mixit.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
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

    private static int turns = 0;
    private Handler handler = new Handler();
    private TextView tv_time_since_start;
    private long startTime;

    private Runnable updateTimerRunnable = new Runnable() {
        @Override
        public void run() {
            long elapsedMillis = System.currentTimeMillis() - startTime;

            int seconds = (int) (elapsedMillis / 1000) % 60;
            int minutes = (int) (elapsedMillis / (1000 * 60)) % 60;
            int hours = (int) (elapsedMillis / (1000 * 60 * 60));

            String time = String.format(Locale.getDefault(), "Time: %02d:%02d:%02d", hours, minutes, seconds);
            tv_time_since_start.setText(time);

            // Alle 1 Sekunde neu aufrufen
            handler.postDelayed(this, 1000);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_arcade, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startTime = System.currentTimeMillis();
        tv_time_since_start = view.findViewById(R.id.textview_time_since_start);
        startTime = System.currentTimeMillis();
        handler.post(updateTimerRunnable); // Timer starten
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateTimerRunnable); // Handler stoppen, um Memory-Leaks zu vermeiden
    }
}
