package com.huriyo.Ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.huriyo.Adapter.FriendRequestAdapter;
import com.huriyo.Model.Friend;
import com.huriyo.Model.FriendRequest;
import com.huriyo.Model.SearchUser;
import com.huriyo.Model.User;
import com.huriyo.R;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.Globals;
import com.huriyo.Utility.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFriendsActivity extends BaseActivity implements View.OnClickListener {

    LinearLayout llBack;
    RadioButton btnRequest, btnFriends;
    RecyclerView recyclerView;

    String userId;
    TextView noData;
    FriendRequestAdapter requestAdapter;
    FriendRequestAdapter friendsAdapter;
    boolean showOptions = true;
    int type = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_friends);

        if (getIntent().getExtras().containsKey(Constants.MM_user_id))
            userId = getIntent().getExtras().getString(Constants.MM_user_id);

        if (getIntent().getExtras().containsKey(Constants.MM_type))
            type = getIntent().getExtras().getInt(Constants.MM_type);

        if (userId == null || userId.isEmpty())
            finish();

        String userIdCurrent = new Gson().fromJson(Utils.getPreference(UserFriendsActivity.this, Constants.MM_UserDate), User.class)._id;
        if (userIdCurrent.equalsIgnoreCase(userId))
            showOptions = true;
        else
            showOptions = false;

        llBack = (LinearLayout) findViewById(R.id.llBack);
        llBack.setOnClickListener(this);
        noData = (TextView) findViewById(R.id.noData);

        btnRequest = (RadioButton) findViewById(R.id.btnRequest);
        btnRequest.setOnClickListener(this);
        btnFriends = (RadioButton) findViewById(R.id.btnFriends);
        btnFriends.setOnClickListener(this);

        requestAdapter = new FriendRequestAdapter(UserFriendsActivity.this, new FriendRequestAdapter.OnFriendRequestListener() {
            @Override
            public void onAcceptRejectRequest(boolean accept, String userId, int pos) {
                acceptRejectRequest(userId, accept ? 1 : 0);
            }
        });

        friendsAdapter = new FriendRequestAdapter(UserFriendsActivity.this, null);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(UserFriendsActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(requestAdapter);

        if(type == 1)
            getFriendRequests();
        else
            btnFriends.performClick();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llBack:
                finish();
                break;
            case R.id.btnRequest:
                getFriendRequests();
                break;
            case R.id.btnFriends:
                getFriends();
                break;
        }
    }

    private void getFriendRequests() {
        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_user_id, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utils.showProgressDialog(UserFriendsActivity.this);
        Globals.initRetrofit(UserFriendsActivity.this).getFriendRequests(object).enqueue(new Callback<Friend>() {
            @Override
            public void onResponse(Call<Friend> call, Response<Friend> response) {
                Utils.closeProgressDialog();
                if (response.body().response_code == 200) {
                    if (response.body().requests != null && response.body().requests.size() > 0) {
                        requestAdapter.doRefresh(response.body().requests, response.body().user_media_base_url, showOptions);
                        noData.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        recyclerView.setAdapter(requestAdapter);
                    } else {
                        requestAdapter.doRefresh(new ArrayList<Friend.Friends>(), response.body().user_media_base_url, showOptions);
                        noData.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        recyclerView.setAdapter(requestAdapter);
                    }
                }

                if (requestAdapter.getUsers() == null || requestAdapter.getUsers().size() <= 0) {
                    noData.setText(getString(R.string.no_friends_req));
                    noData.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Friend> call, Throwable t) {
                Utils.closeProgressDialog();
                t.printStackTrace();
            }
        });
    }

    private void getFriends() {
        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_user_id, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utils.showProgressDialog(UserFriendsActivity.this);
        Globals.initRetrofit(UserFriendsActivity.this).getFriendList(object).enqueue(new Callback<Friend>() {
            @Override
            public void onResponse(Call<Friend> call, Response<Friend> response) {
                Utils.closeProgressDialog();
                if (response.body().response_code == 200) {
                    if (response.body().friends != null && response.body().friends.size() > 0) {
                        friendsAdapter.doRefresh(response.body().friends, response.body().user_media_base_url, false);
                        noData.setVisibility(View.GONE);
                        recyclerView.setAdapter(friendsAdapter);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        friendsAdapter.doRefresh(new ArrayList<Friend.Friends>(), response.body().user_media_base_url, false);
                        noData.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        recyclerView.setAdapter(friendsAdapter);
                    }
                }

                if (friendsAdapter.getUsers() == null || friendsAdapter.getUsers().size() <= 0) {
                    noData.setText(getString(R.string.no_friends));
                    noData.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Friend> call, Throwable t) {
                Utils.closeProgressDialog();
                t.printStackTrace();
            }
        });
    }

    private void acceptRejectRequest(final String userId, int status) {

        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_request_user_id, userId);
            object.addProperty(Constants.MM_status, status);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.showProgressDialog(UserFriendsActivity.this);
        Globals.initRetrofit(UserFriendsActivity.this).accpetRejectFriendRequest(object).enqueue(new Callback<FriendRequest>() {
            @Override
            public void onResponse(Call<FriendRequest> call, Response<FriendRequest> response) {

                if (response.isSuccessful() && response.body() != null && response.body().response_code == 200) {
                    Toast.makeText(UserFriendsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    getFriendRequests();
                } else {
                    Utils.closeProgressDialog();
                    Toast.makeText(UserFriendsActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FriendRequest> call, Throwable t) {
                Utils.closeProgressDialog();
                Toast.makeText(UserFriendsActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
