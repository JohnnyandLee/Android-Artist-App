package com.example.myapplicationtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class SplashActivity extends AppCompatActivity {
    private ProgressBar spinner;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        spinner = (ProgressBar)findViewById(R.id.progressBar);
        img = findViewById(R.id.imageView);

        spinner.setVisibility(View.GONE);

        img.postDelayed(new Runnable() {
            public void run() {
                img.setVisibility(View.GONE);
            }
        }, 1000);

        spinner.postDelayed(new Runnable() {
            public void run() {
                spinner.setVisibility(View.VISIBLE);
            }
        }, 1000);

        Thread thread = new Thread(){

            @Override
            public void run(){

                try{
                    sleep(2000);
                }catch(Exception e){
                    e.printStackTrace();
                }finally {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
            }
        };
        thread.start();
    }
}