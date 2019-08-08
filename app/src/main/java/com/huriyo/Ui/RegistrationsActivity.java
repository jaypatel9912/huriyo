package com.huriyo.Ui;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huriyo.Model.BasicResponse;
import com.huriyo.Model.User;
import com.huriyo.R;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.Globals;
import com.huriyo.Utility.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationsActivity extends LocationBaseActivity implements View.OnClickListener {

    Button btnRegister;
    LinearLayout llBack, llPhone;
    EditText edFname, edLname, edEmail, edPassword, edPhone;
    ImageView ivEye;
    boolean showPassword = false;
    RadioButton rdEmail, rdPhone;
    LinearLayout llMain;
    Location mLastLocation;
    ScrollView scrollview;
    TextView tvTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.activity_registeration);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

        llBack = (LinearLayout) findViewById(R.id.llBack);
        llBack.setOnClickListener(this);

        llMain = (LinearLayout) findViewById(R.id.llMain);
        tvTP = (TextView) findViewById(R.id.tvTP);
        scrollview = (ScrollView) findViewById(R.id.scrollview);

        llPhone = (LinearLayout) findViewById(R.id.llPhone);

        edFname = (EditText) findViewById(R.id.edFname);
        edLname = (EditText) findViewById(R.id.edLname);
        edEmail = (EditText) findViewById(R.id.edEmail);
        edPhone = (EditText) findViewById(R.id.edPhone);
        edPassword = (EditText) findViewById(R.id.edPassword);

        ivEye = (ImageView) findViewById(R.id.ivEye);
        ivEye.setOnClickListener(this);

        rdEmail = (RadioButton) findViewById(R.id.rdEmail);
        rdPhone = (RadioButton) findViewById(R.id.rdPhone);

        String terms = "Terms of Use";
        String policy = "Privacy Policy";
        String all = getString(R.string.register_p_p);

        SpannableString ss= new SpannableString(all);

        ss.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent pp = new Intent(RegistrationsActivity.this, PrivacyAndTermsActivity.class);
                pp.putExtra(Constants.MM_is_privacy, false);
                startActivity(pp);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(true);
            }
        }, all.indexOf(terms), all.indexOf(terms) + terms.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ss.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent pp = new Intent(RegistrationsActivity.this, PrivacyAndTermsActivity.class);
                pp.putExtra(Constants.MM_is_privacy, true);
                startActivity(pp);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(true);
            }
        }, all.indexOf(policy), all.indexOf(policy) + policy.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvTP.setText(ss);
        tvTP.setMovementMethod(LinkMovementMethod.getInstance());

        rdEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    edEmail.setVisibility(View.VISIBLE);
                    llPhone.setVisibility(View.GONE);
                }
            }
        });

        rdPhone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    edEmail.setVisibility(View.GONE);
                    llPhone.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void getLocation(Location mLastLocation) {
        this.mLastLocation = mLastLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);
        this.mLastLocation = location;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRegister:
                checkValidation();
//                startActivity(new Intent(RegistrationsActivity.this, VerifyPhoneActivity.class));
                break;

            case R.id.ivEye:
                if (showPassword) {
                    showPassword = false;
                    ivEye.setImageResource(R.mipmap.ic_eye);
                    edPassword.setTransformationMethod(new PasswordTransformationMethod());
                } else {
                    showPassword = true;
                    ivEye.setImageResource(R.mipmap.ic_eye_off);
                    edPassword.setTransformationMethod(null);
                }
                break;

            case R.id.llBack:
                finish();
                break;
        }
    }

    private void checkValidation() {
        String fame = edFname.getText().toString().trim();
        String lame = edLname.getText().toString().trim();
        String email = edEmail.getText().toString().trim();
        String phone = edPhone.getText().toString().trim();
        String password = edPassword.getText().toString().trim();

        Utils.hideSoftKeyboard(RegistrationsActivity.this);

        if (fame != null) {
            if (fame == null || fame.isEmpty()) {
                edFname.setError(getString(R.string.error_fname));
                edFname.requestFocus();
                return;
            }
        }

        if (lame == null || lame.isEmpty()) {
            edLname.setError(getString(R.string.error_lname));
            edLname.requestFocus();
            return;
        }

        if (rdPhone.isChecked()) {
            if (phone == null || phone.isEmpty() || phone.trim().length() != 10) {
                edPhone.setError(getString(R.string.error_phoneno));
                edPhone.requestFocus();
                return;
            }
        } else if (rdEmail.isChecked()) {
            if (email == null || email.isEmpty() || !(Utils.isValidEmail(email))) {
                edEmail.setError(getString(R.string.error_email));
                edEmail.requestFocus();
                return;
            }
        }

        if (password == null || password.isEmpty() || password.length() < 8) {
            edPassword.setError(getString(R.string.error_password));
            edPassword.requestFocus();
            return;
        }

        if (!Utils.isNetworkAvailable(RegistrationsActivity.this)) {
            Toast.makeText(this, getString(R.string.no_imternet), Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User();
        user.setFirst_name(fame);
        user.setLast_name(lame);
        user.setPassword(password);
        if (rdPhone.isChecked()) {
            user.setIs_email_phone(2);
            user.setPhone_no(edPhone.getText().toString());
            user.setCountry_code("+91");
            user.setEmail("");
        } else {
            user.setIs_email_phone(1);
            user.setPhone_no("");
            user.setEmail(email);
        }
        if (mLastLocation != null)
            user.setPlace(Utils.getPlace(RegistrationsActivity.this, mLastLocation == null ? 0.0 : mLastLocation.getLatitude(), mLastLocation == null ? 0.0 : mLastLocation.getLongitude()));
        else
            user.setPlace("");

        Utils.showProgressDialog(RegistrationsActivity.this);
        Globals.initRetrofit(RegistrationsActivity.this).doRegister(user).enqueue(registrationCallback);
    }

    Callback<BasicResponse> registrationCallback = new Callback<BasicResponse>() {
        @Override
        public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
            Utils.closeProgressDialog();
            if (response.isSuccessful()) {
                if (response.body().response_code <= 0) {
                    Toast.makeText(RegistrationsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    if (response.body().response_code != 200) {
                        Toast.makeText(RegistrationsActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Utils.setPreference(RegistrationsActivity.this, Constants.MM_token, response.body().getUser().getToken());
                    response.body().getUser().password = edPassword.getText().toString().trim();

                    if (response.body().getOtp() == null || response.body().getOtp().isEmpty())
                        response.body().getUser().username = edEmail.getText().toString().trim();
                    else
                        response.body().getUser().username = edPhone.getText().toString().trim();

                    Utils.setPreference(RegistrationsActivity.this, Constants.MM_UserDate, new Gson().toJson(response.body().getUser()));
                    if (response.body().getOtp() == null || response.body().getOtp().isEmpty()) {
                        Intent i = new Intent(RegistrationsActivity.this, SelectCategoryActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Intent i = new Intent(RegistrationsActivity.this, VerifyPhoneActivity.class);
                        i.putExtra(Constants.MM_otp, response.body().getOtp());
                        i.putExtra(Constants.MM_phone_no, "+91" + edPhone.getText().toString());
                        startActivity(i);
                    }
                }
            } else {
                Toast.makeText(RegistrationsActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFailure(Call<BasicResponse> call, Throwable t) {
            Utils.closeProgressDialog();
            t.printStackTrace();
            Toast.makeText(RegistrationsActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
        }
    };

}
