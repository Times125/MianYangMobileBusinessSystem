package com.example.lch.mianyangmobileoffcingsystem.main.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lch.mianyangmobileoffcingsystem.R;
import com.example.lch.mianyangmobileoffcingsystem.config.MyCache;
import com.example.lch.mianyangmobileoffcingsystem.config.Preference;
import com.example.lch.mianyangmobileoffcingsystem.tools.Constants;
import com.netease.nim.uikit.cache.DataCacheManager;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.widget.ClearableEditTextWithIcon;
import com.netease.nim.uikit.common.util.string.MD5;
import com.netease.nim.uikit.permission.MPermission;
import com.netease.nim.uikit.permission.annotation.OnMPermissionDenied;
import com.netease.nim.uikit.permission.annotation.OnMPermissionGranted;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by lch on 2017/3/9.
 */

public class LoginActivity extends UI implements View.OnClickListener {

    private ClearableEditTextWithIcon loginAccountEdit;
    private ClearableEditTextWithIcon loginPasswordEdit;
    private final int BASIC_PERMISSION_REQUEST_CODE = 110;
    private TextView register;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        requestBasicPermission();
        initViews();
    }

    private void initViews() {
        findViewById(R.id.register).setOnClickListener(this);
        findViewById(R.id.login).setOnClickListener(this);
        loginAccountEdit = (ClearableEditTextWithIcon) findViewById(R.id.edit_account_login);
        loginPasswordEdit = (ClearableEditTextWithIcon) findViewById(R.id.edit_password_login);
        loginAccountEdit.setIconResource(R.mipmap.user_account_icon);
        loginPasswordEdit.setIconResource(R.mipmap.user_pwd_lock_icon);
        loginPasswordEdit.setText(Preference.getUserToken());
        loginAccountEdit.setText(Preference.getUserAccount());
    }

    public static void actionStart(Context context, String value) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("param", value);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                RegisterActivity.actionStart(LoginActivity.this);
                break;
            case R.id.login:
                //SignActivity.actionStart(LoginActivity.this,null);
                checkInfo();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @OnMPermissionGranted(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionSuccess() {
        Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
    }

    @OnMPermissionDenied(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionFailed() {
        Toast.makeText(this, "授权失败，请在设置中给予应用权限", Toast.LENGTH_SHORT).show();
    }

    private void requestBasicPermission() {
        MPermission.with(LoginActivity.this)
                .addRequestCode(BASIC_PERMISSION_REQUEST_CODE)
                .permissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE).request();
    }

    private void checkInfo() {
        String account = loginAccountEdit.getText().toString();
        String password = loginPasswordEdit.getText().toString();

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {
            String token = MD5.getStringMD5(password);
            doLogin(account, token);
        } else {
            Toast.makeText(LoginActivity.this, "输入不能为空，请检查", Toast.LENGTH_SHORT).show();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.LOGIN_SUCCESS_CODE:
                    String data = msg.getData().getString("responseData");
                    //Log.d(TAG, "handleMessage: "+ " ---" + data);
                    String account = msg.getData().getString("accid");
                    String token = msg.getData().getString("token");
                    parseJSONWithJSONObject(data, account, token);
                    break;
                case Constants.LOGIN_TIMEOUT_CODE:
                    Toast.makeText(LoginActivity.this, "网络超时，请检查网络连接", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void parseJSONWithJSONObject(String data, String account, String token) {
        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int rCode = jsonObject.getInt("code");
                if (rCode == Constants.LOGIN_SUCCESS_CODE) {
                    logIMserver(account, token);
                } else if (rCode == Constants.LOGIN_FAILED_CODE) {
                    Toast.makeText(LoginActivity.this, "不存在此账号，请注册", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void logIMserver(final String account, final String token) {
        LoginInfo loginfo = new LoginInfo(account, token);
        RequestCallback<LoginInfo> callback = new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo loginInfo) {
                MyCache.setAccount(account);
                //构建缓存
                DataCacheManager.buildDataCacheAsync();
                Preference.saveUserAccount(account);
                Preference.saveUserToken(token);
                MainActivity.actionStart(LoginActivity.this, null);
                finish();
            }

            @Override
            public void onFailed(int i) {

            }

            @Override
            public void onException(Throwable throwable) {

            }
        };
        NIMClient.getService(AuthService.class).login(loginfo).setCallback(callback);

    }

    private void doLogin(final String account, final String token) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("accid", account)
                            .add("token", token)
                            .build();
                    Request request = new Request.Builder()
                            .url(Constants.LOGIN_URL)
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Bundle bundle = new Bundle();
                    bundle.putString("accid", account);
                    bundle.putString("token", token);
                    bundle.putString("responseData", responseData);
                    Message message = new Message();
                    message.what = Constants.LOGIN_SUCCESS_CODE;
                    message.setData(bundle);
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message message = new Message();
                    message.what = Constants.LOGIN_TIMEOUT_CODE;
                    handler.sendMessage(message);
                }

            }
        }).start();

    }
}
