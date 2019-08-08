package com.huriyo.Utility;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.huriyo.Model.Login;
import com.huriyo.Model.User;
import com.huriyo.R;

import io.fabric.sdk.android.Fabric;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Jay on 21/11/17.
 */

public class Globals extends MultiDexApplication {

    public static User user;
    public static Login login;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    public static Location mLastLocation;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        if (Utils.getPreference(getApplicationContext(), Constants.MM_UserDate) != null &&
                !Utils.getPreference(getApplicationContext(), Constants.MM_UserDate).isEmpty()) {
            user = new Gson().fromJson(Utils.getPreference(getApplicationContext(), Constants.MM_UserDate), User.class);
            if (user != null) {
                login = new Login();
                login.setUsername(user.getUsername());
                login.setPassword(user.getPassword());
            }
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("SanFranciscoText-Regular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public SharedPreferences getSharedPref() {
        return sp = (sp == null) ? getSharedPreferences(Constants.MM_secrets, Context.MODE_PRIVATE) : sp;
    }

    public SharedPreferences.Editor getEditor() {
        return editor = (editor == null) ? getSharedPref().edit() : editor;
    }

    public double getLatitude() {
        return Double.parseDouble(getSharedPref().getString(Constants.MM_latitude, "0.0"));
    }

    public void setLatitude(double latitude) {
        if (latitude != 0) {
            getEditor().putString(Constants.MM_latitude, String.valueOf(latitude));
            getEditor().commit();
        }
    }

    public double getLongitude() {
        return Double.parseDouble(getSharedPref().getString(Constants.MM_longitude, "0.0"));
    }

    public void setLongitude(double longitude) {
        if (longitude != 0) {
            getEditor().putString(Constants.MM_longitude, String.valueOf(longitude));
            getEditor().commit();
        }
    }

    public static ApiInterface initRetrofit(final Context context) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();

                String token = Utils.getPreference(context.getApplicationContext(), Constants.MM_token);

                Request.Builder builder = originalRequest.newBuilder();
                if (token != null && !token.isEmpty()) {
                    builder.header(Constants.MM_Authorization, token);
                }

                builder.addHeader(Constants.MM_content_type, Constants.MM_app_json);
                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }
        }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ApiInterface.class);
    }
}
