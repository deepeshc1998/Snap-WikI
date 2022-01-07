package com.example.snapwiki2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.snapwiki2.databinding.ActivitySplashScreenBinding;

public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_TIMER=5000;

   ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding=ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.lottieAnimation.animate().translationY(2000).setDuration(1000).setStartDelay(4000);
        //new handler

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(getApplicationContext(), MainActivity.class));

                finish();

            }
        },SPLASH_TIMER);

    }
}