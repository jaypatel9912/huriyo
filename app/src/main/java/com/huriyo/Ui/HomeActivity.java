package com.huriyo.Ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.huriyo.Fragment.AddPostFragment;
import com.huriyo.Fragment.HomeFragment;
import com.huriyo.Fragment.NotificationFragment;
import com.huriyo.Fragment.ProfileFragment;
import com.huriyo.Fragment.SearchFragment;
import com.huriyo.Fragment.SettingFragment;
import com.huriyo.Model.BasicResponse;
import com.huriyo.Model.User;
import com.huriyo.Model.UserDetails;
import com.huriyo.R;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.Globals;
import com.huriyo.Utility.ImagePickerActivity;
import com.huriyo.Utility.Utils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends LocationBaseActivity {

    TextView tvCounter;
    User currUser;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(HomeActivity.this, R.layout.activity_home);

        tvCounter = (TextView) findViewById(R.id.tvCounter);
        User userDetails = new Gson().fromJson(Utils.getPreference(HomeActivity.this, Constants.MM_UserDate), User.class);
        if (userDetails != null && userDetails.notification_count > 0) {
            tvCounter.setVisibility(View.VISIBLE);
            tvCounter.setText(String.valueOf(userDetails.notification_count));
        } else {
            tvCounter.setVisibility(View.GONE);
            tvCounter.setText(String.valueOf(0));
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new HomeFragment().newInstance(), "home");
        transaction.commit();

        initTabBar();
        switchFragment(HomeFragment.newInstance());
        updateDeviceToken();


        currUser = new Gson().fromJson(Utils.getPreference(getActivity(), Constants.MM_UserDate), User.class);
        if (currUser != null && currUser.user_type > 0 && (currUser.business_name == null || currUser.businessCategoryInfo == null)) {
            getUserDetails();
        } else {
            checkMenuVisibility();
        }
    }

    private void getUserDetails() {

        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_user_id, currUser._id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Globals.initRetrofit(getActivity()).getUserDetails(object).enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                if (response.isSuccessful() && response.body() != null && response.body().response_code == 200) {
                    if (response.body() != null) {
                        currUser.business_name = response.body().user.business_name;
                        currUser.business_description = response.body().user.business_description;
                        currUser.businessCategoryInfo = response.body().user.businessCategoryInfo;
                        currUser.about_me = response.body().user.about_me;
                        Utils.setPreference(HomeActivity.this, Constants.MM_UserDate, new Gson().toJson(currUser));
                        checkMenuVisibility();
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkMenuVisibility() {
        if (currUser.getUser_type() == 0) {
            llNewPost.setVisibility(View.GONE);
        } else {
            llNewPost.setVisibility(View.VISIBLE);
        }
    }

    private void initTabBar() {
        if (this.llHome != null) {
            this.llHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switchFragment(HomeFragment.newInstance());
                }
            });
        }
        if (this.llSearch != null) {
            this.llSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switchFragment(SearchFragment.newInstance());
                }
            });
        }
        if (this.llNewPost != null) {
            this.llNewPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switchFragment(AddPostFragment.newInstance());
                }
            });
        }
        if (this.llNotification != null) {
            this.llNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switchFragment(NotificationFragment.newInstance());
                }
            });
        }
        if (this.llSetting != null) {
            this.llSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switchFragment(SettingFragment.newInstance());
                }
            });
        }
    }


    public void selectHome() {
        switchFragment(HomeFragment.newInstance());
    }

    @Override
    public void getLocation(Location mLastLocation) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }


    private void updateDeviceToken() {
        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_device_token, Utils.getPreference(HomeActivity.this, Constants.MM_noti_token));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Globals.initRetrofit(getActivity()).addUpdateAndroidDeviceToken(object).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


}
