package com.dboat.dragonboat.dragonboating;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TimerActivity extends Activity {

    private TextView tvBPM;
    private TextView tvTimer;
    private TextView tvStrokeCount;
    private TextView tvSetCount;

    private ConstraintLayout root;

    private ProgressBar pbTimer;

    private long[] times = new long[3];
    private int[] bpms = new int[3];

    private int count = 0;
    private long millisSinceLastBeat = 0;

    private CountDownTimer cdt;
    private CountDownTimer flashTimer;
    private SoundPool sp;
    boolean loaded = false;
    int beatId;
    long beatDuration;

    boolean isPaused = false;

    private int setCount;

    private long strokeCount = 0;

    private final int[] colors = {Color.GREEN, Color.YELLOW, 0xFF00CCFF};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        // Set window fullscreen and remove title bar, and force landscape orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Intent intent = getIntent();
        for(int i = 0; i < 3; i++) {
            times[i] = 1000 * Long.parseLong(intent.getStringExtra(MainActivity.EXTRA_TIME + i));
        }
        for (int i = 0; i < 3; i++) {
            bpms[i] = Integer.parseInt(intent.getStringExtra(MainActivity.EXTRA_BPM + i));
        }

        tvBPM = findViewById(R.id.tvBPM);
        tvTimer = findViewById(R.id.tvTimer);
        pbTimer = findViewById(R.id.pbTimer);
        tvStrokeCount = findViewById(R.id.tvStrokeCount);
        tvSetCount = findViewById(R.id.tvSetCount);

        root = findViewById(R.id.root);

        setCount = Integer.parseInt(intent.getStringExtra(MainActivity.EXTRA_SET)) - 1;
        tvSetCount.setText("" + setCount);

        tvStrokeCount.setText("" + strokeCount + " strokes");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sp = new SoundPool.Builder().build();
        }
        else {
            sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }
        sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });
        beatId = sp.load(this, R.raw.beat, 1);
        MediaPlayer m = MediaPlayer.create(this, R.raw.beat);
        beatDuration = m.getDuration();
        m.release();
        m = null;

        pbTimer.setBackgroundColor(Color.BLACK);

        //start a countdown of 5 seconds before
        root.setBackgroundColor(Color.RED);
        cdt = new CountDownTimer(5000, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                String str = String.format("%d.%03d", (millisUntilFinished / 1000) % 60, millisUntilFinished % 1000);
                tvTimer.setText(str);
            }

            @Override
            public void onFinish() {
                startCDT();
            }
        }.start();
    }

    private void startCDT() {
        millisSinceLastBeat = Long.MAX_VALUE;
        tvBPM.setText("" + bpms[count] + " bpm");
        root.setBackgroundColor(colors[count]);

        long beatPer = bpms[count] != 0 ? 60000 / bpms[count] : Long.MAX_VALUE;
        final float rate = ((float) beatDuration) / beatPer;

        //if (loaded)
        //   sp.play(beatId, 0.5f, 0.5f, 1, (int) (bpms[count] * times[count] / 60000), rate < 1.0f ? rate : 1);

        cdt = new CountDownTimer(times[count], 1) {
            final long maxTime = times[count];
            final long beatPeriod = bpms[count] != 0 ? 60000 / bpms[count] : Long.MAX_VALUE;

            @Override
            public void onTick(long millisUntilFinished) {
                //update timer display
                String str = String.format("%d.%03d", (millisUntilFinished / 1000) % 60, millisUntilFinished % 1000);
                if (millisUntilFinished >= 60000)
                    tvTimer.setText(String.format("%d:%s", millisUntilFinished / 60000, str));
                else
                    tvTimer.setText(str);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    pbTimer.setProgress((int) (millisUntilFinished * 100 / maxTime), true);
                else
                    pbTimer.setProgress((int) (millisUntilFinished * 100 / maxTime));

                //check if beat
                if (millisSinceLastBeat - millisUntilFinished >= beatPeriod) {
                    millisSinceLastBeat = millisUntilFinished;

                    strokeCount++;
                    tvStrokeCount.setText("" + strokeCount + " strokes");

                    if (loaded)
                        sp.play(beatId, 0.5f, 0.5f, 1, 0, /*rate < 1.0f ? rate :*/ 1);

                    //flash the screen and beep
                    root.setBackgroundColor(Color.WHITE);
                    flashTimer = new CountDownTimer(100, 250) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            return;
                        }

                        @Override
                        public void onFinish() {
                            root.setBackgroundColor(colors[count]);
                        }
                    }.start();
                }
            }

            @Override
            public void onFinish() {
                millisSinceLastBeat = Long.MAX_VALUE;
                count++;
                count %= 3;

                //if did all 3
                if (count == 0) {
                    setCount--;
                    tvSetCount.setText("" + setCount);
                }

                if (setCount < 0) {
                    return;
                }

                startCDT();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        sp.stop(beatId);
        sp.release();
        super.onDestroy();
    }
}
