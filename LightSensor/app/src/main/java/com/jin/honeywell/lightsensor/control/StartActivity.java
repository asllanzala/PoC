package com.jin.honeywell.lightsensor.control;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jin.honeywell.lightsensor.R;


public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        startWelcomePageThread();

    }


    private void startWelcomePageThread() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(2000);

                    startActivity(new Intent(StartActivity.this, MainActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }
}