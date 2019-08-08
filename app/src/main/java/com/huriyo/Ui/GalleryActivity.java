package com.huriyo.Ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huriyo.Adapter.GalleryAdapter;
import com.huriyo.Model.GalleryItem;
import com.huriyo.R;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.ItemDecorationAlbumColumns;
import com.huriyo.Utility.RecyclerItemClickListener;
import com.huriyo.Utility.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends BaseActivity implements View.OnClickListener {

    RecyclerView recyclerView;
    LinearLayout llBack, llDone;
    ArrayList<GalleryItem> list;
    ArrayList<GalleryItem> selectedList;
    GalleryAdapter adapter;
    final private int REQUEST_CODE_CAMERA_PERMISSION = 234;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        list = new ArrayList<>();
        selectedList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        llBack = (LinearLayout) findViewById(R.id.llBack);
        llDone = (LinearLayout) findViewById(R.id.llDone);
        llBack.setOnClickListener(this);
        llDone.setOnClickListener(this);
        adapter = new GalleryAdapter(this);
        recyclerView.setAdapter(adapter);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.grid_margin);

        recyclerView.addItemDecoration(new ItemDecorationAlbumColumns(4, spacingInPixels, true, 0));
        GridLayoutManager manager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        showPermissionDialog();

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        GalleryItem item = list.get(position);


                        if (!item.isSelected()) {

                            if (selectedList.size() == 10) {
                                Toast.makeText(GalleryActivity.this, "Maximum 10 images or video can be uploaded at a tvPosition", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            selectedList.add(item);
                        } else {
                            for (int i = 0; i < selectedList.size(); i++) {
                                if (selectedList.get(i).getPath().equals(item.getPath()))
                                    selectedList.remove(i);
                            }
                        }
                        item.setSelected(!item.isSelected());
                        adapter.doRefreshItem(item, position);
                    }
                })
        );

    }

    public void getPhoneAlbums() {

        if (ContextCompat.checkSelfPermission(GalleryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Utils.showProgressDialog(GalleryActivity.this);
        // Creating vectors to hold the final albums objects and albums names
        // which image properties are we querying
        String[] projection = new String[]{
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE
        };

        // content: style URI for the "primary" external storage volume
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        Uri queryUri = MediaStore.Files.getContentUri("external");

        CursorLoader cursorLoader = new CursorLoader(
                this,
                queryUri,
                projection,
                selection,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
        );

        Cursor cur = cursorLoader.loadInBackground();

        if (cur != null && cur.getCount() > 0) {
            Log.i("DeviceImageManager", " query count=" + cur.getCount());

            if (cur.moveToFirst()) {
                String bucketName;
                String data;
                String imageId;
                int bucketNameColumn = cur.getColumnIndex(
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

                int mediaPath = cur.getColumnIndex(
                        MediaStore.Files.FileColumns.DATA);

                int mediaType = cur.getColumnIndex(
                        MediaStore.Files.FileColumns.MIME_TYPE);

                do {
                    // Get the field values
                    data = cur.getString(mediaPath);
                    imageId = cur.getString(mediaType);
                    if (new File(data).length() > 0) {
                        int type = 0;
                        if (imageId != null && imageId.toLowerCase().contains("image"))
                            type = 1;
                        else if (imageId != null && imageId.toLowerCase().contains("video"))
                            type = 2;

                        if (type > 0)
                            list.add(new GalleryItem(data, type));
                    }
                    // Adding a new PhonePhoto object to phonePhotos vector
                } while (cur.moveToNext());
            }

            cur.close();
        }

        adapter.doRefresh(list);
        Utils.closeProgressDialog();
    }

    private void showPermissionDialog() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int cameraPermission = ContextCompat.checkSelfPermission(GalleryActivity.this, Manifest.permission.CAMERA);
            int readStoragePermission = ContextCompat.checkSelfPermission(GalleryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);

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
            } else {
                getPhoneAlbums();
            }
        } else {
            getPhoneAlbums();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (grantResults.length == permissions.length) {
                getPhoneAlbums();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 32512){
            Intent intent = getIntent();
            intent.putExtra(Constants.MM_Data, data.getExtras().getString(Constants.MM_Data));
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llBack:
                finish();
                break;

            case R.id.llDone:
                if(selectedList == null || selectedList.size() <= 0){
                    Toast.makeText(this, "Please select image(s).", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(GalleryActivity.this, ImageCropActivity.class);
                intent.putExtra(Constants.MM_Data, new Gson().toJson(selectedList));
                startActivityForResult(intent, 32512);
                break;
        }
    }
}
