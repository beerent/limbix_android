package com.krux.util;

import android.content.Intent;

import com.krux.activity.main.HomeActivity;
import com.krux.activity.preLogin.LoginActivity;
import com.krux.activity.preLogin.RegisterActivity;

/**
 * Created by brent on 8/23/16.
 */
public class IntentManager {

    public Intent loginToRegisterIntent(LoginActivity la){
        return new Intent(la, RegisterActivity.class);
    }

    public Intent registerToLoginIntent(RegisterActivity ra) {
        return new Intent(ra, LoginActivity.class);
    }

    public Intent loginToHomeIntent(LoginActivity la) {
        return new Intent(la, HomeActivity.class);
    }
}