package com.bingo.android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class ChangeInfoActivity extends AppCompatActivity {
    Button infoButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_info);

        // find view
        infoButton = (Button)findViewById(R.id.infoButton);
    }
}