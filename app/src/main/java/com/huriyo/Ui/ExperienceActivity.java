package com.huriyo.Ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.huriyo.Model.UserDetails;
import com.huriyo.Model.UserExperience;
import com.huriyo.R;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.Globals;
import com.huriyo.Utility.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExperienceActivity extends BaseActivity implements View.OnClickListener {

    LinearLayout llBack, llDone;
    EditText edJobTitle, edDescription;
    TextView tvStartDate, tvEndDate;
    CheckBox cbCurrentWorking;
    static final int DATE_PICKER_ID = 1111;

    private int year;
    private int month;
    private int day;
    boolean isStart = true;

    UserDetails.ExperienceInfo experienceInfo = null;
    int position;
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(Constants.MM_Experience_data)) {
            experienceInfo = new Gson().fromJson(getIntent().getExtras().getString(Constants.MM_Experience_data), UserDetails.ExperienceInfo.class);
            position = getIntent().getExtras().getInt(Constants.MM_position);
        }

        llBack = (LinearLayout) findViewById(R.id.llBack);
        llDone = (LinearLayout) findViewById(R.id.llDone);

        llBack.setOnClickListener(this);
        llDone.setOnClickListener(this);

        edJobTitle = (EditText) findViewById(R.id.edJobTitle);
        edDescription = (EditText) findViewById(R.id.edCompnyName);

        tvStartDate = (TextView) findViewById(R.id.tvStartDate);
        tvStartDate.setOnClickListener(this);
        tvEndDate = (TextView) findViewById(R.id.tvEndDate);
        tvEndDate.setOnClickListener(this);

        year = Calendar.getInstance().get(Calendar.YEAR);
        month = Calendar.getInstance().get(Calendar.MONTH);
        day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);


        cbCurrentWorking = (CheckBox) findViewById(R.id.cbCurrentWorking);
        cbCurrentWorking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    tvEndDate.setText("Continue");
                    tvEndDate.setClickable(false);
                    tvEndDate.setEnabled(false);
                } else {
                    tvEndDate.setText(getToday());
                    tvEndDate.setClickable(true);
                    tvEndDate.setEnabled(true);
                }
            }
        });

        Date currentTime = Calendar.getInstance().getTime();
        tvStartDate.setText(getToday());
        tvEndDate.setText(getToday());


        if (experienceInfo != null) {
            edJobTitle.setText(experienceInfo.job_title);
            edDescription.setText(experienceInfo.company_name);
            Date time = new Date(Long.parseLong(experienceInfo.start_date));
            tvStartDate.setText(dateFormat.format(time));
            if (experienceInfo.is_currently_work != 1) {
                Date time2 = new Date(Long.parseLong(experienceInfo.end_date));
                tvEndDate.setText(dateFormat.format(time2));
            } else {
                tvEndDate.setText("Continue");
            }
            if (experienceInfo.is_currently_work == 1)
                cbCurrentWorking.setChecked(true);
        }
    }

    public static String getToday() {
        Date presentTime_Date = Calendar.getInstance().getTime();
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(presentTime_Date);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llBack:
                finish();
                break;
            case R.id.llDone:
                if (edJobTitle.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Enter job title", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edDescription.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Enter description", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (experienceInfo != null)
                    updateExperience();
                else
                    addExperience();
                break;
            case R.id.tvStartDate:
                isStart = true;
                showDialog(DATE_PICKER_ID);
                break;
            case R.id.tvEndDate:
                isStart = false;
                showDialog(DATE_PICKER_ID);
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:
                return new DatePickerDialog(this, pickerListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            if (isStart) {
                tvStartDate.setText(selectedDay + "-" + (selectedMonth + 1) + "-" + selectedYear);
            } else {
                tvEndDate.setText(selectedDay + "-" + (selectedMonth + 1) + "-" + selectedYear);
            }
        }
    };

    private void addExperience() {
        JsonObject object = new JsonObject();
        try {

            DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            if (tvEndDate.getText().toString().equalsIgnoreCase(""))
                object.addProperty(Constants.MM_end_date, "");
            else {
                Date endDate = format.parse(tvEndDate.getText().toString());
                long eTime = endDate.getTime();
                object.addProperty(Constants.MM_end_date, eTime);
            }
            Date startDate = format.parse(tvStartDate.getText().toString());
            long sTime = startDate.getTime();
            object.addProperty(Constants.MM_start_date, sTime);

            object.addProperty(Constants.MM_job_title, edJobTitle.getText().toString());
            object.addProperty(Constants.MM_company_name, edDescription.getText().toString());
            object.addProperty(Constants.MM_is_currently_work, cbCurrentWorking.isChecked() ? 1 : 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.showProgressDialog(ExperienceActivity.this);

        Globals.initRetrofit(ExperienceActivity.this).saveExperience(object).enqueue(new Callback<UserExperience>() {
            @Override
            public void onResponse(Call<UserExperience> call, Response<UserExperience> response) {
                Utils.closeProgressDialog();
                if (response.isSuccessful() && response.body() != null && response.body().response_code == 200) {
                    Intent intent = getIntent();
                    intent.putExtra(Constants.MM_Experience_data, new Gson().toJson(response.body().experience));
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(ExperienceActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserExperience> call, Throwable t) {
                Utils.closeProgressDialog();
                Toast.makeText(ExperienceActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateExperience() {
        JsonObject object = new JsonObject();
        try {
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            if (tvEndDate.getText().toString().equalsIgnoreCase(""))
                object.addProperty(Constants.MM_end_date, "");
            else {
                Date endDate = format.parse(tvEndDate.getText().toString());
                long eTime = endDate.getTime();
                object.addProperty(Constants.MM_end_date, eTime);
            }
            Date startDate = format.parse(tvStartDate.getText().toString());
            long sTime = startDate.getTime();
            object.addProperty(Constants.MM_start_date, sTime);

            object.addProperty(Constants.MM_job_title, edJobTitle.getText().toString());
            object.addProperty(Constants.MM_company_name, edDescription.getText().toString());
            object.addProperty(Constants.MM_is_currently_work, cbCurrentWorking.isChecked() ? 1 : 0);
            object.addProperty(Constants.MM_experience_id, experienceInfo._id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.showProgressDialog(ExperienceActivity.this);

        Globals.initRetrofit(ExperienceActivity.this).updateExperience(object).enqueue(new Callback<UserExperience>() {
            @Override
            public void onResponse(Call<UserExperience> call, Response<UserExperience> response) {
                Utils.closeProgressDialog();
                if (response.isSuccessful() && response.body() != null && response.body().response_code == 200) {
                    Intent intent = getIntent();
                    intent.putExtra(Constants.MM_Experience_data, new Gson().toJson(response.body().experience));
                    intent.putExtra(Constants.MM_position, position);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(ExperienceActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserExperience> call, Throwable t) {
                Utils.closeProgressDialog();
                Toast.makeText(ExperienceActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
