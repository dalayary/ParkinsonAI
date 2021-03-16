package com.example.parkinsonai;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;



public class fragment_profile extends AppCompatActivity {

    Button btnsave, btn_delete;
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    TextInputEditText txt_firstname;
    TextInputEditText txt_lastname;
    TextInputEditText txt_age;
    TextInputEditText txt_docmail;
    TextInputEditText txt_doc;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        //Assign variables
        toolbar = findViewById(R.id.tb);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn_delete = findViewById(R.id.btn_delete);

        //assign TextInputEditText and set text
        txt_firstname = (TextInputEditText) findViewById(R.id.text_FirstName);
        txt_firstname.setText(getApplication().getSharedPreferences("First Name", Context.MODE_PRIVATE).getString("txt_firstname", ""));


        txt_lastname = (TextInputEditText) findViewById(R.id.text_LastName);
        txt_lastname.setText(getApplication().getSharedPreferences("Last Name", Context.MODE_PRIVATE).getString("txt_lastname", ""));

        txt_age = (TextInputEditText) findViewById(R.id.text_BirthDate);
        txt_age.setText(getApplication().getSharedPreferences("Age", Context.MODE_PRIVATE).getString("txt_age", ""));

        txt_doc = (TextInputEditText) findViewById(R.id.text_FamDoc);
        txt_doc.setText(getApplication().getSharedPreferences("Doctor", Context.MODE_PRIVATE).getString("txt_doc", ""));

        txt_docmail = (TextInputEditText) findViewById(R.id.text_emaildoc);
        txt_docmail.setText(getApplication().getSharedPreferences("DocMail", Context.MODE_PRIVATE).getString("txt_docmail", ""));

        //bottom view
        bottomNavigationView = findViewById(R.id.bot_view_profile);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.navigation_home:
                        Intent intent = new Intent(fragment_profile.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_profile:
                        break;
                    case R.id.navigation_result:
                        Intent intent2 = new Intent(fragment_profile.this, fragment_result.class);
                        startActivity(intent2);
                        break;
                }
                return false;
            }
        });

        //Save button
        btnsave = findViewById(R.id.btnsave);
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                getApplication().getSharedPreferences("First Name", Context.MODE_PRIVATE).edit().putString("txt_firstname", fragment_profile.this.txt_firstname.getText().toString()).commit();
                getApplication().getSharedPreferences("Last Name", Context.MODE_PRIVATE).edit().putString("txt_lastname", fragment_profile.this.txt_lastname.getText().toString()).commit();
                getApplication().getSharedPreferences("Doctor", Context.MODE_PRIVATE).edit().putString("txt_doc", fragment_profile.this.txt_doc.getText().toString()).commit();
                getApplication().getSharedPreferences("DocMail", Context.MODE_PRIVATE).edit().putString("txt_docmail", fragment_profile.this.txt_docmail.getText().toString()).commit();
                getApplication().getSharedPreferences("Age", Context.MODE_PRIVATE).edit().putString("txt_age", fragment_profile.this.txt_age.getText().toString()).commit();

                Intent resintent = new Intent(fragment_profile.this, fragment_result.class);
                resintent.putExtra("first Name", txt_firstname.getText().toString());
                resintent.putExtra("Last Name", txt_lastname.getText().toString());
                resintent.putExtra("Age", txt_age.getText().toString());
                resintent.putExtra("Doctor", txt_doc.getText().toString());
                startActivity(resintent);


                Toast.makeText(fragment_profile.this, "Profile Saved to patient card", Toast.LENGTH_SHORT).show();
            }
        });

        //Delete Button
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialog = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                getApplication().getSharedPreferences("First Name", Context.MODE_PRIVATE).edit().clear().commit();
                                getApplication().getSharedPreferences("Last Name", Context.MODE_PRIVATE).edit().clear().commit();
                                getApplication().getSharedPreferences("Doctor", Context.MODE_PRIVATE).edit().clear().commit();
                                getApplication().getSharedPreferences("DocMail", Context.MODE_PRIVATE).edit().clear().commit();
                                getApplication().getSharedPreferences("Age", Context.MODE_PRIVATE).edit().clear().commit();

                                Toast.makeText(fragment_profile.this, "Profile Deleted", Toast.LENGTH_SHORT).show();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(fragment_profile.this);
                builder.setMessage("Are you sure You want to delete your Profile ?").setPositiveButton("Yes", dialog)
                        .setNegativeButton("No", dialog).show();

            }
        });


    }
}
