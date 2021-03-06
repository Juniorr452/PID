package com.mobile.pid.pid.login;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.mobile.pid.pid.home.HomeActivity;

/**
 * Created by junio on 27/02/2018.
 */

public class LoginAuthStateListener implements FirebaseAuth.AuthStateListener
{
    private static final String TAG = "LoginAuthStateListener";

    private Activity a;

    public LoginAuthStateListener(Activity a) {
        this.a = a;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
    {
        if (firebaseAuth.getCurrentUser() != null)
        {
            FirebaseAuth.getInstance().removeAuthStateListener(this);

            a.startActivity(new Intent(a, HomeActivity.class));
            a.finish();
        }
    }
}
