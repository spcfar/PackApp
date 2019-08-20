package com.mi.virtual;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.WindowManager;

import com.mi.virtual.abs.ui.VActivity;

public class SplashActivity extends VActivity {
    private static final String TAG = "kkkk";

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        SystemClock.sleep(100);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
//        MainActivity.goHome(this);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "==onDestroy==000==" + System.currentTimeMillis() / 1000);
    }
}
