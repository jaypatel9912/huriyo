package com.huriyo.Ui;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.huriyo.Fragment.HomeFragment;
import com.huriyo.R;
import com.huriyo.Utility.Constants;

public class UserPostsActivity extends BaseActivity {

    HomeFragment homeFragment;
    LinearLayout llBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_posts);

        Bundle bundle = new Bundle();
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(Constants.MM_user_id)) {
            bundle.putString(Constants.MM_user_id, getIntent().getExtras().getString(Constants.MM_user_id));
        }

        llBack = (LinearLayout) findViewById(R.id.llBack);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        homeFragment = new HomeFragment();
        homeFragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.frame_layout, homeFragment).commit();

    }
}

