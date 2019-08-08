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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.marlonlom.utilities.timeago.TimeAgoMessages;
import com.huriyo.Model.UserNotification;
import com.huriyo.Model.UserRating;
import com.huriyo.R;
import com.huriyo.Utility.CircleImageView;
import com.huriyo.Utility.Utils;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by jai on 23/01/18.
 */

public class UserRatingAdapter extends RecyclerView.Adapter<UserRatingAdapter.MyViewHolder> {

    private List<UserRating.Ratings> mdata;
    Context context;
    Locale LocaleBylanguageTag;
    TimeAgoMessages messages;
    String profileUrl;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvTime, tvComment;
        LinearLayout llMain;
        CircleImageView ivProfile;
        ScaleRatingBar simpleRatingBar;


        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvName.setTypeface(Utils.getTypefaceNormal(context));
            tvTime = (TextView) view.findViewById(R.id.tvTime);
            tvTime.setTypeface(Utils.getTypefaceNormal(context));
            tvComment = (TextView) view.findViewById(R.id.tvComment);
            tvComment.setTypeface(Utils.getTypefaceNormal(context));

            ivProfile = (CircleImageView) view.findViewById(R.id.ivProfile);

            simpleRatingBar = (ScaleRatingBar) view.findViewById(R.id.simpleRatingBar);

            llMain = (LinearLayout) view.findViewById(R.id.llMain);
        }
    }

    public void addAll(List<UserRating.Ratings> data, String profileUrl) {
        this.mdata = data;
        this.profileUrl = profileUrl;
        notifyDataSetChanged();
    }

    public void resetNotification(int pos, UserRating.Ratings note) {
        this.mdata.set(pos, note);
        notifyDataSetChanged();
    }

    public void reset() {
        this.mdata = new ArrayList<>();
        notifyDataSetChanged();
    }

    public List<UserRating.Ratings> getMdata() {
        return mdata;
    }

    public UserRatingAdapter(Context context) {
        this.context = context;
        mdata = new ArrayList<>();
        LocaleBylanguageTag = context.getResources().getConfiguration().locale;
        messages = new TimeAgoMessages.Builder().withLocale(LocaleBylanguageTag).build();

    }

    @Override
    public UserRatingAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rating_item_layout, parent, false);

        return new UserRatingAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserRatingAdapter.MyViewHolder holder, int position) {
        UserRating.Ratings rating = mdata.get(position);
        holder.tvName.setText(rating.userInfo.first_name + rating.userInfo.last_name);
        String text = TimeAgo.using(Long.parseLong(rating.timestamp), messages);

        holder.tvTime.setText(text);

        holder.tvComment.setText(rating.description);
        holder.simpleRatingBar.setRating(mdata.get(position).rating);

        Glide.with(context).load(profileUrl + "/" + rating.userInfo._id + "/" + rating.userInfo.profile_image).
                diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.avatar).into(holder.ivProfile);
    }

    @Override
    public int getItemCount() {
        return mdata == null ? 0 : mdata.size();
    }
}