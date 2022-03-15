package com.bingo.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    EditText editTextPhone, editTextPassword;
    Button identifyButton, loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // find view
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        identifyButton = (Button) findViewById(R.id.identifyButton);
        loginButton = (Button) findViewById(R.id.loginButton);

        // btn identify
        identifyButton.setOnClickListener((View view)->{

        });

        // btn login
        loginButton.setOnClickListener((View view)->{

        });
    }
}