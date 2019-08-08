package com.huriyo.Ui;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huriyo.Adapter.GalleryAdapter;
import com.huriyo.Adapter.ImageVideoAdapter;
import com.huriyo.Adapter.MiniImageVideoAdapter;
import com.huriyo.Model.GalleryItem;
import com.huriyo.R;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.RecyclerItemClickListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class SelectedImageVideoActivity extends BaseActivity {

    ArrayList<GalleryItem> selectedList;
    ViewPager viewPager;
    MiniImageVideoAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_selected_image_video);

        selectedList = new ArrayList<>();

        if (!getIntent().getExtras().containsKey(Constants.MM_Data)) {
            finish();
        }

        Type type = new TypeToken<ArrayList<GalleryItem>>() {
        }.getType();
        selectedList = new Gson().fromJson(getIntent().getExtras().getString(Constants.MM_Data), type);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        viewPager.setAdapter(new ImageVideoAdapter(SelectedImageVideoActivity.this, selectedList));

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new MiniImageVideoAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.doRefresh(selectedList);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        viewPager.setCurrentItem(position);
                    }
                }));
    }


}
