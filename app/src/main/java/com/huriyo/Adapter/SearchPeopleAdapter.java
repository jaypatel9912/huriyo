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
 * Created by Jay on 09/11/17.
 */

public class SearchPeopleAdapter extends RecyclerView.Adapter<SearchPeopleAdapter.MyViewHolder> {

    private List<SearchUser.Results> data;
    Context context;
    String profileUrl;
    OnFriendRequestListener listener;
    String currUserId = "";

    public interface OnFriendRequestListener {
        public void onSendRequest(String userId, SearchUser.Results res, int pos);

        public void onAcceptRejectRequest(String userId, int status, int pos);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView username, tvLocation;
        CircleImageView ivProfile;
        Button btnRequest;
        LinearLayout llMain;

        public MyViewHolder(View view) {
            super(view);
            username = (TextView) view.findViewById(R.id.username);
            username.setTypeface(Utils.getTypefaceNormal(context));

            tvLocation = (TextView) view.findViewById(R.id.tvLocation);
            tvLocation.setTypeface(Utils.getTypefaceNormal(context));

            ivProfile = (CircleImageView) view.findViewById(R.id.ivProfile);
            btnRequest = (Button) view.findViewById(R.id.btnRequest);

            llMain = (LinearLayout) view.findViewById(R.id.llMain);
        }
    }

    public List<SearchUser.Results> getUsers() {
        return this.data;
    }

    public void doEmpty() {
        this.data = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void doRefreshItem(SearchUser.Results data, int pos) {
        this.data.set(pos, data);
        notifyDataSetChanged();
    }

    public void doRefresh(List<SearchUser.Results> data, String profileUrl) {
        this.data.addAll(data);
        this.profileUrl = profileUrl;
        notifyDataSetChanged();
    }

    public List<SearchUser.Results> getData() {
        return data;
    }

    public SearchPeopleAdapter(Context context, OnFriendRequestListener listener) {
        this.context = context;
        data = new ArrayList<>();
        this.listener = listener;

        currUserId = new Gson().fromJson(Utils.getPreference(context, Constants.MM_UserDate), User.class)._id;
    }

    @Override
    public SearchPeopleAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.search_people_item, parent, false);

        return new SearchPeopleAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SearchPeopleAdapter.MyViewHolder holder, final int position) {
        final SearchUser.Results results = data.get(position);

        if(results.getBusiness_name() != null && !results.getBusiness_name().isEmpty())
            holder.username.setText(results.getBusiness_name());
        else
            holder.username.setText(results.getName() != null ? results.getName() : "");

        holder.tvLocation.setText(results.getPlace() != null ? results.getPlace() : "");
        Glide.with(context).load(profileUrl + "/" + results._id + "/" + results.profile_image).
                diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.avatar).into(holder.ivProfile);

        if (currUserId.equalsIgnoreCase(results.get_id())) {
            holder.btnRequest.setVisibility(View.GONE);
        } else if (results.getRequest_status() == 0) {
            holder.btnRequest.setVisibility(View.VISIBLE);
            holder.btnRequest.setText("Add Connect");
            holder.btnRequest.setClickable(true);
            holder.btnRequest.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_green_button_drawable));
        } else if (results.getRequest_status() == 2) {
            holder.btnRequest.setVisibility(View.VISIBLE);
            holder.btnRequest.setText("Request Sent");
            holder.btnRequest.setClickable(false);
            holder.btnRequest.setBackground(ContextCompat.getDrawable(context, R.drawable.gray_rounded_bg));
        } else {
            holder.btnRequest.setVisibility(View.GONE);
        }

        holder.btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (results.getRequest_status() == 0) {
                    listener.onSendRequest(results._id, results, position);
                } else if (results.getRequest_status() == 3) {
                    listener.onAcceptRejectRequest(results._id, 0, position);
                }
            }
        });

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