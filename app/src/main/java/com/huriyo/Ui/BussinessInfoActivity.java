package com.huriyo.Ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.huriyo.Model.BasicResponse;
import com.huriyo.Model.Category;
import com.huriyo.Model.UpdatePicture;
import com.huriyo.Model.User;
import com.huriyo.Model.UserDetails;
import com.huriyo.Model.UserNotification;
import com.huriyo.R;
import com.huriyo.Utility.CameraEntity;
import com.huriyo.Utility.CircleImageView;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.Globals;
import com.huriyo.Utility.Picture;
import com.huriyo.Utility.Utils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BussinessInfoActivity extends LocationBaseActivity implements View.OnClickListener {

    Button btnContinue, btnSkip;
    EditText bussiness_vivers, bussiness_desc, bussiness_name, bussiness_location;
    Location mLastLocation;
    Category.Categories categorySelected;
    RelativeLayout rlImages;
    ImageView ivCover, ivEditCover;
    CircleImageView ivProfile;
    UserDetails userDetails;
    User currUserDetails;
    String base64Profile, base64Cover;
    TextView tvTitle;
    boolean isCoverImageClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bussiness_info);

        if (getIntent().getExtras().containsKey(Constants.MM_business_category))
            categorySelected = new Gson().fromJson(getIntent().getExtras().getString(Constants.MM_business_category), Category.Categories.class);
        if (getIntent().getExtras().containsKey(Constants.MM_UserDate))
            userDetails = new Gson().fromJson(getIntent().getExtras().getString(Constants.MM_UserDate), UserDetails.class);

        rlImages = (RelativeLayout) findViewById(R.id.rlImages);
        ivCover = (ImageView) findViewById(R.id.ivCover);
        ivProfile = (CircleImageView) findViewById(R.id.ivProfile);
        ivEditCover = (ImageView) findViewById(R.id.ivEditCover);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        btnSkip = (Button) findViewById(R.id.btnSkip);
        btnContinue = (Button) findViewById(R.id.btnContinue);

        bussiness_vivers = (EditText) findViewById(R.id.bussiness_vivers);
        bussiness_desc = (EditText) findViewById(R.id.bussiness_desc);
        bussiness_name = (EditText) findViewById(R.id.bussiness_name);
        bussiness_location = (EditText) findViewById(R.id.bussiness_location);

        if (categorySelected != null)
            bussiness_vivers.setText(categorySelected.category_name);

        btnSkip.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
        ivProfile.setOnClickListener(this);
        ivEditCover.setOnClickListener(this);

        if (userDetails != null) {
            btnSkip.setVisibility(View.GONE);
            tvTitle.setText("Bussiness Info");
            rlImages.setVisibility(View.VISIBLE);
            bussiness_vivers.setVisibility(View.GONE);
            bussiness_location.setVisibility(View.GONE);

            if (userDetails.user.cover_image != null) {
                Glide.with(BussinessInfoActivity.this).load(userDetails.user.cover_image).into(ivCover);
            }
            if (userDetails.user.profile_image != null) {
                Glide.with(BussinessInfoActivity.this).load(userDetails.user.profile_image).into(ivProfile);
            }

            bussiness_name.setText(userDetails.user.business_name);
            bussiness_desc.setText(userDetails.user.business_description);

        } else {
            rlImages.setVisibility(View.GONE);
        }

        this.picture = new Picture(BussinessInfoActivity.this);
    }

    @Override
    public void onResume() {
        super.onResume();
        currUserDetails = new Gson().fromJson(Utils.getPreference(BussinessInfoActivity.this, Constants.MM_UserDate), User.class);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (currUserDetails == null)
            return;

        Utils.setPreference(BussinessInfoActivity.this, Constants.MM_UserDate, new Gson().toJson(currUserDetails));
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
    public void onBackPressed() {
        moveToMainScreen();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnContinue:
                if (bussiness_name.getText().toString().isEmpty()) {
                    Toast.makeText(this, R.string.enter_bussiness_name, Toast.LENGTH_SHORT).show();
                    return;
                }
                setBussinessInfo();
                break;
            case R.id.btnSkip:
                moveToMainScreen();
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

    private Picture picture;

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

    private boolean showPermissionDialog() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int cameraPermission = ContextCompat.checkSelfPermission(BussinessInfoActivity.this, Manifest.permission.CAMERA);
            int readStoragePermission = ContextCompat.checkSelfPermission(BussinessInfoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);

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

    final private int REQUEST_CODE_CAMERA_PERMISSION = 25314;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (grantResults.length == permissions.length) {
                showDialogOption();
            }
        }
    }

    private void setBussinessInfo() {
        JsonObject object = new JsonObject();
        try {
            String cat = "";
            try {
                if (userDetails != null) {
                    cat = userDetails.user.businessCategoryInfo.get(0)._id;
                } else {
                    cat = categorySelected._id;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            object.addProperty(Constants.MM_business_name, bussiness_name.getText().toString().trim());
            object.addProperty(Constants.MM_business_description, bussiness_desc.getText().toString().trim());
            object.addProperty(Constants.MM_business_address, bussiness_location.getText().toString().trim());
            object.addProperty(Constants.MM_business_category_id, cat);
            object.addProperty(Constants.MM_business_latitude, mLastLocation == null ? 0.0 : mLastLocation.getLatitude());
            object.addProperty(Constants.MM_business_longitude, mLastLocation == null ? 0.0 : mLastLocation.getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.showProgressDialog(BussinessInfoActivity.this);
        Globals.initRetrofit(BussinessInfoActivity.this).setBussinessInfo(object).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Utils.closeProgressDialog();
                if (response.body().response_code == 200) {

                    User userDetails = new Gson().fromJson(Utils.getPreference(getActivity(), Constants.MM_UserDate), User.class);
                    userDetails.business_address = bussiness_location.getText().toString().trim();
                    userDetails.business_description = bussiness_desc.getText().toString().trim();
                    userDetails.business_name = bussiness_name.getText().toString().trim();
                    userDetails.user_type = 1;

                    Utils.setPreference(getActivity(), Constants.MM_UserDate, new Gson().toJson(userDetails));

                    currUserDetails.business_name = bussiness_name.getText().toString().trim();
                    currUserDetails.business_description = bussiness_desc.getText().toString().trim();
                    moveToMainScreen();
                } else {
                    Toast.makeText(BussinessInfoActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                t.printStackTrace();
            }
        });
    }

    public void moveToMainScreen() {

        if (userDetails != null) {
            setResult(RESULT_OK);
            finish();
        } else {
            Intent i = new Intent(BussinessInfoActivity.this, HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
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

        Utils.showProgressDialog(BussinessInfoActivity.this);
        Globals.initRetrofit(BussinessInfoActivity.this).updatePicture(object).enqueue(new Callback<UpdatePicture>() {
            @Override
            public void onResponse(Call<UpdatePicture> call, Response<UpdatePicture> response) {
                Utils.closeProgressDialog();
                if (response.isSuccessful() && response.body() != null && response.body().response_code == 200) {
                    if (type == 1) {
                        userDetails.user.profile_image = response.body().profile_image_url;
                        currUserDetails.setProfile_image_url(response.body().profile_image_url);
                        Glide.with(BussinessInfoActivity.this).load(response.body().profile_image_url).into(ivProfile);
                    } else if (type == 2) {
                        userDetails.user.cover_image = response.body().cover_image_url;
                        currUserDetails.setCover_image_url(response.body().cover_image_url);
                        Glide.with(BussinessInfoActivity.this).load(response.body().cover_image_url).centerCrop().into(ivCover);
                    }
                } else {
                    Toast.makeText(BussinessInfoActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UpdatePicture> call, Throwable t) {
                Utils.closeProgressDialog();
                Toast.makeText(BussinessInfoActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
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
                Glide.with(BussinessInfoActivity.this).load(Uri.parse(path)).into(isCoverImageClicked ? ivCover : ivProfile);
            } else {
                CropImage.activity(from == Picture.REQUEST_CAMERA ? Uri.fromFile(new File(path)) : Uri.parse(path)).setCropShape(CropImageView.CropShape.RECTANGLE).setGuidelines(CropImageView.Guidelines.ON).setRequestedSize(400, 400).setFixAspectRatio(false).start(BussinessInfoActivity.this);
            }
        }
    }

    public static CameraEntity cameraEntity = null;


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
                    Glide.with(BussinessInfoActivity.this).load(Uri.parse(cameraEntity.getPath())).into(isCoverImageClicked ? ivCover : ivProfile);
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
}
