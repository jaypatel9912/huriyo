package com.huriyo.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.huriyo.Model.GalleryItem;
import com.huriyo.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Jay on 15/11/17.
 */

public class MiniImageVideoAdapter extends RecyclerView.Adapter<MiniImageVideoAdapter.ViewHolder> {

    ArrayList<GalleryItem> mValues;
    Context mContext;

    public MiniImageVideoAdapter(Context context) {
        mValues = new ArrayList<>();
        mContext = context;
    }

    public void doRefresh(ArrayList<GalleryItem> values) {
        mValues = values;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.imageView);
        }
    }

    @Override
    public MiniImageVideoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.image_video_mini_item_layout, parent, false);
        return new MiniImageVideoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MiniImageVideoAdapter.ViewHolder holder, int position) {
        Glide.with(mContext).load("file://" + mValues.get(position).getPath()).into(holder.imageView);
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }
}