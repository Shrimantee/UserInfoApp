package com.shrimantee.userInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
  Splash activity displayed at application launch.

 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        /****** Create Thread that will sleep for 3 seconds****/
        Thread background = new Thread() {
            public void run() {
                try {
                    // Thread will sleep for 3 seconds
                    sleep(3*1000);

                    // After 5 seconds redirect to another intent
                    Intent i=new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(i);

                    //Remove activity
                    finish();
                } catch (Exception e) {
                }
            }
        };
        // start thread
        background.start();


    }
}
