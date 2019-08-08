package com.huriyo.Ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huriyo.Model.Feed;
import com.huriyo.Model.GalleryItem;
import com.huriyo.R;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.Picture;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ImageCropActivity extends BaseActivity implements View.OnClickListener {
    ArrayList<GalleryItem> selectedList;
    LinearLayout llDone, llCrop;
    ViewPager mViewPager;
    CustomPagerAdapter mCustomPagerAdapter;
    ArrayList<Feed.Post_media> post_media;
    String mediaUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_crop);


        if (getIntent().getExtras().containsKey(Constants.MM_view)) {
            Type type = new TypeToken<ArrayList<Feed.Post_media>>() {
            }.getType();
            post_media = new Gson().fromJson(getIntent().getExtras().getString(Constants.MM_Data), type);
            mediaUrl = getIntent().getExtras().getString(Constants.MM_imagepath);
        } else {
            Type type = new TypeToken<ArrayList<GalleryItem>>() {
            }.getType();
            selectedList = new Gson().fromJson(getIntent().getExtras().getString(Constants.MM_Data), type);

            if (selectedList == null || selectedList.size() <= 0) {
                cancelTask();
            }
        }

        llDone = (LinearLayout) findViewById(R.id.llDone);
        llDone.setOnClickListener(this);
        llCrop = (LinearLayout) findViewById(R.id.llCrop);
        llCrop.setOnClickListener(this);

        if (selectedList == null) {
            llCrop.setVisibility(View.GONE);
            findViewById(R.id.tvTitle).setVisibility(View.GONE);
        }

        mCustomPagerAdapter = new CustomPagerAdapter(this);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mCustomPagerAdapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            selectedList.get(mViewPager.getCurrentItem()).setPath(new File(result.getUri().getPath()).getAbsolutePath());
            mCustomPagerAdapter.notifyDataSetChanged();
        }
    }

    public void cancelTask() {
        if (selectedList == null) {
            finish();
            return;
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.llDone:
                if (selectedList == null) {
                    finish();
                    return;
                }

                Intent intent = getIntent();
                intent.putExtra(Constants.MM_Data, new Gson().toJson(selectedList));
                setResult(RESULT_OK, intent);
                finish();

                break;
            case R.id.llCrop:
                CropImage.activity(Uri.fromFile(new File(selectedList.get(mViewPager.getCurrentItem()).getPath()))).
                        setCropShape(CropImageView.CropShape.RECTANGLE).setGuidelines(CropImageView.Guidelines.ON).
                        setRequestedSize(500, 250).setFixAspectRatio(true).start(ImageCropActivity.this);

                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (selectedList == null) {
            finish();
            return;
        }

        cancelTask();
    }

    public class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        public CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return selectedList == null ? post_media.size() : selectedList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((RelativeLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            final ProgressBar pb = (ProgressBar) itemView.findViewById(R.id.pb);
            String imageUtl = null;
            if (selectedList != null) {
                imageUtl = "file://" + selectedList.get(position).getPath();
            } else {
                imageUtl = mediaUrl + "/" + post_media.get(position).user_id + "/" + post_media.get(position).file_name;
            }
            pb.setVisibility(View.VISIBLE);
            Glide.with(imageView.getContext())
                    .load(imageUtl)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            pb.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            pb.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imageView);
            container.addView(itemView);

            return itemView;
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout) object);
        }
    }
}
