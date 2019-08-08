package com.huriyo.Utility;

import android.content.Context;

import com.google.gson.Gson;
import com.huriyo.Model.BasicResponse;
import com.huriyo.Model.Login;
import com.huriyo.Model.User;
import com.huriyo.Ui.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jay on 27/12/17.
 */

public class LoginHandler {

    public interface LoginResponseHandler{
        public void onSuccess(BasicResponse response);
        public void onFail(String msg);
    }

    public LoginHandler(final Context context, final Login login, final LoginResponseHandler listener) {
        Globals.initRetrofit(context).doLogin(login).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().response_code == 200) {
                    Utils.setPreference(context, Constants.MM_token, response.body().getUser().getToken());
                    response.body().getUser().password = login.getPassword();
                    response.body().getUser().username = login.getUsername();
                    Utils.setPreference(context, Constants.MM_UserDate, new Gson().toJson(response.body().getUser()));
                    Globals.login = login;
                    listener.onSuccess(response.body());
                }else {
                    listener.onFail(response.body().message);
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                listener.onFail("");
            }
        });
    }
}
