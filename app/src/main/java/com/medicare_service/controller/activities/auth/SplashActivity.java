package com.medicare_service.controller.activities.auth;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;

import com.medicare_service.R;
import com.medicare_service.controller.activities.other.MainActivity;
import com.medicare_service.helpers.BaseActivity;
import com.medicare_service.helpers.Constants;
import com.medicare_service.helpers.LocaleUtils;
import com.orhanobut.hawk.Hawk;

@SuppressLint("CustomSplashScreen")

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleUtils.setLocale(this, "ar");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
    }

    private void initView() {
        new Handler().postDelayed(() -> {
            if (Hawk.get(Constants.TYPE_IS_LOGIN, false)) {
                startActivity(MainActivity.class);
            } else {
                startActivity(LoginActivity.class);
            }
            finish();
        }, 2000);
    }
}