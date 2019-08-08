package com.huriyo.Adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.marlonlom.utilities.timeago.TimeAgoMessages;
import com.huriyo.Model.UserNotification;
import com.huriyo.R;
import com.huriyo.Utility.CircleImageView;
import com.huriyo.Utility.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by jai on 22/01/18.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private List<UserNotification.Notifications> mdata;
    Context context;
    Locale LocaleBylanguageTag;
    TimeAgoMessages messages;
    String profileUrl;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvTime;
        LinearLayout llMain;
        CircleImageView ivProfile;

        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvName.setTypeface(Utils.getTypefaceNormal(context));
            tvTime = (TextView) view.findViewById(R.id.tvTime);
            tvTime.setTypeface(Utils.getTypefaceNormal(context));

            llMain = (LinearLayout) view.findViewById(R.id.llMain);
            ivProfile = (CircleImageView) view.findViewById(R.id.ivProfile);
        }
    }

    public void addAll(List<UserNotification.Notifications> data, String profileUrl) {
        this.mdata = data;
        this.profileUrl = profileUrl;
        notifyDataSetChanged();
    }

    public void resetNotification(int pos, UserNotification.Notifications note) {
        this.mdata.set(pos, note);
        notifyDataSetChanged();
    }

    public void reset() {
        this.mdata = new ArrayList<>();
        notifyDataSetChanged();
    }

    public List<UserNotification.Notifications> getMdata() {
        return mdata;
    }

    public NotificationAdapter(Context context) {
        this.context = context;
        mdata = new ArrayList<>();
        LocaleBylanguageTag = context.getResources().getConfiguration().locale;
        messages = new TimeAgoMessages.Builder().withLocale(LocaleBylanguageTag).build();

    }

    @Override
    public NotificationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item_layout, parent, false);

        return new NotificationAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.MyViewHolder holder, int position) {

        String msg = "";
        String username = "";
        if (mdata.get(position).getUserInfo().user_type == 0) {
            String fname = mdata.get(position).getUserInfo().first_name != null ? mdata.get(position).getUserInfo().first_name : "";
            String lname = mdata.get(position).getUserInfo().last_name != null ? mdata.get(position).getUserInfo().last_name : "";
            username = fname + " " + lname + " ";
        } else {
            username = mdata.get(position).getUserInfo().business_name + " ";
        }

        if (mdata.get(position).getNotification_type() == 1)
            msg = " Sent you a connect Request";
        else if (mdata.get(position).getNotification_type() == 2)
            msg = " Accepted  your connect Request";
        else if (mdata.get(position).getNotification_type() == 3)
            msg = " Likes your post";
        else if (mdata.get(position).getNotification_type() == 4)
            msg = " Likes your Review";


        holder.tvName.setText(username + msg);
        String text = TimeAgo.using(Long.parseLong(mdata.get(position).timestamp), messages);
        holder.tvTime.setText(text);

        if (mdata.get(position).getStatus() == 1) {
            holder.llMain.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
        } else {
            holder.llMain.setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray_end_time));
        }


        if (mdata.get(position).userInfo.profile_image != null && !mdata.get(position).userInfo.profile_image.isEmpty()) {
            Glide.with(context).load(profileUrl + "/" + mdata.get(position).userInfo._id + "/" + mdata.get(position).userInfo.profile_image)
                    .centerCrop().into(holder.ivProfile);
        } else {
            holder.ivProfile.setImageResource(R.mipmap.avatar);
        }


    }

    @Override
    public int getItemCount() {
        return mdata == null ? 0 : mdata.size();
    }
}