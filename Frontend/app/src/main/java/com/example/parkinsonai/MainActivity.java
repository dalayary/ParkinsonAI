package com.example.parkinsonai;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private LottieAnimationView btnMic;
    private LottieAnimationView playView;
    private AppBarConfiguration mAppConfig;
    private Button btnSub, btnPlay;
    private boolean isRecording = false;
    private BottomNavigationView bot_view;
    private NavigationView navigationView;
    private MediaRecorder mediaRecorder;
    private static String fileName = null;
    private boolean isPlaying = false;
    private MediaPlayer player = null;
    private int PERMISSION_CODE = 21;
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private String recordFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assign variables
        bot_view = findViewById(R.id.bot_View);
        playView = findViewById(R.id.playView);
        btnMic = findViewById(R.id.recordView);
        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //change record button
                if (isRecording) {
                    //Stop Recording
                    stopRecording();

                    //set Recording state to false
                    isRecording = false;

                } else {
                    //Check permission to record audio
                    if (checkPermissions()) {
                        //Start Recording
                        startRecording();

                        //set Recording state to false
                        isRecording = true;
                    }
                }
            }
        });

        //assign toolbar
        Toolbar toolbar = findViewById(R.id.tb);
        setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        //Appbar configuration
        mAppConfig = new AppBarConfiguration.Builder(
                R.id.navdraw_profile, R.id.navdraw_home)
                .setDrawerLayout(drawerLayout)
                .build();


        btnSub = findViewById(R.id.btnsub);
        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MultiPartRequest();
                //Create instance
                OkHttpClient client = new OkHttpClient();

                //Build Request
                //emulator request
                Request request = new Request.Builder().url("http://10.0.2.2:5000/").build();
                //localhost request
                //Request request = new Request.Builder().url("http://192.168.178.34:5000/").build();
                //Call request
                client.newCall(request).enqueue(new Callback() {
                    //callback fails
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    //callback succeed
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Audio sent and analysed", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

            }
        });


        btnPlay = findViewById(R.id.btnplay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay();
            }
        });


        //Bottom View Navigation
        bot_view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_profile:
                        Intent intent = new Intent(MainActivity.this, fragment_profile.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_home:
                        break;
                    case R.id.navigation_result:
                        Intent intent3 = new Intent(MainActivity.this, fragment_result.class);
                        startActivity(intent3);
                        break;
                }
                return false;
            }
        });


        //Navigation drawer Items
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.navdraw_profile:
                        Intent intent = new Intent(MainActivity.this, fragment_profile.class);
                        startActivity(intent);
                        break;
                    case R.id.navdraw_hospitals:
                        Intent intent3 = new Intent(MainActivity.this, fragment_hospitals.class);
                        startActivity(intent3);
                        break;
                    case R.id.navdraw_result:
                        Intent intent4 = new Intent(MainActivity.this, fragment_result.class);
                        startActivity(intent4);
                        break;
                    case R.id.navdraw_settings:
                        Intent intent5 = new Intent(MainActivity.this, fragment_settings.class);
                        startActivity(intent5);
                        break;

                }
                return false;
            }
        });

    }

    public void onPlay() {
        if (isPlaying) {
            stopPlaying();
        } else {
            startPlaying();
        }
    }

    public void startPlaying() {

        // show animation
        playView.setVisibility(View.VISIBLE);

        // change button text
        btnPlay.setText(R.string.stop);

        // play audio
        player = new MediaPlayer();
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        try {
            player.setDataSource(path + "/RecVoiceParkinson.wav");
            player.prepare();
            player.start();
            player.setOnCompletionListener(mp -> {
                stopPlaying();
            });
        }
        // Handle the error
        catch (IOException e) {
            e.printStackTrace();
        }

        isPlaying = !isPlaying;
    }

    public void stopPlaying() {

        // hide animation
        playView.setVisibility(View.INVISIBLE);

        // change button text
        btnPlay.setText(R.string.play);

        // stop player
        player.release();
        player = null;

        isPlaying = !isPlaying;
    }


    //function start recording
    private void startRecording() {

        //start animation
        btnMic.playAnimation();

        //Get app external directory path
        File recordPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);

        //initialize filename variable
        recordFile = "RecVoiceParkinson" + ".wav";

        //Setup Media Recorder for recording
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioSamplingRate(48000);

        //Start Recording
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        }
        // Handle the error
        catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //check permission
    private boolean checkPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //permission granted
            int result = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        } else {
            //Permission not granted, ask for permission
            ActivityCompat.requestPermissions(this, new String[]{recordPermission}, PERMISSION_CODE);
            return false;
        }
    }

    //Arrow to go back to home screen
    @Override
    public void onBackPressed() {
        //Assign drawer variable
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //stop recording
    private void stopRecording() {

        //Cancel Animation
        btnMic.cancelAnimation();
        btnMic.setProgress(0f);

        try {
            //Stop media recorder and set it to null for further use to record new audio
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        // Handle the error
        catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onStop() {
        super.onStop();
        if (isRecording) {
            stopRecording();
        }
    }


    //Play Recorded Audio
    private void playAudio() {
        //Setup Media Player
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            //select the path
            mediaPlayer.setDataSource(Environment.DIRECTORY_MUSIC);
            //play audio
            mediaPlayer.prepare();
            mediaPlayer.start();
        }
        // Handle the error
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        if (isRecording == true) {
            playAudio();
        }
    }


    //Http request that clients construct to send files and data over a http server
    private void MultiPartRequest() {
        //initialize path
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        Log.d("Audio", "Path: " + path);
        //assign file variable
        File f = new File(String.valueOf(path));
        //store files into an array
        File file[] = f.listFiles();
        Log.d("Files", "Size: " + file.length);
        //loop through the files array
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                Log.d("OKHTTP3_VJ_FILES", "Filename: " + file[i].getName());
                uploadFile(file[i]);
                break;
            }
        }
    }

    //upload file
    public static Boolean uploadFile(File file) {
        //Create instance
        OkHttpClient client = new OkHttpClient();
        //Initialize url
        //emulator
        String url = "http://10.0.2.2:5000";
        //localhost
        //String url = "http://192.168.178.34:5000";
        //build Request body
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("audio", file.getName(),
                        RequestBody.create(MediaType.parse("audio/x-wav"), file))
                .build();
        //build a request
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        try {
            //call the request
            client.newCall(request).enqueue(new Callback() {
                //callback failed
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }
                //callback succeed
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Log.d("OKHTTP3_VJ_FILES", "Request successful");
                    Log.d("OKHTTP3_VJ_FILES", response.body().string());

                }
            });


        } catch (Exception ex) {
            // Handle the error
            Log.d("OKHTTP3_VJ_FILES", "Exception occured: " + ex.toString());
            ex.printStackTrace();
        }
        return false;
    }
}

