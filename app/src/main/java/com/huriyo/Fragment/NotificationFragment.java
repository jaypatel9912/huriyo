package com.huriyo.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.huriyo.Adapter.NotificationAdapter;
import com.huriyo.Model.BasicResponse;
import com.huriyo.Model.SearchUser;
import com.huriyo.Model.User;
import com.huriyo.Model.UserNotification;
import com.huriyo.R;
import com.huriyo.Ui.FeedDescriptionActivity;
import com.huriyo.Ui.HomeActivity;
import com.huriyo.Ui.UserFriendsActivity;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.FragmentBase;
import com.huriyo.Utility.Globals;
import com.huriyo.Utility.RecyclerItemClickListener;
import com.huriyo.Utility.Screen;
import com.huriyo.Utility.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends FragmentBase<HomeActivity> {

    public NotificationFragment() {
        // Required empty public constructor
    }

    public static NotificationFragment newInstance() {
        NotificationFragment fragment = new NotificationFragment();
        return fragment;
    }

    @Override
    public Screen getCode() {
        return Screen.NOTIFICATION;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    NotificationAdapter adapter;
    RecyclerView rlNotifications;
    View view;
    int skip = 0, limit = 10;
    TextView tvNodata;
    int pos;
    UserNotification.Notifications notifications;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_notification, container, false);

        rlNotifications = (RecyclerView) view.findViewById(R.id.rlNotifications);
        tvNodata = (TextView) view.findViewById(R.id.tvNodata);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rlNotifications.setLayoutManager(mLayoutManager);
        rlNotifications.setItemAnimator(new DefaultItemAnimator());
        rlNotifications.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        adapter = new NotificationAdapter(getActivity());
        rlNotifications.setAdapter(adapter);

        rlNotifications.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        notifications = adapter.getMdata().get(position);
                        if (adapter.getMdata().get(position).status == 0) {
                            pos = position;
                            markNotificationAsRead(notifications._id);
                        }
                        Intent intent;
                        if (notifications.getNotification_type() == 1 || notifications.getNotification_type() == 2) {
                            intent = new Intent(getActivity(), UserFriendsActivity.class);
                            intent.putExtra( Constants.MM_type, notifications.getNotification_type());
                            intent.putExtra(Constants.MM_user_id, new Gson().fromJson(Utils.getPreference(getActivity(), Constants.MM_UserDate), User.class)._id);
                            startActivity(intent);
                        } else if (notifications.getNotification_type() == 3 || notifications.getNotification_type() == 4) {
                            intent = new Intent(getActivity(), FeedDescriptionActivity.class);
                            intent.putExtra(Constants.MM_Feed, notifications.post_id);
                            startActivity(intent);
                        }
                    }
                }));

        getNotifications();
        return view;
    }

    private void getNotifications() {
        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_skip, skip);
            object.addProperty(Constants.MM_limit, limit);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utils.showProgressDialog(getActivity());
        Globals.initRetrofit(getActivity()).getUserNotifications(object).enqueue(new Callback<UserNotification>() {
            @Override
            public void onResponse(Call<UserNotification> call, Response<UserNotification> response) {
                Utils.closeProgressDialog();
                if (response.body().response_code == 200) {
                    if (response.body().getNotifications() != null && response.body().getNotifications().size() > 0) {
                        adapter.addAll(response.body().getNotifications(),response.body().getUser_media_base_url());
                        skip += 10;
                        limit += 10;
                        tvNodata.setVisibility(View.GONE);
                        rlNotifications.setVisibility(View.VISIBLE);
                    }

                    if (adapter.getItemCount() <= 0) {
                        tvNodata.setVisibility(View.VISIBLE);
                        rlNotifications.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserNotification> call, Throwable t) {
                Utils.closeProgressDialog();
                t.printStackTrace();
            }
        });
    }

    private void markNotificationAsRead(String noteId) {
        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_notification_id, noteId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Globals.initRetrofit(getActivity()).markNotificationAsRead(object).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.body().response_code == 200) {
                    notifications.setStatus(1);
                    adapter.resetNotification(pos, notifications);
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


}