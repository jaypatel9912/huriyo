package com.huriyo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.huriyo.Model.Friend;
import com.huriyo.Model.SearchUser;
import com.huriyo.Model.User;
import com.huriyo.R;
import com.huriyo.Ui.ProfileActivity;
import com.huriyo.Utility.CircleImageView;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jai on 25/01/18.
 */

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.MyViewHolder> {

    private List<Friend.Friends> data;
    Context context;
    String profileUrl;
    OnFriendRequestListener listener;
    String currUserId = "";
    boolean showOptions = false;

    public interface OnFriendRequestListener {
        public void onAcceptRejectRequest(boolean accept, String userId, int pos);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView username, tvLocation;
        CircleImageView ivProfile;
        Button btnAccept, btnDelete;
        LinearLayout llMain, llOptions;

        public MyViewHolder(View view) {
            super(view);
            username = (TextView) view.findViewById(R.id.username);
            username.setTypeface(Utils.getTypefaceNormal(context));

            tvLocation = (TextView) view.findViewById(R.id.tvLocation);
            tvLocation.setTypeface(Utils.getTypefaceNormal(context));

            ivProfile = (CircleImageView) view.findViewById(R.id.ivProfile);
            btnAccept = (Button) view.findViewById(R.id.btnAccept);
            btnDelete = (Button) view.findViewById(R.id.btnDelete);
            llMain = (LinearLayout) view.findViewById(R.id.llMain);
            llOptions = (LinearLayout) view.findViewById(R.id.llOptions);
        }
    }

    public List<Friend.Friends> getUsers() {
        return this.data;
    }

    public void doEmpty() {
        this.data = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void doRefreshItem(Friend.Friends data, int pos) {
        this.data.set(pos, data);
        notifyDataSetChanged();
    }

    public void doRefresh(List<Friend.Friends> data, String profileUrl, boolean showOptions) {
        this.data = data;
        this.profileUrl = profileUrl;
        this.showOptions = showOptions;
        notifyDataSetChanged();
    }

    public List<Friend.Friends> getData() {
        return data;
    }

    public FriendRequestAdapter(Context context, OnFriendRequestListener listener) {
        this.context = context;
        data = new ArrayList<>();
        this.listener = listener;

        currUserId = new Gson().fromJson(Utils.getPreference(context, Constants.MM_UserDate), User.class)._id;
    }

    @Override
    public FriendRequestAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.friend_request_item_layout, parent, false);

        return new FriendRequestAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FriendRequestAdapter.MyViewHolder holder, final int position) {
        final Friend.Friends results = data.get(position);
        String name = results.getFirst_name() != null ? results.getFirst_name() + " " : " ";
        name += results.getLast_name() != null ? results.getLast_name() + " " : " ";
        holder.username.setText(name);
//        holder.tvLocation.setText(results.getPlace() != null ? results.getPlace() : "");
        Glide.with(context).load(profileUrl + "/" + results._id + "/" + results.profile_image).
                diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.avatar).into(holder.ivProfile);

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onAcceptRejectRequest(true, results._id, position);
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onAcceptRejectRequest(false, results._id, position);
            }
        });

        holder.llOptions.setVisibility(showOptions ? View.VISIBLE : View.GONE);

        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra(Constants.MM_user_id, results._id);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }
}