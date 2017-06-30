package com.example.lch.mianyangmobileoffcingsystem.config;

import android.content.Context;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;

/**
 * Created by lch on 2017/3/11.
 */

public class MyCache {
    private static Context context;

    private static String account;
    private static String fileName;

    private static StatusBarNotificationConfig config;

    public static void clear() {
        account = null;
    }

    public static String getAccount() {
        return account;
    }

    public static void setAccount(String account) {
        MyCache.account = account;
        NimUIKit.setAccount(account);
    }

    public static void setFileName(String f) {
        fileName = f;
    }
    public static String getFileName() {
        return fileName;
    }
    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        MyCache.context = context.getApplicationContext();
    }

    public static StatusBarNotificationConfig getConfig() {
        return config;
    }

    public static void setConfig(StatusBarNotificationConfig config) {
        MyCache.config = config;
    }
}