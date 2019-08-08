package com.huriyo.Utility;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    public void onTokenRefresh() {
        super.onTokenRefresh();
        Utils.setPreference(getApplicationContext(), Constants.MM_noti_token, FirebaseInstanceId.getInstance().getToken());
    }
}
