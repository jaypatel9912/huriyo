package com.huriyo.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.huriyo.Model.GalleryItem;
import com.huriyo.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Jay on 15/11/17.
 */

public class ImageVideoAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    ArrayList<GalleryItem> selectedList;

    public ImageVideoAdapter(Context context, ArrayList<GalleryItem> selectedList) {
        mContext = context;
        this.selectedList = selectedList;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return selectedList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.view_image_layout, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        EditText edCaption = (EditText) itemView.findViewById(R.id.edCaption);

        edCaption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                selectedList.get(position).setCaption(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Glide.with(mContext).load("file://" + selectedList.get(position).getPath()).into(imageView);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }
}
