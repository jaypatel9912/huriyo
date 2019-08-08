package com.huriyo.Ui;

import android.graphics.Bitmap;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.huriyo.Fragment.ProfileFragment;
import com.huriyo.R;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.ImagePickerActivity;

import java.io.File;

public class ProfileActivity extends ImagePickerActivity {

    @Override
    public void getLocation(Location mLastLocation) {
    }

    @Override
    public void onImageSelected(int imgResID, Bitmap bitmap, String encodeImageString, File imageFile) {
        if (profileFragment != null && profileFragment.isVisible()) {
            profileFragment.onImageSelected(imgResID, bitmap, encodeImageString, imageFile);
        }
    }

    ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Bundle bundle = new Bundle();
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(Constants.MM_user_id)) {
            bundle.putString(Constants.MM_user_id, getIntent().getExtras().getString(Constants.MM_user_id));
        }

        profileFragment = new ProfileFragment();
        profileFragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.frame_layout, profileFragment).commit();

    }
}
