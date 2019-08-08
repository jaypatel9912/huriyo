package com.huriyo.Ui;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.huriyo.R;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.Utils;

public class SplashActivity extends BaseActivity {

    TextView tvTitle1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        tvTitle1 = (TextView) findViewById(R.id.tvTitle1);
        tvTitle1.setTypeface(Utils.getTypefaceCurly(SplashActivity.this));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Utils.getPreference(SplashActivity.this, Constants.MM_UserDate) != null &&
                                !Utils.getPreference(SplashActivity.this, Constants.MM_UserDate).isEmpty()) {
                            Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        } else {
                            Intent i = new Intent(SplashActivity.this, BussinessUsersActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        }
                    }
                });
            }
        }, 2000);
    }
}
