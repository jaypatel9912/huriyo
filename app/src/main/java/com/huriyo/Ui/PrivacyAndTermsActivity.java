package com.huriyo.Ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.huriyo.Model.Suggestions;
import com.huriyo.R;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.Globals;
import com.huriyo.Utility.Utils;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrivacyAndTermsActivity extends BaseActivity {

    LinearLayout llBack;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_and_terms);

        llBack = (LinearLayout) findViewById(R.id.llBack);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        webView = (WebView) findViewById(R.id.webView);

        if(getIntent().getExtras().getBoolean(Constants.MM_is_privacy)){
            getDataPrivacy();
        }else{
            getDataTerms();
        }
    }

    private void getDataTerms() {
        Utils.showProgressDialog(PrivacyAndTermsActivity.this);
        Globals.initRetrofit(PrivacyAndTermsActivity.this).getTermsAndConditions().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Utils.closeProgressDialog();
                if (response.isSuccessful()) {
                    try {
                        webView.loadData(response.body().string(), "text/html", null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Utils.closeProgressDialog();
                t.printStackTrace();
            }
        });
    }

    private void getDataPrivacy() {
        Utils.showProgressDialog(PrivacyAndTermsActivity.this);
        Globals.initRetrofit(PrivacyAndTermsActivity.this).getPrivacyPolicy().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Utils.closeProgressDialog();
                if (response.isSuccessful()) {
                    try {
                        webView.loadData(response.body().string(), "text/html", null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Utils.closeProgressDialog();
                t.printStackTrace();
            }
        });
    }
}
