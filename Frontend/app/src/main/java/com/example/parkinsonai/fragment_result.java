package com.example.parkinsonai;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class fragment_result extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    TextView acc_txt, yn_txt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_result);

        //Assign variables
        toolbar = findViewById(R.id.tb);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        yn_txt = findViewById(R.id.yn_txt);
        OkHttpClient okHttpClient = new OkHttpClient();
        //lh
        //Request request = new Request.Builder().url("http://192.168.178.34:5000/").build();
        //emu
        Request request = new Request.Builder().url("http://10.0.2.2:5000").build();
        
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(fragment_result.this, "failed.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            yn_txt.setText(response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });





        acc_txt = findViewById(R.id.txt_acc_nummer);
        OkHttpClient client = new OkHttpClient();
        //local
       // Request req = new Request.Builder().url("http://192.168.178.34:5000/acc").build();
        //emu
        Request req = new Request.Builder().url("http://10.0.2.2:5000/acc").build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(()-> {
                    Toast.makeText(fragment_result.this, "failed.", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            acc_txt.setText(response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


        //Bottom Navigation
        bottomNavigationView = findViewById(R.id.bot_View_result);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.navigation_home:
                        Intent intent = new Intent(fragment_result.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_profile:
                        Intent intent2 = new Intent(fragment_result.this, fragment_profile.class);
                        startActivity(intent2);
                        break;
                }

                return false;
            }
        });

    }
}

