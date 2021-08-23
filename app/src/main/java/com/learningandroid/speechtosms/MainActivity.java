package com.learningandroid.speechtosms;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    ImageView mic;
    EditText textSMS, mobileNO;
    Button sendBtn;
    SmsManager smsManager;
    private static final int requestCode_Speech_input = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mobileNO = findViewById(R.id.mobileNO);
        mic = findViewById(R.id.mic);
        textSMS = findViewById(R.id.textSMS);
        sendBtn = findViewById(R.id.sendBtn);
        smsManager = SmsManager.getDefault();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS);

                if(permissionCheck == PackageManager.PERMISSION_GRANTED){
                    SendSMS();
                }else{
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 0);
                }
            }
        });

        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to Text");

                try {
                    startActivityForResult(i, requestCode_Speech_input);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, " " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestCode_Speech_input) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                textSMS.setText(result.get(0));
            }
        }
    }

    private void SendSMS() {

        String phoneNo = mobileNO.getText().toString().trim();
        String message = textSMS.getText().toString();
        if (!mobileNO.getText().toString().equals("") || !textSMS.getText().toString().equals("")){
            smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo,null, message, null, null);
            Toast.makeText(MainActivity.this, "Message sent!!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MainActivity.this, "Please enter details!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case 0:
                if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    SendSMS();
                }else{
                    Toast.makeText(MainActivity.this, "You don't have required permissions", Toast.LENGTH_SHORT).show();
                }

        }
    }
}