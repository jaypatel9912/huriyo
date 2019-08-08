package com.huriyo.Utility;


import android.Manifest.permission;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;


import com.huriyo.R;
import com.huriyo.Ui.LocationBaseActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public abstract class ImagePickerActivity extends LocationBaseActivity {

    String TAG = getClass().getName();

    public static final int REQUEST_CODE_GALLERY = 1354;
    public static final int REQUEST_CODE_TAKE_PICTURE = 5484;
    public static final int REQUEST_CODE_CROP_IMAGE = 2453;

    private File mFileTemp = null;

    Bitmap selectedBitmap = null;
    String encodeSelectedImageString = null;
    int imgResID;
    ImagePickerEventOption pickerEventAction;
    boolean isRequireCrop = false;

    public enum ImagePickerEventOption {
        SHOW_DIALOG,
        SELECT_FROM_GALLARY,
        TAKE_A_PICTURE;
    }

    Uri fileUri;

    public void takePicture(boolean isRequireCrop) {
        this.isRequireCrop = isRequireCrop;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            Uri mImageCaptureUri = null;
//			String state = Environment.getExternalStorageState();
//			if (Environment.MEDIA_MOUNTED.equals(state)) {
//				mImageCaptureUri = Uri.fromFile(mFileTemp);
//			} else {
            mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
//			}
//            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
//            intent.putExtra("return-data", true);
//            startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);

            fileUri = getOutputMediaFileUri();

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // start the image capture Intent
            startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);


        } catch (ActivityNotFoundException e) {
            Log.d(TAG, "cannot take picture", e);
        }
    }

    public void openGallery(boolean isRequireCrop) {
        this.isRequireCrop = isRequireCrop;
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
    }

    private void startCropImage(int reqCode) {

        if (mFileTemp != null) {
//            Intent intent = new Intent(this, CropImage.class);
//            intent.putExtra(CropImage.IMAGE_PATH, reqCode == REQUEST_CODE_GALLERY ? mFileTemp.getAbsolutePath() : fileUri.getPath());
//            intent.putExtra(CropImage.SCALE, false);
//
//            intent.putExtra(CropImage.ASPECT_X, 2);
//            intent.putExtra(CropImage.ASPECT_Y, 2);

//            startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);

            CropImage.activity(reqCode == REQUEST_CODE_GALLERY ? Uri.fromFile(mFileTemp) : fileUri)
                    .start(this);

        }
    }

    private static final String[] LOCATION_PERMS = {
            permission.CAMERA,
    };

    private static final int CAMERA_REQUEST = 133;

    public void handleImageSelectionEvent(final int imgResID, Bitmap bitmap, boolean isRequireCrop) {
        this.isRequireCrop = isRequireCrop;
        Utils.hideSoftKeyboard(ImagePickerActivity.this);
        this.imgResID = imgResID;
        selectedBitmap = bitmap;
        showImagePickerDialog(imgResID, ImagePickerEventOption.SHOW_DIALOG);

    }


    static boolean isImageDownloaded = false;


    public void setFilePath() {
//		if(mFileTemp == null) {
//	    	String state = Environment.getExternalStorageState();
//	    	if (Environment.MEDIA_MOUNTED.equals(state)) {
//	    		mFileTemp = new File(Environment.getExternalStorageDirectory(), Constant.TEMP_PHOTO_FILE_NAME);
//	    	} else {
        mFileTemp = new File(getFilesDir(), Constants.TEMP_PHOTO_FILE_NAME);
//	    	}
//		}
    }

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    /*
     * returning image / video
     */

    private static final String IMAGE_DIRECTORY_NAME = "Images";

    private static File getOutputMediaFile() {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }


    public void showImagePickerDialog(int imgResID, ImagePickerEventOption pickerEventAction) {
        this.imgResID = imgResID;
        this.pickerEventAction = pickerEventAction;

        if (android.support.v4.app.ActivityCompat.checkSelfPermission(this, permission.CAMERA) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            android.support.v4.app.ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_REQUEST);
            return;
        } else {
            showDialog();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case CAMERA_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showDialog();
                } else {
                }
                break;
        }
    }


    public void showDialog() {
        switch (pickerEventAction) {

            case SHOW_DIALOG:

                final String[] items = getResources().getStringArray(R.array.camera_option);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.select_dialog_item_material, items);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle(getString(R.string.picker_select_image));
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        setFilePath();
                        if (item == 0) {
                            takePicture(isRequireCrop);
                        } else {
                            openGallery(isRequireCrop);
                        }
                        dialog.cancel();
                    }
                });

                final AlertDialog dialog = builder.create();
                dialog.show();

                break;

            case SELECT_FROM_GALLARY:
                setFilePath();
                openGallery(isRequireCrop);
                break;


            case TAKE_A_PICTURE:
                setFilePath();
                takePicture(isRequireCrop);
                break;
        }
    }



    public abstract void onImageSelected(int imgResID, Bitmap bitmap, String encodeImageString, File imageFile);

    public static boolean isValideFileSize(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 90, stream);
        byte[] imageInByte = stream.toByteArray();
        // Get length of file in bytes
        long fileSizeInBytes = imageInByte.length;

        if (getFileSizeInMB(fileSizeInBytes) <= Constants.MM_Max_File_Sie_Of_Doc_MB) {
            return true;
        } else {
            return false;
        }
    }

    public static long getFileSizeInMB(long fileSizeInBytes) {
        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        long fileSizeInKB = fileSizeInBytes / 1024;
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        long fileSizeInMB = fileSizeInKB / 1024;

        return fileSizeInMB;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }

        Bitmap bitmap;

        switch (requestCode) {

            case REQUEST_CODE_GALLERY:
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
                    copyStream(inputStream, fileOutputStream);
                    fileOutputStream.close();
                    inputStream.close();

                    if (isRequireCrop) {
                        startCropImage(REQUEST_CODE_GALLERY);
                    } else {
                        String path = mFileTemp.getAbsolutePath();
                        if (path == null) {
                            return;
                        }

                        bitmap = BitmapFactory.decodeFile(path);

                        if (bitmap != null) {
                            selectedBitmap = bitmap;
                        }
                        encodeSelectedImageString = Utils.encodeTobase64(bitmap);
                        onImageSelected(imgResID, bitmap, encodeSelectedImageString, mFileTemp);
                    }

                } catch (Exception e) {
                    Log.e(TAG, "Error while creating temp file", e);
                }

                break;
            case REQUEST_CODE_TAKE_PICTURE:
                if (isRequireCrop) {
                    startCropImage(REQUEST_CODE_TAKE_PICTURE);
                } else {

                    if (fileUri == null || fileUri.getPath() == null) {
                        return;
                    }

                    String path = fileUri.getPath();


                    bitmap = BitmapFactory.decodeFile(path);

                    if (bitmap != null) {
                        selectedBitmap = bitmap;
                    }
                    encodeSelectedImageString = Utils.encodeTobase64(bitmap);
                    onImageSelected(imgResID, bitmap, encodeSelectedImageString, mFileTemp);
                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:

                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    String path = resultUri.getPath();
                    if (path == null) {
                        return;
                    }

                    bitmap = BitmapFactory.decodeFile(path);

                    if (bitmap != null) {
                        selectedBitmap = bitmap;
                    }
                    encodeSelectedImageString = Utils.encodeTobase64(bitmap);
                    onImageSelected(imgResID, bitmap, encodeSelectedImageString, mFileTemp);

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    return;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static void copyStream(InputStream input, OutputStream output) throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] filePathColumn = {MediaStore.Images.ImageColumns.DATA};
        Cursor cursor = getContentResolver().query(contentUri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        String picturePath = cursor.getString(0);
        cursor.close();
        return picturePath;
    }
}
