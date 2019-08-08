package com.huriyo.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.huriyo.Model.GalleryItem;
import com.huriyo.R;

import java.io.File;
import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    ArrayList<GalleryItem> mValues;
    Context mContext;

    public GalleryAdapter(Context context) {
        mValues = new ArrayList<>();
        mContext = context;
    }

    public void doRefresh(ArrayList<GalleryItem> values) {
        mValues = values;
        notifyDataSetChanged();
    }

    public void doRefreshItem(GalleryItem value, int position) {
        mValues.set(position, value);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView, videoImg, checkImg;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.imageView);
            videoImg = (ImageView) v.findViewById(R.id.videoImg);
            checkImg = (ImageView) v.findViewById(R.id.checkImg);
        }
    }

    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.gallery_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (mValues.get(position).isSelected()) {
            holder.checkImg.setVisibility(View.VISIBLE);
        } else {
            holder.checkImg.setVisibility(View.GONE);
        }

        if (mValues.get(position).getType() == 2) {
            holder.videoImg.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(Uri.fromFile(new File(mValues.get(position).getPath()))).into(holder.imageView);
        } else {
            holder.videoImg.setVisibility(View.GONE);
            Glide.with(mContext).load("file://" + mValues.get(position).getPath()).into(holder.imageView);
        }

    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }
}