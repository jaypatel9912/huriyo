package com.huriyo.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huriyo.Model.BussinessUser;
import com.huriyo.Model.SearchUser;
import com.huriyo.R;
import com.huriyo.Utility.CircleImageView;
import com.huriyo.Utility.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jai on 14/02/18.
 */

public class BussinessUsersAdapter extends RecyclerView.Adapter<BussinessUsersAdapter.MyViewHolder> {

    Context context;
    List<BussinessUser.Businesses> data;
    String profileUrl;

    public BussinessUsersAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bussiness_user_item_layout, parent, false);
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        BussinessUser.Businesses businesses = data.get(position);

        holder.textView.setText(businesses.business_name);
        holder.name.setText(businesses.businessCategoryInfo != null && businesses.businessCategoryInfo.size() > 0 ? businesses.businessCategoryInfo.get(0).category_name : "");
        holder.rating.setText("Rating " + businesses.rating_average);
        holder.reviews.setText("( " + businesses.total_rating + " Reviews)");

        if (businesses.profile_image != null && !businesses.profile_image.isEmpty()) {
            Glide.with(context).load(profileUrl + "/" + businesses._id + "/" + businesses.profile_image)
                    .into(holder.ivProfile);
        } else {
            holder.ivProfile.setImageResource(R.mipmap.avatar);
        }

        if (businesses.cover_image != null && !businesses.cover_image.isEmpty()) {
            Glide.with(context).load(profileUrl + "/" + businesses._id + "/" + businesses.cover_image)
                    .centerCrop().into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.mipmap.ic_cover);
        }

    }

    public void doRefresh(List<BussinessUser.Businesses> data, String profileUrl) {
        this.data.addAll(data);
        this.profileUrl = profileUrl;
        notifyDataSetChanged();
    }

    public List<BussinessUser.Businesses> getData() {
        return data;
    }

    @Override
    public int getItemCount() {
        if (data == null)
            data = new ArrayList<>();

        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView, name, rating, reviews;// init the item view's
        ImageView imageView;
        CircleImageView ivProfile;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            textView = (TextView) itemView.findViewById(R.id.textView);

            name = (TextView) itemView.findViewById(R.id.name);
            name.setTypeface(Utils.getTypefaceNormal(context));

            rating = (TextView) itemView.findViewById(R.id.rating);
            rating.setTypeface(Utils.getTypefaceNormal(context));
            reviews = (TextView) itemView.findViewById(R.id.reviews);
            reviews.setTypeface(Utils.getTypefaceNormal(context));
            ivProfile = (CircleImageView) itemView.findViewById(R.id.ivProfile);
        }
    }
}