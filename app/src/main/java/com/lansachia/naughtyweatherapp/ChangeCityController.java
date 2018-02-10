package com.lansachia.naughtyweatherapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class ChangeCityController extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setting layout to controller
        setContentView(R.layout.change_city_layout);

        //managing edit city field
        final EditText editText = (EditText)findViewById(R.id.queryET);
        ImageButton imageButtonBack = (ImageButton)findViewById(R.id.backButton);

        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //setting an onEditorActionListener to grab city name entered by user
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                //city name in string to contain user cityName
                String cityName = editText.getText().toString();

                //intent to navigate back to previous activity
                Intent intentCityName = new Intent(ChangeCityController.this, WeatherController.class);

                //adding an extra to intent for update by the WeatherController(to be handled in onResume() in WeatherController.java)
                intentCityName.putExtra("cityNameExtra", cityName);

                //lunching weather controller activity after adding intent
                startActivity(intentCityName);
                return false;
            }
        });
    }
}
