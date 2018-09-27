package com.example.dani.mybookmasterdetail.helperClasses;

import android.content.Context;

import com.example.dani.mybookmasterdetail.R;

public enum DeviceType {

    TABLET, PHONE;

    public static boolean isTablet(Context context) {
        return TABLET == DeviceType.valueOf(context.getString(R.string.deviceType).toUpperCase());
    }

    public static boolean isPhone(Context context) {
        return PHONE == DeviceType.valueOf(context.getString(R.string.deviceType).toUpperCase());
    }
}
