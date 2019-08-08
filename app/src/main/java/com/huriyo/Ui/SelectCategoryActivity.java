package com.huriyo.Ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.huriyo.Adapter.CategoryAdapter;
import com.huriyo.Adapter.SearchAdapter;
import com.huriyo.Model.Category;
import com.huriyo.Model.Feed;
import com.huriyo.R;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.DividerItemDecoration;
import com.huriyo.Utility.Globals;
import com.huriyo.Utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectCategoryActivity extends BaseActivity implements View.OnClickListener {

    RecyclerView recyclerViewCategories;
    LinearLayout llBack;
    Button btnContinue, btnSkip;
    CategoryAdapter adapter;
    Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);

        recyclerViewCategories = (RecyclerView) findViewById(R.id.recyclerViewCategories);
        llBack = (LinearLayout) findViewById(R.id.llBack);
        btnSkip = (Button) findViewById(R.id.btnSkip);
        btnContinue = (Button) findViewById(R.id.btnContinue);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SelectCategoryActivity.this);
        recyclerViewCategories.setLayoutManager(mLayoutManager);
        recyclerViewCategories.addItemDecoration(new DividerItemDecoration(SelectCategoryActivity.this, R.drawable.rcl_divider));
        recyclerViewCategories.setItemAnimator(new DefaultItemAnimator());
        adapter = new CategoryAdapter(SelectCategoryActivity.this);
        recyclerViewCategories.setAdapter(adapter);

        btnSkip.setOnClickListener(this);
        llBack.setOnClickListener(this);
        btnContinue.setOnClickListener(this);

        getCategories();
    }

    public void getCategories() {
        Utils.showProgressDialog(SelectCategoryActivity.this);
        Globals.initRetrofit(SelectCategoryActivity.this).getCategories().enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                Utils.closeProgressDialog();
                if (response.isSuccessful()) {
                    category = response.body();
                    if (category == null || category.categories == null || category.categories.size() <= 0) {
                        Toast.makeText(SelectCategoryActivity.this, "No Categories available", Toast.LENGTH_SHORT).show();
                    } else {
                        adapter.doRefresh(category.categories);
                    }
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                Utils.closeProgressDialog();
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnContinue:
                if (adapter.getSelectedCategory() == null) {
                    Toast.makeText(this, R.string.select_category_err, Toast.LENGTH_SHORT).show();
                } else {
                    categorySelected = adapter.getSelectedCategory();
                    moveToMainScreen();
                }
                break;
            case R.id.llBack:
                finish();
                break;
            case R.id.btnSkip:
                moveToMainScreen();
                break;
        }
    }

    Category.Categories categorySelected;

    private void setCategory() {

        if(categorySelected == null)
            return;

        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_business_category_id, categorySelected._id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utils.showProgressDialog(SelectCategoryActivity.this);
        Globals.initRetrofit(SelectCategoryActivity.this).setCategory(object).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Utils.closeProgressDialog();
                if (!response.isSuccessful()) {
                    Toast.makeText(SelectCategoryActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
                moveToMainScreen();
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Utils.closeProgressDialog();
                t.printStackTrace();
                moveToMainScreen();
            }
        });
    }

    public void moveToMainScreen() {
        Intent i = new Intent(SelectCategoryActivity.this, BussinessInfoActivity.class);
        i.putExtra(Constants.MM_business_category, new Gson().toJson(categorySelected));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
