package com.codetravel.mediarecorder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

public class StartActivity extends AppCompatActivity {

    private Button startButton;
    private Button trainingButton;
    private VoiceGuide vg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        startButton = (Button)findViewById(R.id.startButton);
        trainingButton = (Button)findViewById(R.id.trainingButton);

        vg = new VoiceGuide(this);

        trainingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                StartActivity.this.startActivity(intent);
            }
        });
    }
}
