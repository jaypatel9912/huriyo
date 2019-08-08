package com.huriyo.Fragment;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.huriyo.Model.User;
import com.huriyo.R;
import com.huriyo.Ui.AboutUsActivity;
import com.huriyo.Ui.BussinessUsersActivity;
import com.huriyo.Ui.ChangePasswordActivity;
import com.huriyo.Ui.ContactUsActivity;
import com.huriyo.Ui.HomeActivity;
import com.huriyo.Ui.MainActivity;
import com.huriyo.Ui.PrivacyAndTermsActivity;
import com.huriyo.Ui.ProfileActivity;
import com.huriyo.Utility.CircleImageView;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.FragmentBase;
import com.huriyo.Utility.Globals;
import com.huriyo.Utility.Screen;
import com.huriyo.Utility.Utils;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jay on 07/11/17.
 */

public class SettingFragment extends FragmentBase<HomeActivity> implements View.OnClickListener {

    SwitchButton sb_push, sb_email, sb_sms;
    AppCompatRadioButton rdLow, rdMed, rdHigh;
    LinearLayout llClearHistory, llContactUs, llAboutUs, llPrivacyPolicy, llTermsConditions, llLogout, llChangePassword;
    View view;
    CircleImageView ivProfile;
    TextView tvName, tvDesc;
    LinearLayout llProfile, llGiveReview, llShare;


    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    @Override
    public Screen getCode() {
        return Screen.SETTING;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, container, false);

        sb_push = (SwitchButton) view.findViewById(R.id.sb_push);
        sb_email = (SwitchButton) view.findViewById(R.id.sb_email);
        sb_sms = (SwitchButton) view.findViewById(R.id.sb_sms);

        tvName = (TextView) view.findViewById(R.id.tvName);
        tvDesc = (TextView) view.findViewById(R.id.tvDesc);

        llProfile = (LinearLayout) view.findViewById(R.id.llProfile);
        llProfile.setOnClickListener(this);
        ivProfile = (CircleImageView) view.findViewById(R.id.ivProfile);

        rdLow = (AppCompatRadioButton) view.findViewById(R.id.rdLow);
        rdMed = (AppCompatRadioButton) view.findViewById(R.id.rdMed);
        rdHigh = (AppCompatRadioButton) view.findViewById(R.id.rdHigh);

        llChangePassword = (LinearLayout) view.findViewById(R.id.llChangePassword);
        llChangePassword.setOnClickListener(this);
        llClearHistory = (LinearLayout) view.findViewById(R.id.llClearHistory);
        llClearHistory.setOnClickListener(this);
        llContactUs = (LinearLayout) view.findViewById(R.id.llContactUs);
        llContactUs.setOnClickListener(this);
        llAboutUs = (LinearLayout) view.findViewById(R.id.llAboutUs);
        llAboutUs.setOnClickListener(this);
        llPrivacyPolicy = (LinearLayout) view.findViewById(R.id.llPrivacyPolicy);
        llPrivacyPolicy.setOnClickListener(this);
        llTermsConditions = (LinearLayout) view.findViewById(R.id.llTermsConditions);
        llTermsConditions.setOnClickListener(this);
        llLogout = (LinearLayout) view.findViewById(R.id.llLogout);
        llLogout.setOnClickListener(this);

        llGiveReview = (LinearLayout) view.findViewById(R.id.llGiveReview);
        llGiveReview.setOnClickListener(this);
        llShare = (LinearLayout) view.findViewById(R.id.llShare);
        llShare.setOnClickListener(this);

        String quality = Utils.getPreference(getActivity(), Constants.MM_quality);
        if (quality == null || quality.isEmpty()) {
            Utils.setPreference(getActivity(), Constants.MM_quality, "2");
            quality = "2";
        }

        if (quality.equalsIgnoreCase("2"))
            rdMed.setChecked(true);
        else if (quality.equalsIgnoreCase("1"))
            rdLow.setChecked(true);
        else if (quality.equalsIgnoreCase("3"))
            rdHigh.setChecked(true);

        rdMed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rdLow.setChecked(false);
                    rdHigh.setChecked(false);
                    Utils.setPreference(getActivity(), Constants.MM_quality, "2");
                }
            }
        });

        rdLow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rdMed.setChecked(false);
                    rdHigh.setChecked(false);
                    Utils.setPreference(getActivity(), Constants.MM_quality, "1");
                }
            }
        });

        rdHigh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rdMed.setChecked(false);
                    rdLow.setChecked(false);
                    Utils.setPreference(getActivity(), Constants.MM_quality, "3");
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        User userDetails = new Gson().fromJson(Utils.getPreference(getActivity(), Constants.MM_UserDate), User.class);
        if (userDetails.profile_image_url != null) {
            Glide.with(getActivity()).load(userDetails.profile_image_url).into(ivProfile);
        }

        try {
            if (userDetails.user_type > 0) {
                tvName.setText(userDetails.business_name);
                tvDesc.setText(userDetails.business_description);
            } else {
                tvName.setText(userDetails.first_name + " " + userDetails.last_name);
                tvDesc.setText(userDetails.about_me);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llProfile:
                startActivity(new Intent(getActivity(), ProfileActivity.class));
                break;
            case R.id.llClearHistory:
                doClearHistory();
                break;
            case R.id.llContactUs:
                startActivity(new Intent(getActivity(), ContactUsActivity.class));
                break;
            case R.id.llChangePassword:
                startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
                break;
            case R.id.llAboutUs:
                startActivity(new Intent(getActivity(), AboutUsActivity.class));
                break;
            case R.id.llPrivacyPolicy:
                Intent pp = new Intent(getActivity(), PrivacyAndTermsActivity.class);
                pp.putExtra(Constants.MM_is_privacy, true);
                startActivity(pp);
                break;
            case R.id.llTermsConditions:
                Intent tc = new Intent(getActivity(), PrivacyAndTermsActivity.class);
                tc.putExtra(Constants.MM_is_privacy, false);
                startActivity(tc);
                break;
            case R.id.llLogout:
                logout();
                break;

            case R.id.llGiveReview:
                launchMarket();
                break;

            case R.id.llShare:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out Huriyo, it is an awesome app which provides platform to expand your business. \nhttps://play.google.com/store/apps/details?id=com.huriyo");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
        }
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }

    private void doClearHistory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.cHistory_title));
        builder.setMessage(getString(R.string.cHistory_message));
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                Utils.setPreference(getActivity(), Constants.MM_recent, new Gson().toJson(new ArrayList<String>()));
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void logout() {
        Utils.showProgressDialog(getActivity());
        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_device_token, Utils.getPreference(getActivity(), Constants.MM_noti_token));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Globals.initRetrofit(getActivity()).logout(object).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Utils.closeProgressDialog();
                if (response.isSuccessful()) {
                    Utils.setPreference(getActivity(), Constants.MM_UserDate, "");
                    Utils.setPreference(getActivity(), Constants.MM_token, "");
                    Intent i = new Intent(getActivity(), BussinessUsersActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    getActivity().finish();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Utils.closeProgressDialog();
                t.printStackTrace();
            }
        });
    }
}


