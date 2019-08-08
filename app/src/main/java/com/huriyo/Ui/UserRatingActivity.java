package com.huriyo.Ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.huriyo.Adapter.UserRatingAdapter;
import com.huriyo.Model.UserNotification;
import com.huriyo.Model.UserRating;
import com.huriyo.R;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.Globals;
import com.huriyo.Utility.Utils;
import com.willy.ratingbar.ScaleRatingBar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRatingActivity extends BaseActivity {

    RecyclerView recyclerRating;
    LinearLayout llBack;
    TextView tvNodata;
    UserRatingAdapter adapter;
    int skip = 0, limit = 10;
    String userId;
    LinearLayout llMain;
    ScaleRatingBar simpleRatingBar;
    TextView tvWriteAbout;
    EditText edReview;
    Button btnWriteReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_rating);

        userId = getIntent().getExtras().getString(Constants.MM_user_id);
        if (userId == null || userId.isEmpty())
            finish();

        llMain = (LinearLayout) findViewById(R.id.llMain);
        simpleRatingBar = (ScaleRatingBar) findViewById(R.id.simpleRatingBar);
        tvWriteAbout = (TextView) findViewById(R.id.tvWriteAbout);
        edReview = (EditText) findViewById(R.id.edReview);
        btnWriteReview = (Button) findViewById(R.id.btnWriteReview);


        llBack = (LinearLayout) findViewById(R.id.llBack);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        adapter = new UserRatingAdapter(UserRatingActivity.this);
        recyclerRating = (RecyclerView) findViewById(R.id.recyclerRating);
        tvNodata = (TextView) findViewById(R.id.tvNodata);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(UserRatingActivity.this);
        recyclerRating.setLayoutManager(mLayoutManager);
        recyclerRating.setItemAnimator(new DefaultItemAnimator());

        recyclerRating.setAdapter(adapter);

        if (getIntent().getExtras().containsKey(Constants.MM_give_rate)) {
            llMain.setVisibility(View.VISIBLE);
            tvWriteAbout.setText("Write about " + getIntent().getExtras().getString(Constants.MM_user_name));
        }


        btnWriteReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (simpleRatingBar.getRating() <= 0)
                    return;

                saveUserRating();
            }
        });

        getUserRatings();
    }


    boolean reviewGiven = false;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(Constants.MM_give_rate, reviewGiven);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void getUserRatings() {
        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_user_id, userId);
            object.addProperty(Constants.MM_skip, skip);
            object.addProperty(Constants.MM_limit, limit);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utils.showProgressDialog(UserRatingActivity.this);
        Globals.initRetrofit(UserRatingActivity.this).userRatingList(object).enqueue(new Callback<UserRating>() {
            @Override
            public void onResponse(Call<UserRating> call, Response<UserRating> response) {
                Utils.closeProgressDialog();
                if (response.body().response_code == 200) {
                    if (response.body().ratings != null && response.body().ratings.size() > 0) {
                        adapter.addAll(response.body().ratings, response.body().user_media_base_url);
                        skip += 10;
                        limit += 10;
                        tvNodata.setVisibility(View.GONE);
                        recyclerRating.setVisibility(View.VISIBLE);
                    }

                    if (adapter.getItemCount() <= 0) {
                        tvNodata.setVisibility(View.VISIBLE);
                        recyclerRating.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserRating> call, Throwable t) {
                Utils.closeProgressDialog();
                t.printStackTrace();
            }
        });
    }

    private void saveUserRating() {
        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_user_id, userId);
            object.addProperty(Constants.MM_rating, simpleRatingBar.getRating());
            object.addProperty(Constants.MM_description, edReview.getText().toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utils.showProgressDialog(UserRatingActivity.this);
        Globals.initRetrofit(UserRatingActivity.this).saveUserRating(object).enqueue(new Callback<UserRating>() {
            @Override
            public void onResponse(Call<UserRating> call, Response<UserRating> response) {
                Utils.closeProgressDialog();
                if (response.body().response_code == 200) {
                    reviewGiven = true;
                    llMain.setVisibility(View.GONE);
                    getUserRatings();
                }
            }

            @Override
            public void onFailure(Call<UserRating> call, Throwable t) {
                Utils.closeProgressDialog();
                t.printStackTrace();
            }
        });
    }
}
