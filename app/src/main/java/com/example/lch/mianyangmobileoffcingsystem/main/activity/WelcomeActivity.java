package com.example.lch.mianyangmobileoffcingsystem.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.lch.mianyangmobileoffcingsystem.R;
import com.example.lch.mianyangmobileoffcingsystem.config.MyCache;
import com.example.lch.mianyangmobileoffcingsystem.config.Preference;
import com.netease.nim.uikit.cache.DataCacheManager;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;

/**
 * Created by lch on 2017/3/9.
 */

public class WelcomeActivity extends UI {
    private static final String TAG = "WelcomeActivity";
    private static boolean isFirstEnter = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        handler = new Handler();
        showBackgroundViews();
    }

    private void showBackgroundViews() {
        getWindow().setBackgroundDrawableResource(R.drawable.welcome);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == -1) {
                Toast.makeText(WelcomeActivity.this," 无网络，自动登录失败",Toast.LENGTH_SHORT).show();
                LoginActivity.actionStart(WelcomeActivity.this," ");
                handler.removeCallbacks(runnable);
            }
        }
    };
    private void logIMserver(final String account, final String token) {
        LoginInfo loginfo = new LoginInfo(account, token);
        RequestCallback<LoginInfo> callback = new RequestCallback<LoginInfo>() {
            Message msg = new Message();
            @Override
            public void onSuccess(LoginInfo loginInfo) {
                MyCache.setAccount(account);
                //构建缓存
                DataCacheManager.buildDataCacheAsync();
                Preference.saveUserAccount(account);
                Preference.saveUserToken(token);
                MainActivity.actionStart(WelcomeActivity.this, null);
                Log.d(TAG, "onSuccess: " + token);
                finish();
            }

            @Override
            public void onFailed(int i) {
                Log.e(TAG, "onException: "+ "failed" );
                msg.what = -1;
                handler.sendMessage(msg);
            }

            @Override
            public void onException(Throwable throwable) {
                msg.what = -1;
                handler.sendMessage(msg);
                Log.e(TAG, "onException: "+ "onException" );
            }
        };
        NIMClient.getService(AuthService.class).login(loginfo).setCallback(callback);
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            final String account = Preference.getUserAccount();
            final String token = Preference.getUserToken();
            if (canAutoLogin()) {
                logIMserver(account, token);
            }else {
                LoginActivity.actionStart(WelcomeActivity.this,null);
            }
            handler.removeCallbacks(runnable);
        }
    };

    private boolean canAutoLogin() {
        //检查是否本地有账号密码记录，有则返回true
        String account = Preference.getUserAccount();
        String token = Preference.getUserToken();
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(token)) {
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 3000);
    }

    //处理通知栏消息，暂时还没做
    private void parseNotifyIntent(Intent intent) {
        ArrayList<IMMessage> messages = (ArrayList<IMMessage>) intent.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
        if (messages == null || messages.size() > 1) {

        } else {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
