package com.example.parkinsonai;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class fragment_settings extends AppCompatActivity {

    Toolbar toolbar;
    TextView mAbout;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);

        toolbar = findViewById(R.id.tb);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAbout = findViewById(R.id.Language_About);
        mAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fragment_settings.this, fragment_about.class);
                startActivity(intent);
            }
        });

        bottomNavigationView = findViewById(R.id.bot_View_settings);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.navigation_home:
                        Intent intent = new Intent(fragment_settings.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_profile:
                        Intent intent1 = new Intent(fragment_settings.this, fragment_profile.class);
                        startActivity(intent1);
                        break;
                    case R.id.navigation_result:
                        Intent intent2 = new Intent(fragment_settings.this, fragment_result.class);
                        startActivity(intent2);
                        break;
                }
                return false;
            }
        });
    }
}
