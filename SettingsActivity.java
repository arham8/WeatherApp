package com.example.assignmentweatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity {

    Button celcius, fahrenhite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        celcius = findViewById(R.id.celcius_button);
        fahrenhite = findViewById(R.id.fahrenheit_button);

        celcius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                prefs.edit().putString("conversion_formate", "celcius").commit();

                Intent i = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        fahrenhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                prefs.edit().putString("conversion_formate", "fahrenhite").commit();

                Intent i = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }
}