package com.hamsoftug.birduganda;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        Thread Delay = new Thread() {
            @Override
            public void run() {
                try {
                    // loading time set
                    sleep(3000);

                } catch (InterruptedException e) {
                    e.printStackTrace();

                } finally {
                    startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                    finish();
                }
            }

        };
        Delay.start();

    }

}
