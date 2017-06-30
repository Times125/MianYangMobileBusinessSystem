package com.example.lch.mianyangmobileoffcingsystem.config;

import android.content.Context;
import android.location.Criteria;
import android.location.LocationManager;
import android.text.TextUtils;

/**
 * Created by lch on 2017/3/11.
 * 暂时没什么用
 */

public class NimLocationManager {
    public static boolean isLocationEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria cri = new Criteria();
        cri.setAccuracy(Criteria.ACCURACY_COARSE);
        cri.setAltitudeRequired(false);
        cri.setBearingRequired(false);
        cri.setCostAllowed(false);
        String bestProvider = locationManager.getBestProvider(cri, true);
        return !TextUtils.isEmpty(bestProvider);

    }
}
