package com.huriyo.Ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.huriyo.R;
import com.huriyo.Utility.Constants;

public class AboutUsActivity extends BaseActivity {

    LinearLayout llBack, abFb, abInsta, abTwitter, abPinterest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        llBack = (LinearLayout) findViewById(R.id.llBack);
        abFb = (LinearLayout) findViewById(R.id.abFb);
        abInsta = (LinearLayout) findViewById(R.id.abInsta);
        abTwitter = (LinearLayout) findViewById(R.id.abTwitter);
        abPinterest = (LinearLayout) findViewById(R.id.abPinterest);

        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        abFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(Constants.FACEBOOK));
                startActivity(i);
            }
        });

        abInsta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(Constants.INSTAGRAM));
                startActivity(i);
            }
        });

        abTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(Constants.TWITTER));
                startActivity(i);
            }
        });

        abPinterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(Constants.PINTREST));
                startActivity(i);
            }
        });
    }
}
