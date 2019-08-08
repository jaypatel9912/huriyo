package com.huriyo.Utility;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Picture {
    public static final int REQUEST_CAMERA = 8881;
    public static final int REQUEST_GALLERY = 8882;
    private static String mCurrentPhotoPath;
    private Activity activity;
    public static final String ROLL_OVER_FILE_NAME_SEPARATOR = "_";

    public interface OnPictureUpdate {
        void onUpdateCamera(String str);

        void onUpdateGallery(Uri uri);
    }

    public Picture(Activity activity) {
        this.activity = activity;
    }

    public void intentGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        intent.addCategory("android.intent.category.OPENABLE");
        this.activity.startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem"), REQUEST_GALLERY);
    }

    public void result(int requestCode, int resultCode, Intent data, OnPictureUpdate onPictureUpdate) {
        if (requestCode == REQUEST_CAMERA && resultCode == -1) {
            galleryAddPic();
            onPictureUpdate.onUpdateCamera(getCurrentPhoto());
        } else if (requestCode == REQUEST_GALLERY && resultCode == -1) {
            onPictureUpdate.onUpdateGallery(data.getData());
        }
    }

    public String getCurrentPhoto() {
        return mCurrentPhotoPath;
    }

    public void intentCamera() {
        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (takePictureIntent.resolveActivity(this.activity.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                takePictureIntent.putExtra("output", FileProvider.getUriForFile(this.activity, "com.huriyo.fileprovider", photoFile));
                this.activity.startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }

    private File createImageFile() throws IOException {
        File image = File.createTempFile("JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ROLL_OVER_FILE_NAME_SEPARATOR, ".jpg", this.activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        mediaScanIntent.setData(Uri.fromFile(new File(mCurrentPhotoPath)));
        this.activity.sendBroadcast(mediaScanIntent);
    }
}
