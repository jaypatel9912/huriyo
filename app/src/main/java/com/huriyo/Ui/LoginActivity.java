package com.huriyo.Ui;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huriyo.Model.BasicResponse;
import com.huriyo.Model.Login;
import com.huriyo.R;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.Globals;
import com.huriyo.Utility.LoginHandler;
import com.huriyo.Utility.Utils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends LocationBaseActivity implements View.OnClickListener {

    EditText edUsername, edPassword;
    Button btnLogin;
    LinearLayout llBack;
    Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        edUsername = (EditText) findViewById(R.id.edEmail);
        edPassword = (EditText) findViewById(R.id.edPassword);
        llBack = (LinearLayout) findViewById(R.id.llBack);
        llBack.setOnClickListener(this);
    }

    @Override
    public void getLocation(Location mLastLocation) {
    }


    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);

        this.mLastLocation = location;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                checkValidation();
                break;

            case R.id.llBack:
                finish();
                break;
        }
    }

    private void checkValidation() {
        String username = edUsername.getText().toString().trim();
        String password = edPassword.getText().toString().trim();

        Utils.hideSoftKeyboard(LoginActivity.this);

        if (username != null) {
            if (username == null || username.isEmpty()) {
                edUsername.setError(getString(R.string.error_name));
                edUsername.requestFocus();
                return;
            }
        }

        if (password == null || password.isEmpty() || password.length() < 8) {
            edPassword.setError(getString(R.string.error_password));
            edPassword.requestFocus();
            return;
        }

        Login login = new Login();
        login.setPassword(password);
        login.setUsername(username);
        if (mLastLocation != null)
            login.setPlace(Utils.getPlace(LoginActivity.this, mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        else
            login.setPlace("");

        Utils.showProgressDialog(LoginActivity.this);
        new LoginHandler(LoginActivity.this, login, new LoginHandler.LoginResponseHandler() {
            @Override
            public void onSuccess(BasicResponse response) {
                Utils.closeProgressDialog();
                if (response.getStatus() <= 0) {
                    Toast.makeText(LoginActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onFail(String message) {
                Utils.closeProgressDialog();
                Toast.makeText(LoginActivity.this, message != null && !message.isEmpty() ? message : getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        });
    }
}
