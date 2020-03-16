package com.dboat.dragonboat.dragonboating;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.dboat.dragonboat.dragonboating.MESSAGE";

    public static final String EXTRA_TIME = "com.dboat.dragonboat.dragonboating.TIME";
    public static final String EXTRA_BPM = "com.dboat.dragonboat.dragonboating.BPM";
    public static final String EXTRA_SET = "com.dboat.dragonboat.dragonboating.SET";

    private static final int MAX_BPM = 100;
    private static final int MAX_TIME = 120;

    boolean isText = false;
    boolean isSeek = false;

    private SeekBar[] sbArr = new SeekBar[6];

    private EditText[] etArr = new EditText[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set window fullscreen and remove title bar, and force landscape orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sbArr[0] = (SeekBar) findViewById(R.id.sbTime1);
        sbArr[1] = (SeekBar) findViewById(R.id.sbTime2);
        sbArr[2] = (SeekBar) findViewById(R.id.sbTime3);
        sbArr[3] = (SeekBar) findViewById(R.id.sbBPM1);
        sbArr[4] = (SeekBar) findViewById(R.id.sbBPM2);
        sbArr[5] = (SeekBar) findViewById(R.id.sbBPM3);

        etArr[0] = (EditText) findViewById(R.id.time1);
        etArr[1] = (EditText) findViewById(R.id.time2);
        etArr[2] = (EditText) findViewById(R.id.time3);
        etArr[3] = (EditText) findViewById(R.id.bpm1);
        etArr[4] = (EditText) findViewById(R.id.bpm2);
        etArr[5] = (EditText) findViewById(R.id.bpm3);

        for (int i  =0; i < 6; i++) {
            final int indx = i;

            etArr[i].setText("0");
            etArr[i].setCursorVisible(true);

            sbArr[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (!isText) {
                        etArr[indx].setText("" + progress * (indx < 3 ? MAX_TIME : MAX_BPM) / 100);
                        isSeek = true;
                    }
                    isText = false;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    return;
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    return;
                }
            });

            etArr[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    return;
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!isSeek) {
                        try {
                            sbArr[indx].setProgress(Integer.parseInt(s.toString()) * 100/ (indx < 3 ? MAX_TIME : MAX_BPM));
                        } catch (Exception e) {
                            sbArr[indx].setProgress(0);
                        }
                        isText = true;
                    }
                    isSeek = false;
                }

                @Override
                public void afterTextChanged(Editable s) {
                    return;
                }
            });
        }

        ((EditText) findViewById(R.id.etSet)).setText("1");
    }

    public void startTimer(View view) {
        Intent intent = new Intent(this, TimerActivity.class);

        String message;
        for (int i = 0; i < 6; i++) {
            message = etArr[i].getText().toString();
            intent.putExtra((i < 3 ? EXTRA_TIME : EXTRA_BPM) + (i % 3), message);
        }

        message = ((EditText) findViewById(R.id.etSet)).getText().toString();
        intent.putExtra(EXTRA_SET, message);

        startActivity(intent);
    }
}
