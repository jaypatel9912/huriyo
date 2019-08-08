package com.huriyo.Ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.huriyo.Model.BasicResponse;
import com.huriyo.Model.BussinessUser;
import com.huriyo.Model.Category;
import com.huriyo.Model.UpdatePicture;
import com.huriyo.Model.User;
import com.huriyo.Model.UserDetails;
import com.huriyo.R;
import com.huriyo.Utility.CameraEntity;
import com.huriyo.Utility.CircleImageView;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.Globals;
import com.huriyo.Utility.ImagePickerActivity;
import com.huriyo.Utility.Picture;
import com.huriyo.Utility.Utils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonaInformationActivity extends LocationBaseActivity implements View.OnClickListener {

    private Picture picture;
    public static CameraEntity cameraEntity = null;
    Location mLastLocation;
    LinearLayout llBack, llDone;
    CircleImageView ivProfile;
    ImageView ivCover, ivEditCover;
    EditText edFname, edLname, edAboutMe;
    String base64Profile, base64Cover;
    UserDetails userDetails;
    User currUserDetails;
    final private int REQUEST_CODE_CAMERA_PERMISSION = 25314;
    boolean isCoverImageClicked = false;
    int userType = 0;
    EditText bussiness_desc, bussiness_name, bussiness_location;
    TextView bussiness_vivers;
    Category category;
    Category.Categories selectedCategory = null;
    List<Address> addresses;
    ArrayAdapter<String> arrayAdapter;
    LinearLayout llNormal, llBussess;

    @Override
    public void getLocation(Location mLastLocation) {
        this.mLastLocation = mLastLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);
        this.mLastLocation = location;

//        if (mLastLocation != null) {
//            bussiness_location.setText(Utils.getPlace(PersonaInformationActivity.this, mLastLocation.getLatitude(), mLastLocation.getLongitude()));
//        }
    }

    public void setStatusBarcolor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.statusbar_color));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_persona_information);

        if (getIntent().getExtras() == null || !getIntent().getExtras().containsKey(Constants.MM_UserDate))
            finish();

        if (getIntent().getExtras().containsKey(Constants.MM_type))
            userType = getIntent().getExtras().getInt(Constants.MM_type);

        setStatusBarcolor();
        userDetails = new Gson().fromJson(getIntent().getExtras().getString(Constants.MM_UserDate), UserDetails.class);
        this.picture = new Picture(PersonaInformationActivity.this);

        llBack = (LinearLayout) findViewById(R.id.llBack);
        llDone = (LinearLayout) findViewById(R.id.llDone);

        llBack.setOnClickListener(this);
        llDone.setOnClickListener(this);

        llNormal = (LinearLayout) findViewById(R.id.llNormal);
        llBussess = (LinearLayout) findViewById(R.id.llBussess);

        if (userType == 0) {
            llNormal.setVisibility(View.VISIBLE);
            llBussess.setVisibility(View.GONE);
        } else {
            llNormal.setVisibility(View.GONE);
            llBussess.setVisibility(View.VISIBLE);
            getCategories();
        }

        ivProfile = (CircleImageView) findViewById(R.id.ivProfile);

        ivCover = (ImageView) findViewById(R.id.ivCover);
        ivEditCover = (ImageView) findViewById(R.id.ivEditCover);
        ivProfile.setOnClickListener(this);
        ivEditCover.setOnClickListener(this);

        edFname = (EditText) findViewById(R.id.edFname);
        edLname = (EditText) findViewById(R.id.edLname);
        edAboutMe = (EditText) findViewById(R.id.edAboutMe);

        bussiness_vivers = (TextView) findViewById(R.id.bussiness_vivers);
        bussiness_desc = (EditText) findViewById(R.id.bussiness_desc);
        bussiness_name = (EditText) findViewById(R.id.bussiness_name);
        bussiness_location = (EditText) findViewById(R.id.bussiness_location);

        setData();
    }


    private void setData() {
        if (userDetails.user.first_name != null)
            edFname.setText(userDetails.user.first_name);
        if (userDetails.user.last_name != null)
            edLname.setText(userDetails.user.last_name);
        if (userDetails.user.about_me != null)
            edAboutMe.setText(userDetails.user.about_me);

        if (userType != 0) {

            bussiness_name.setText(userDetails.user.business_name);
            bussiness_desc.setText(userDetails.user.business_description);

            selectedCategory = new Category.Categories();
            selectedCategory._id = userDetails.user.businessCategoryInfo.get(0)._id;

            selectedCategory.category_name = userDetails.user.businessCategoryInfo.get(0).category_name;

            bussiness_vivers.setText(selectedCategory.category_name);

            bussiness_location.setText(userDetails.user.business_address);

            bussiness_vivers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showCategoryDialog();
                }
            });
        }
        if (userDetails.user.cover_image != null) {
            Glide.with(PersonaInformationActivity.this).load(userDetails.user.cover_image).into(ivCover);
        }
        if (userDetails.user.profile_image != null) {
            Glide.with(PersonaInformationActivity.this).load(userDetails.user.profile_image).into(ivProfile);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llBack:
                finishProfile();
                break;
            case R.id.llDone:

                Utils.hideSoftKeyboard(PersonaInformationActivity.this);

                if (userType == 0) {
                    if (edFname.getText().toString().isEmpty()) {
                        edFname.requestFocus();
                        Toast.makeText(this, "Please enter first name", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (edLname.getText().toString().isEmpty()) {
                        edLname.requestFocus();
                        Toast.makeText(this, "Please enter last name", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (edAboutMe.getText().toString().isEmpty()) {
                        edAboutMe.requestFocus();
                        Toast.makeText(this, "Please tell something about you", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    updateProfile();

                } else {
                    if (bussiness_name.getText().toString().isEmpty()) {
                        Toast.makeText(this, R.string.enter_bussiness_name, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Geocoder geocoder = new Geocoder(PersonaInformationActivity.this);
                    try {
                        addresses = geocoder.getFromLocationName(bussiness_location.getText().toString(), 5);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setBussinessInfo();
                }

                break;
            case R.id.ivProfile:
                isCoverImageClicked = false;
                if (showPermissionDialog())
                    showDialogOption();
                break;
            case R.id.ivEditCover:
                isCoverImageClicked = true;
                if (showPermissionDialog())
                    showDialogOption();
                break;
        }
    }

    private void showDialogOption() {
        final String[] items = getResources().getStringArray(R.array.camera_option);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.select_dialog_item_material, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.picker_select_image));
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                try {
                    if (item == 0) {
                        picture.intentCamera();
                    } else {
                        picture.intentGallery();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updatePicture(final int type) {

        JsonObject object = new JsonObject();
        try {
            if (type == 1 && base64Profile == null)
                return;
            if (type == 2 && base64Cover == null)
                return;

            object.addProperty(Constants.MM_base64Data, type == 1 ? base64Profile : base64Cover);
            object.addProperty(Constants.MM_type, type);
            object.addProperty(Constants.MM_extension, "png");

            String url = type == 1 ? userDetails.user.profile_image : userDetails.user.cover_image;

            String fileNameWithoutExtn = "";
            if (url != null)
                fileNameWithoutExtn = url.substring(url.lastIndexOf('/') + 1, url.length());

            object.addProperty(Constants.MM_old_image, fileNameWithoutExtn);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.showProgressDialog(PersonaInformationActivity.this);
        Globals.initRetrofit(PersonaInformationActivity.this).updatePicture(object).enqueue(new Callback<UpdatePicture>() {
            @Override
            public void onResponse(Call<UpdatePicture> call, Response<UpdatePicture> response) {
                Utils.closeProgressDialog();
                if (response.isSuccessful() && response.body() != null && response.body().response_code == 200) {
                    if (type == 1) {
                        userDetails.user.profile_image = response.body().profile_image_url;
                        currUserDetails.setProfile_image_url(response.body().profile_image_url);
                        Glide.with(PersonaInformationActivity.this).load(response.body().profile_image_url).into(ivProfile);
                    } else if (type == 2) {
                        userDetails.user.cover_image = response.body().cover_image_url;
                        currUserDetails.setCover_image_url(response.body().cover_image_url);
                        Glide.with(PersonaInformationActivity.this).load(response.body().cover_image_url).centerCrop().into(ivCover);
                    }
                } else {
                    Toast.makeText(PersonaInformationActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UpdatePicture> call, Throwable t) {
                Utils.closeProgressDialog();
                Toast.makeText(PersonaInformationActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean showPermissionDialog() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int cameraPermission = ContextCompat.checkSelfPermission(PersonaInformationActivity.this, Manifest.permission.CAMERA);
            int readStoragePermission = ContextCompat.checkSelfPermission(PersonaInformationActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);

            List<String> listPermissionsNeeded = new ArrayList<>();

            if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA);
            }

            if (readStoragePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

            if (!listPermissionsNeeded.isEmpty()) {
                Log.i("Permission size", listPermissionsNeeded.size() + " ");
                requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_CODE_CAMERA_PERMISSION);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (grantResults.length == permissions.length) {
                showDialogOption();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        currUserDetails = new Gson().fromJson(Utils.getPreference(PersonaInformationActivity.this, Constants.MM_UserDate), User.class);
    }

    @Override
    public void onPause() {
        super.onPause();
        Utils.setPreference(PersonaInformationActivity.this, Constants.MM_UserDate, new Gson().toJson(currUserDetails));
    }

    private void updateProfile() {

        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_first_name, edFname.getText().toString().trim());
            object.addProperty(Constants.MM_last_name, edLname.getText().toString().trim());
            object.addProperty(Constants.MM_about_me, edAboutMe.getText().toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.showProgressDialog(PersonaInformationActivity.this);
        Globals.initRetrofit(PersonaInformationActivity.this).updateProfile(object).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Utils.closeProgressDialog();
                if (response.isSuccessful() && response.body() != null && response.body().response_code == 200) {
                    userDetails.user.about_me = edAboutMe.getText().toString().trim();
                    userDetails.user.first_name = edFname.getText().toString().trim();
                    userDetails.user.last_name = edLname.getText().toString().trim();
                    currUserDetails.setFirst_name(edFname.getText().toString().trim());
                    currUserDetails.setLast_name(edLname.getText().toString().trim());
                    currUserDetails.setAbout_me(edAboutMe.getText().toString().trim());
                    finishProfile();
                } else {
                    Toast.makeText(PersonaInformationActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                Toast.makeText(PersonaInformationActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishProfile();
    }

    private void finishProfile() {
        Intent intent = getIntent();
        intent.putExtra(Constants.MM_UserDate, new Gson().toJson(userDetails));
        setResult(RESULT_OK, intent);
        finish();
    }

    class PictureListener implements Picture.OnPictureUpdate {
        PictureListener() {
        }

        public void onUpdateCamera(String path) {
            savePicture(path, false, Picture.REQUEST_CAMERA);
        }

        public void onUpdateGallery(Uri uri) {
            savePicture(uri.toString(), false, Picture.REQUEST_GALLERY);
        }

        private void savePicture(String path, boolean cropped, int request) {
            if (cameraEntity == null) {
                cameraEntity = new CameraEntity();
            }
            cameraEntity.setPath(path);
            cameraEntity.setCropped(cropped);
            cameraEntity.setRequestType(request);
            int from = cameraEntity.getRequestType();
            if (cropped) {
                Glide.with(PersonaInformationActivity.this).load(Uri.parse(path)).into(isCoverImageClicked ? ivCover : ivProfile);
            } else {
                CropImage.activity(from == Picture.REQUEST_CAMERA ? Uri.fromFile(new File(path)) : Uri.parse(path)).setCropShape(CropImageView.CropShape.RECTANGLE).setGuidelines(CropImageView.Guidelines.ON).setRequestedSize(400, 400).setFixAspectRatio(false).start(PersonaInformationActivity.this);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 203) {
            this.picture.result(requestCode, resultCode, data, new PictureListener());
        } else if (resultCode == -1) {
            try {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (result != null) {
                    if (cameraEntity == null) {
                        cameraEntity = new CameraEntity();
                    }
                    cameraEntity.setPath(result.getUri().toString());
                    cameraEntity.setCropped(true);
                    Glide.with(PersonaInformationActivity.this).load(Uri.parse(cameraEntity.getPath())).into(isCoverImageClicked ? ivCover : ivProfile);
                    Bitmap bitmap = BitmapFactory.decodeFile(new File(result.getUri().getPath()).getAbsolutePath());
                    if (isCoverImageClicked) {
                        base64Cover = Utils.encodeTobase64(bitmap);
                        updatePicture(2);
                    } else {
                        base64Profile = Utils.encodeTobase64(bitmap);
                        updatePicture(1);
                    }

                    if (bitmap != null) {
                        bitmap.recycle();
                        bitmap = null;
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setBussinessInfo() {
        JsonObject object = new JsonObject();
        try {

            String cat = "";

            try {
                cat = userDetails.user.businessCategoryInfo.get(0)._id;
            } catch (Exception e) {
                e.printStackTrace();
            }

            object.addProperty(Constants.MM_business_name, bussiness_name.getText().toString().trim());
            object.addProperty(Constants.MM_business_description, bussiness_desc.getText().toString().trim());
            object.addProperty(Constants.MM_business_address, bussiness_location.getText().toString().trim());
            object.addProperty(Constants.MM_business_category_id, cat);
            object.addProperty(Constants.MM_business_latitude, addresses == null ? 0.0 : addresses.get(0).getLatitude());
            object.addProperty(Constants.MM_business_longitude, addresses == null ? 0.0 : addresses.get(0).getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utils.showProgressDialog(PersonaInformationActivity.this);
        Globals.initRetrofit(PersonaInformationActivity.this).setBussinessInfo(object).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Utils.closeProgressDialog();
                if (response.body().response_code == 200) {
                    userDetails.user.business_name = bussiness_name.getText().toString().trim();
                    userDetails.user.business_description = bussiness_desc.getText().toString().trim();
                    userDetails.user.businessCategoryInfo.get(0).category_name = selectedCategory.category_name;
                    userDetails.user.businessCategoryInfo.get(0)._id = selectedCategory._id;
                    finishProfile();
                } else {
                    Toast.makeText(PersonaInformationActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                t.printStackTrace();
            }
        });
    }

    public void getCategories() {
        Utils.showProgressDialog(PersonaInformationActivity.this);
        Globals.initRetrofit(PersonaInformationActivity.this).getCategories().enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                Utils.closeProgressDialog();
                if (response.isSuccessful()) {
                    category = response.body();
                    if (category == null || category.categories == null || category.categories.size() <= 0) {
                        Toast.makeText(PersonaInformationActivity.this, "No Categories available", Toast.LENGTH_SHORT).show();
                    } else {
                        arrayAdapter = new ArrayAdapter<String>(PersonaInformationActivity.this, android.R.layout.select_dialog_singlechoice);
                        for (Category.Categories categories : category.categories) {
                            arrayAdapter.add(categories.category_name);
                        }
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

    public void showCategoryDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(PersonaInformationActivity.this);

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedCategory = category.categories.get(which);
                bussiness_vivers.setText(selectedCategory.category_name);
                dialog.dismiss();
            }
        });
        builderSingle.show();
    }
}
