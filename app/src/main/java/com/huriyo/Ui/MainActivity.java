package com.huriyo.Ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.huriyo.Model.BasicResponse;
import com.huriyo.Model.PostWithComments;
import com.huriyo.R;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.Globals;
import com.huriyo.Utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    Button btnRegister, btnLogin;
    TextView tvForgotPassword, tvTitle1;
    ImageView fb, google;
    GoogleSignInClient mGoogleSignInClient;
    LoginButton login_button;
    CallbackManager callbackManager;
    private static final String EMAIL = "email";
    JsonObject object = new JsonObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        login_button = (LoginButton) findViewById(R.id.login_button);
        login_button.setReadPermissions(Arrays.asList(EMAIL));

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        fb = (ImageView) findViewById(R.id.fb);
        fb.setOnClickListener(this);
        google = (ImageView) findViewById(R.id.google);
        google.setOnClickListener(this);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
        tvTitle1= (TextView) findViewById(R.id.tvTitle1);
        tvTitle1.setTypeface(Utils.getTypefaceCurly(MainActivity.this));
        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);

        fbLogin();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRegister:
                startActivity(new Intent(MainActivity.this, RegistrationsActivity.class));
                break;

            case R.id.btnLogin:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;

            case R.id.tvForgotPassword:
                startActivity(new Intent(MainActivity.this, ForgotPasswordActivity.class));
                break;

            case R.id.fb:
                login_button.performClick();
                break;

            case R.id.google:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 12352);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 12352) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            object = new JsonObject();
            object.addProperty(Constants.MM_social_login_type, 2);
            object.addProperty(Constants.MM_google_id, account.getId());
            object.addProperty(Constants.MM_email, account.getEmail());
            object.addProperty(Constants.MM_phone_no, "");
            object.addProperty(Constants.MM_first_name, account.getGivenName());
            object.addProperty(Constants.MM_last_name, account.getFamilyName());
            socialLogin(object);
        } catch (ApiException e) {
            Log.w("", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void fbLogin() {
        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                getUserDetails(loginResult);
            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(MainActivity.this, "Fb error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(MainActivity.this, "Fb error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void getUserDetails(LoginResult loginResult) {
        GraphRequest data_request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject json_object,
                            GraphResponse response) {

                        try {

                            if (!json_object.has(Constants.MM_email) || json_object.getString(Constants.MM_email).isEmpty()) {
                                return;
                            }

                            object = new JsonObject();
                            object.addProperty(Constants.MM_social_login_type, 1);
                            object.addProperty(Constants.MM_facebook_id, json_object.getString("id"));
                            object.addProperty(Constants.MM_email, json_object.getString(Constants.MM_email));
                            object.addProperty(Constants.MM_phone_no, "");
                            object.addProperty(Constants.MM_first_name, json_object.getString(Constants.MM_first_name));
                            object.addProperty(Constants.MM_last_name, json_object.getString(Constants.MM_last_name));
                            socialLogin(object);
                            LoginManager.getInstance().logOut();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,first_name,last_name,email,picture.width(120).height(120)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();
    }

    private void socialLogin(JsonObject jsonObject) {
        Utils.showProgressDialog(MainActivity.this);
        Globals.initRetrofit(MainActivity.this).socialLogin(jsonObject).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Utils.closeProgressDialog();
                if (response.isSuccessful()) {
                    if (response.body().response_code <= 0) {
                        Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        if (response.body().response_code != 200) {
                            Toast.makeText(MainActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Utils.setPreference(MainActivity.this, Constants.MM_token, response.body().getUser().getToken());

                        Utils.setPreference(MainActivity.this, Constants.MM_UserDate, new Gson().toJson(response.body().getUser()));
                        Intent i = new Intent(MainActivity.this, SelectCategoryActivity.class);
                        startActivity(i);
                        finish();
                    }
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                t.printStackTrace();
                Toast.makeText(MainActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
