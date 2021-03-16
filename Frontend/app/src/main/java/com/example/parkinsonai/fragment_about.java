package com.example.parkinsonai;

import android.graphics.Paint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class fragment_about extends AppCompatActivity {
    TextView miss_txt, exp_txt, steps_txt;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_about);

        toolbar = findViewById(R.id.tb);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        miss_txt = findViewById(R.id.abt_mission_txt);
        miss_txt.setPaintFlags(miss_txt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        exp_txt = findViewById(R.id.abt_exp_txt);
        exp_txt.setPaintFlags(exp_txt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        steps_txt = findViewById(R.id.abt_steps_txt);
        steps_txt.setPaintFlags(steps_txt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

    }
}
