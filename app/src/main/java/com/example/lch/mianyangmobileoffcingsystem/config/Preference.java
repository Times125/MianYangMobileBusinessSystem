package com.example.lch.mianyangmobileoffcingsystem.config;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lch on 2017/3/9.
 * 用于管理保存在本地的用户账号和密码token信息
 */

public class Preference {
    public static final String KEY_USER_ACCOUNT = "account";
    public static final String KEY_USER_TOKEN = "token";


    public static void saveUserAccount(String account) {
        saveString(KEY_USER_ACCOUNT, account);
    }

    public static String getUserAccount() {
        return getString(KEY_USER_ACCOUNT);
    }

    public static void saveUserToken(String token) {
        saveString(KEY_USER_TOKEN, token);
    }

    public static String getUserToken() {
        return getString(KEY_USER_TOKEN);
    }

    private static void saveString(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putString(key, value);
        editor.apply();
    }
    static SharedPreferences getSharedPreference() {
        return BaseApplication.getContext().getSharedPreferences("UserInfotable", Context.MODE_PRIVATE);
    }

    private static String getString(String key) {
        return getSharedPreference().getString(key, null);
    }

}
