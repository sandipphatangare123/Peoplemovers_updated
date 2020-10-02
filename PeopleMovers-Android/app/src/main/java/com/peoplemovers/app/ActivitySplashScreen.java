package com.peoplemovers.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.util.Util;


public class ActivitySplashScreen extends Activity {

    boolean isLogin = false;
    int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        isLogin = sharedPreferences.getBoolean("isVisited", false);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        Log.e(Util.TAG, "In activity"+isLogin);

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (!isLogin) {
                        Intent i = new Intent(ActivitySplashScreen.this, ActiivityIntrouductionScreen.class);
                        i.putExtras(intent);
                        startActivity(i);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
            } else {
                        Intent i = new Intent(ActivitySplashScreen.this, ActivitySharing.class);
                         i.putExtras(intent);
                        startActivity(i);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();

        }
        } else {
            if (!isLogin) {
                new Handler().postDelayed(new Runnable() {
                    /*
                     * Showing splash screen with a timer. This will be useful when you
                     * want to show case your app logo / company
                     */
                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        // Start your app main activity
                        Intent i = new Intent(ActivitySplashScreen.this, ActiivityIntrouductionScreen.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();

                    }
                }, 3000);
            } else {
                new Handler().postDelayed(new Runnable() {
                    /*
                     * Showing splash screen with a timer. This will be useful when you
                     * want to show case your app logo / company
                     */
                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        // Start your app main activity

                        Intent i = new Intent(ActivitySplashScreen.this, ActivityHome.class);
                        i.putExtra("data","false");
                        startActivity(i);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();

                    }
                }, 100);
            }
        }
    }
}
