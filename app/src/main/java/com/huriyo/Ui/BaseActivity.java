package com.huriyo.Ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huriyo.R;
import com.huriyo.Utility.FragmentBase;


public class BaseActivity extends AppCompatActivity {
    int onStartCount = 0;
    private Activity activity;
    private FragmentBase fragmentBase;
    protected LinearLayout llHome;
    protected LinearLayout llSearch;
    protected LinearLayout llNotification;
    protected LinearLayout llNewPost;
    protected LinearLayout llSetting;
    private View view;


    protected void setContentView(Activity activity, int layoutResID) {
        this.activity = activity;
        this.view = View.inflate(activity, layoutResID, null);
        super.setContentView(this.view);
        this.llHome = (LinearLayout) findViewById(R.id.llHome);
        this.llSearch = (LinearLayout) findViewById(R.id.llSearch);
        this.llNewPost = (LinearLayout) findViewById(R.id.llNewPost);
        this.llNotification = (LinearLayout) findViewById(R.id.llNotification);
        this.llSetting = (LinearLayout) findViewById(R.id.llSetting);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onStartCount = 1;
        if (savedInstanceState == null) // 1st tvPosition
        {
            this.overridePendingTransition(R.anim.slide_from_right,
                    R.anim.slide_to_left);
        } else // already created so reverse animation
        {
            onStartCount = 2;
        }
        setStatusBarcolor();
    }


    public void setStatusBarcolor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.statusbar_color));
        }
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        if (onStartCount > 1) {
            this.overridePendingTransition(R.anim.slide_from_left,
                    R.anim.slide_to_right);

        } else if (onStartCount == 1) {
            onStartCount++;
        }
    }


    public View getRootView() {
        return this.view;
    }

    public Activity getActivity() {
        return this.activity;
    }

    public Context getContext() {
        return this.activity;
    }

    public void setFragmentBase(FragmentBase fragmentBase) {
        this.fragmentBase = fragmentBase;
        verifyTabBar(fragmentBase);
    }

    public FragmentBase getFragmentBase() {
        return this.fragmentBase;
    }

    public boolean switchFragment(FragmentBase fragmentBase) {
        return switchFragment(fragmentBase, false);
    }

    public boolean switchFragment(FragmentBase fragmentBase, boolean isAdd) {
        try {
            if (this.fragmentBase == null || this.fragmentBase.getCode() != fragmentBase.getCode()) {
                setFragmentBase(fragmentBase);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if (isAdd) {
                    fragmentTransaction.add((int) R.id.fragment_container, (Fragment) fragmentBase);
                } else {
                    fragmentTransaction.replace(R.id.fragment_container, fragmentBase, fragmentBase.getCode().toString());
                    fragmentTransaction.addToBackStack(fragmentBase.getCode().toString());
                }
                fragmentTransaction.commit();
                fragmentManager.executePendingTransactions();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected void verifyTabBar(FragmentBase fragmentBase) {
        switch (fragmentBase.getCode()) {
            case HOME:
                updateColor(this.llHome, Color.parseColor("#F9C248"));
                updateColor(this.llSearch, Color.parseColor("#869096"));
                updateColor(this.llNewPost, Color.parseColor("#869096"));
                updateColor(this.llNotification, Color.parseColor("#869096"));
                updateColor(this.llSetting, Color.parseColor("#869096"));
                return;
            case SEARCH:
                updateColor(this.llHome, Color.parseColor("#869096"));
                updateColor(this.llSearch, Color.parseColor("#F9C248"));
                updateColor(this.llNewPost, Color.parseColor("#869096"));
                updateColor(this.llNotification, Color.parseColor("#869096"));
                updateColor(this.llSetting, Color.parseColor("#869096"));
                return;
            case NEW_POST:
                updateColor(this.llHome, Color.parseColor("#869096"));
                updateColor(this.llSearch, Color.parseColor("#869096"));
                updateColor(this.llNewPost, Color.parseColor("#F9C248"));
                updateColor(this.llNotification, Color.parseColor("#869096"));
                updateColor(this.llSetting, Color.parseColor("#869096"));
                return;
            case NOTIFICATION:
                updateColor(this.llHome, Color.parseColor("#869096"));
                updateColor(this.llSearch, Color.parseColor("#869096"));
                updateColor(this.llNewPost, Color.parseColor("#869096"));
                updateColor(this.llNotification, Color.parseColor("#F9C248"));
                updateColor(this.llSetting, Color.parseColor("#869096"));
                return;
            case SETTING:
                updateColor(this.llHome, Color.parseColor("#869096"));
                updateColor(this.llSearch, Color.parseColor("#869096"));
                updateColor(this.llNewPost, Color.parseColor("#869096"));
                updateColor(this.llNotification, Color.parseColor("#869096"));
                updateColor(this.llSetting, Color.parseColor("#F9C248"));
                return;
            default:
                return;
        }
    }

    protected void updateColor(ViewGroup linearLayout, int color) {
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View view = linearLayout.getChildAt(i);
            if (view instanceof ImageView) {
                ((ImageView) view).setColorFilter(null);
                ((ImageView) view).setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            } else if (view instanceof TextView) {
                if (view.getId() != R.id.tvCounter)
                    ((TextView) view).setTextColor(color);
            }
        }
    }

    protected void showToast(String text) {
        if (text != null && getActivity() != null) {
            Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
        }
    }

    protected void showToast(int text) {
        showToast(getString(text));
    }

    public void popBackStack() {
        try {
            getSupportFragmentManager().popBackStackImmediate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void popBackStack(int back) {
        if (back <= 0) {
            for (int i = 0; i < back; i++) {
                getSupportFragmentManager().popBackStackImmediate();
            }
        }
    }

    public void clear() {
        if (getSupportFragmentManager() != null) {
            while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                popBackStack();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (this.fragmentBase != null) {
            this.fragmentBase.onActivityResult(requestCode, resultCode, data);
        }
    }
}
