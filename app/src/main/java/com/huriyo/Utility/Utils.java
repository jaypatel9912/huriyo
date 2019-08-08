package com.huriyo.Utility;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.huriyo.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class Utils {
    public static Dialog dialog;
    static SharedPreferences prefs;
    private static Typeface tf, tf1, tf2, tf3, tf4;
    public static Bitmap bgBitmap = null;
    public static RotateLoading loading;


    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static Animation animation;

    public static Animation getClickRipple(Context context) {
        if (animation == null) {
            animation = AnimationUtils.loadAnimation(context, R.anim.click_ripple);
        }
        return animation;
    }


    public static void closeProgressDialog() {
        try {
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void showProgressDialog(Context context) {
        if (dialog == null) {
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.custom_progress_layout);
            dialog.getWindow().setDimAmount(0);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            loading = (RotateLoading) dialog.findViewById(R.id.rotateloading);
            loading.start();
            dialog.show();
        }
    }

    public static Typeface getTypefaceNormal(Context context) {
        if (tf1 != null) {
            return tf1;
        } else {
            tf1 = Typeface.createFromAsset(context.getAssets(), "SanFranciscoText-Regular.otf");
            return tf1;
        }
    }

    public static Typeface getTypefaceCurly(Context context) {
        if (tf2 != null) {
            return tf2;
        } else {
            tf2 = Typeface.createFromAsset(context.getAssets(), "Savoye-LET-Plain.ttf");
            return tf2;
        }
    }

    public static Typeface getTypefaceBold(Context context) {
        if (tf3 != null) {
            return tf3;
        } else {
            tf3 = Typeface.createFromAsset(context.getAssets(), "SanFranciscoText-Bold.otf");
            return tf3;
        }
    }

    public static Typeface getTypefaceHeavy(Context context) {
        if (tf4 != null) {
            return tf4;
        } else {
            tf4 = Typeface.createFromAsset(context.getAssets(), "SanFranciscoText-Heavy.otf");
            return tf4;
        }
    }

    public static void showMessage1(Context context, String title, String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle(title);
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public static void showMessage2(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

//    public static void saveBooleanToUserDefaults(Context context, String key, boolean value) {
//        getSharedPreference(context).edit().putBoolean(key, value).apply();
//    }
//
//    public static Boolean getBooleanFromUserDefaults(Context context, String key) {
//        Log.d("Utils", "Get:" + key);
//        return getSharedPreference(context).getBoolean(key, false);
//    }


    public static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    public static SharedPreferences getSharedPreference(Context context) {
        return prefs != null ? prefs : context.getSharedPreferences(Constants.APP_PACKAGE_NAME, Context.MODE_PRIVATE);
    }

    public static String getPreference(Context context, String key) {
        return getSharedPreference(context).getString(key, "");
    }

    public static void setPreference(Context context, String key, String value) {
        getSharedPreference(context).edit().putString(key, value).apply();
    }

//    public static String getBase64String(Bitmap bmp){
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//        byte[] byteArray = byteArrayOutputStream .toByteArray();
//        return Base64.encodeToString(byteArray, Base64.DEFAULT);
//    }

    public static String getBase64String(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String input = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return input;
    }


    public static String capitalizeString(String str) {
        String[] strArray = str.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(cap + " ");
        }
        return builder.toString();
    }

    public static String getPlace(Context context, double latitude, double longitude) {
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(context, Locale.getDefault());

            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

            return city + ", " + state + ", " + country;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity.getCurrentFocus() == null || activity.getCurrentFocus().getWindowToken() == null)
            return;

        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static void takeScreenShot(Activity activity) {

        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();

        bgBitmap = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
    }

    public static String encodeTobase64(Bitmap image) {
        if (image != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] b = baos.toByteArray();
            String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
            return imageEncoded;
        }
        return null;
    }


}
