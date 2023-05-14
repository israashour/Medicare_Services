package com.medicare_service.helpers;

import android.app.Application;

import com.medicare_service.helpers.LocaleUtils;
import com.orhanobut.hawk.Hawk;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Hawk.init(this).build();
        LocaleUtils.onCreate(this, "ar");
    }

}
