package com.huriyo.Ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.huriyo.Adapter.BussinessUsersAdapter;
import com.huriyo.Fragment.AddPostFragment;
import com.huriyo.Fragment.HomeFragment;
import com.huriyo.Fragment.NotificationFragment;
import com.huriyo.Fragment.SearchFragment;
import com.huriyo.Fragment.SettingFragment;
import com.huriyo.Model.BussinessUser;
import com.huriyo.Model.Category;
import com.huriyo.Model.User;
import com.huriyo.R;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.Globals;
import com.huriyo.Utility.GridSpacingItemDecoration;
import com.huriyo.Utility.RecyclerItemClickListener;
import com.huriyo.Utility.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BussinessUsersActivity extends LocationBaseActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            AlertDialog.Builder builder = new AlertDialog.Builder(BussinessUsersActivity.this);
            builder.setTitle(R.string.login_needed);
            builder.setMessage(R.string.do_sign_in_now);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    Intent i = new Intent(BussinessUsersActivity.this, MainActivity.class);
                    startActivity(i);
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
    };

    BottomNavigationView navigation;
    RecyclerView recyclerView;
    TextView noData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bussiness_users);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        noData = (TextView) findViewById(R.id.noData);
        BottomNavigationMenuView menuView = (BottomNavigationMenuView)
                navigation.getChildAt(0);

        if (new Gson().fromJson(Utils.getPreference(BussinessUsersActivity.this, Constants.MM_UserDate), User.class) != null) {
            navigation.setVisibility(View.GONE);
        }
        try {
            Field shiftingMode = menuView.getClass()
                    .getDeclaredField("mShiftingMode");

            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);

            for (int i = 0; i < menuView.getChildCount(); i++) {

                BottomNavigationItemView item =
                        (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShiftingMode(false);
                //To update view, set the checked value again
                item.setChecked(item.getItemData().isChecked());
            }

        } catch (NoSuchFieldException e) {
            e.printStackTrace();

        } catch (IllegalAccessException e) {
            e.printStackTrace();

        } catch (SecurityException e) {
            e.printStackTrace();
        }

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        GridLayoutManager manager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager); // set LayoutManager to RecyclerView
        int spanCount = 2; // 3 columns
        int spacing = 20; // 50px
        boolean includeEdge = true;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        customAdapter = new BussinessUsersAdapter(BussinessUsersActivity.this);
        recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(BussinessUsersActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(BussinessUsersActivity.this, ProfileActivity.class);
                        intent.putExtra(Constants.MM_user_id, customAdapter.getData().get(position)._id);
                        startActivity(intent);
                    }
                }));
    }

    @Override
    public void getLocation(Location mLastLocation) {

    }


    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);

        this.mLastLocation = location;
    }

    BussinessUsersAdapter customAdapter;
    int skip = 0, limit = 10;
    Location mLastLocation;

    @Override
    public void onResume() {
        super.onResume();

        if (globals == null) {
            globals = ((Globals) getApplicationContext());
        }

        getBussinessUsers();
    }

    private void getBussinessUsers() {

        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_latitude, globals == null ? 0.0 : globals.getLatitude());
            object.addProperty(Constants.MM_longitude, globals == null ? 0.0 : globals.getLongitude());
            object.addProperty(Constants.MM_limit, limit);
            object.addProperty(Constants.MM_skip, skip);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.showProgressDialog(BussinessUsersActivity.this);
        Globals.initRetrofit(BussinessUsersActivity.this).businessUserList(object).enqueue(new Callback<BussinessUser>() {
            @Override
            public void onResponse(Call<BussinessUser> call, Response<BussinessUser> response) {
                Utils.closeProgressDialog();
                if (response.isSuccessful()) {
                    skip += 10;
                    limit += 10;

                    if (response.body().response_code == 200) {
                        if (response.body().businesses != null && response.body().businesses.size() > 0) {
                            customAdapter.doRefresh(response.body().businesses, response.body().user_media_base_url);
                            skip += 10;
                            limit += 10;
                            noData.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                }

                if (customAdapter.getData() == null || customAdapter.getData().size() <= 0) {
                    noData.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<BussinessUser> call, Throwable t) {
                Utils.closeProgressDialog();
                noData.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });
    }

}
