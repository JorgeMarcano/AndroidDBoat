package com.dboat.dragonboat.dragonboating;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {

    private int[] bpms;
    private long[] times;
    private int[] totTimes = new int[4];
    private long[] totStrokes = new long[4];
    private int setCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent intent = getIntent();

        bpms = intent.getIntArrayExtra(TimerActivity.EXTRA_BPMS);
        times = intent.getLongArrayExtra(TimerActivity.EXTRA_TIMES);
        setCount = intent.getIntExtra(TimerActivity.EXTRA_SETCOUNT, 1);

        //set the bpms
        ((TextView) findViewById(R.id.tvMainBPM)).setText("" + bpms[0] + " BPM");
        ((TextView) findViewById(R.id.tvSecBPM)).setText("" + bpms[1] + " BPM");
        ((TextView) findViewById(R.id.tvRestBPM)).setText("" + bpms[2] + " BPM");

        //set the times
        totTimes[0] = (int) times[0] * setCount / 1000;
        totTimes[1] = (int) times[1] * setCount / 1000;
        totTimes[2] = (int) times[2] * setCount / 1000;
        totTimes[3] = totTimes[0] + totTimes[1] + totTimes[2];
        ((TextView) findViewById(R.id.tvMainTime)).setText("" + totTimes[0] + " s");
        ((TextView) findViewById(R.id.tvSecTime)).setText("" + totTimes[1] + " s");
        ((TextView) findViewById(R.id.tvRestTime)).setText("" + totTimes[2] + " s");
        ((TextView) findViewById(R.id.tvTotalTime)).setText("" + totTimes[3] + " s");

        //set the strokes
        totStrokes[0] = totTimes[0] * bpms[0] / 60;
        totStrokes[1] = totTimes[1] * bpms[1] / 60;
        totStrokes[2] = totTimes[2] * bpms[2] / 60;
        totStrokes[3] = totStrokes[0] + totStrokes[1] + totStrokes[2];
        ((TextView) findViewById(R.id.tvMainStroke)).setText("" + totStrokes[0] + " strokes");
        ((TextView) findViewById(R.id.tvSecStroke)).setText("" + totStrokes[1] + " strokes");
        ((TextView) findViewById(R.id.tvRestStroke)).setText("" + totStrokes[2] + " strokes");
        ((TextView) findViewById(R.id.tvTotalStroke)).setText("" + totStrokes[3] + " strokes");

        //set the avg
        if (totTimes[3] != 0)
            ((TextView) findViewById(R.id.tvAvgBPM)).setText("" + 60 * totStrokes[3] / totTimes[3] + " BPM");
        else
            ((TextView) findViewById(R.id.tvAvgBPM)).setText("0 BPM");
    }
}
