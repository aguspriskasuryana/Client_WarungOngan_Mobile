package com.example.gan.mywoa;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by GUSWIK on 6/27/2018.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseInsIDService";

    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token= FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Token : " + token);
        Toast.makeText(MyFirebaseInstanceIDService.this, token, Toast.LENGTH_SHORT).show();
    }
}
