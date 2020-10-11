package com.example.regischarles.locationbased;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ControlActivity extends AppCompatActivity implements View.OnClickListener {
    Button trackChild, showChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        trackChild = findViewById(R.id.beginTracking);
        showChild = findViewById(R.id.showChild);

        trackChild.setOnClickListener(this);
        showChild.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.showChild:
                startActivity(new Intent(this, MapsActivity.class));
                break;
            case R.id.beginTracking:
                startActivity(new Intent(this, MainActivity.class));
                break;


        }
    }
}
