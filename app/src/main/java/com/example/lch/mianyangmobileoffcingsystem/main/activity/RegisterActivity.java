package com.example.lch.mianyangmobileoffcingsystem.main.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.lch.mianyangmobileoffcingsystem.R;
import com.example.lch.mianyangmobileoffcingsystem.config.Preference;
import com.example.lch.mianyangmobileoffcingsystem.interfaces.HttpCallbackListener;
import com.example.lch.mianyangmobileoffcingsystem.tools.Constants;
import com.example.lch.mianyangmobileoffcingsystem.tools.Validator;
import com.example.lch.mianyangmobileoffcingsystem.utils.HttpUtil;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.widget.ClearableEditTextWithIcon;
import com.netease.nim.uikit.common.util.C;
import com.netease.nim.uikit.common.util.string.MD5;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by lch on 2017/3/10.
 */

public class RegisterActivity extends UI implements View.OnClickListener {

    private ClearableEditTextWithIcon registerAccountEdit;
    private ClearableEditTextWithIcon registerPasswordEdit;
    private ClearableEditTextWithIcon registerPasswordAgainEdit;
    private ClearableEditTextWithIcon registerIdentifierCodeEdit;

    private Button send_code_btn;
    private Button register;
    private ImageView failed_back_btn;
    private String realCode;
    private int count;

    ProgressDialog progressDialog;
    private static final String TAG = "RegisterActivity";
    private static final int COUNT = 101;
    private Timer timer;
    private TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        timer = new Timer();
        initViews();
    }

    private void initViews() {
        send_code_btn = (Button) findViewById(R.id.send_identify_btn);
        send_code_btn.setOnClickListener(this);//发送验证码
        register = (Button) findViewById(R.id.register);
        register.setOnClickListener(this);//注册

        failed_back_btn = (ImageView) findViewById(R.id.register_failed_back);
        failed_back_btn.setOnClickListener(this);
        registerAccountEdit = (ClearableEditTextWithIcon) findViewById(R.id.edit_account_register);
        registerPasswordEdit = (ClearableEditTextWithIcon) findViewById(R.id.edit_password_register);
        registerPasswordAgainEdit = (ClearableEditTextWithIcon) findViewById(R.id.edit_password_register_again);
        registerIdentifierCodeEdit = (ClearableEditTextWithIcon) findViewById(R.id.edit_identify);

        registerAccountEdit.setIconResource(R.mipmap.user_account_icon);
        registerPasswordEdit.setIconResource(R.mipmap.user_pwd_lock_icon);
        registerPasswordAgainEdit.setIconResource(R.mipmap.user_pwd_lock_icon);
        registerIdentifierCodeEdit.setIconResource(R.mipmap.user_pwd_lock_icon);

        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setTitle("正在注册");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);


    }

    private boolean checkNetState() {
        if (!NetworkUtil.isNetAvailable(RegisterActivity.this)) {
            Toast.makeText(RegisterActivity.this, "网络不可用", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                doCheck();
                break;
            case R.id.register_failed_back:
                finish();
                break;
            case R.id.send_identify_btn:
                String accid = registerAccountEdit.getText().toString();
                if (NetworkUtil.isNetAvailable(RegisterActivity.this)) {
                    if (!TextUtils.isEmpty(accid)) {
                        if (Validator.isEmail(accid)) {
                            count = 60;
                            send_code_btn.setEnabled(false);
                            send_code_btn.setText("60秒");
                            sendCode(accid);
                        } else {
                            Toast.makeText(RegisterActivity.this, "账号非法，请输入正确邮箱", Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        Toast.makeText(RegisterActivity.this, "请输入邮箱", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "网络不可用，请检查网络设置", Toast.LENGTH_SHORT).show();
                }

                break;
        }

    }

    private void sendCode(final String accid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("accid", accid)
                            .build();
                    Request request = new Request.Builder()
                            .url(Constants.CAPTCHA_URL)
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                } catch (Exception e) {

                }
            }
        }).start();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                count--;
                Message msg = new Message();
                msg.what = COUNT;
                msg.arg1 = count;
                handler.sendMessage(msg);
            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }

    private void doCheck() {
        String account = registerAccountEdit.getText().toString();
        String password = registerPasswordEdit.getText().toString();
        String passwordA = registerPasswordAgainEdit.getText().toString();
        String code = registerIdentifierCodeEdit.getText().toString();
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)
                && !TextUtils.isEmpty(passwordA) && !TextUtils.isEmpty(code)) {//输入都不为空
            if (Validator.isEmail(account)) {
                if (Validator.isPassword(password)) {
                    if (password.equals(passwordA)) {
                        doRegister(account, password, code);
                    } else {
                        Toast.makeText(this, "两次输入密码不一致，请检查", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "密码含有非法字符，请检查", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "账号不是邮箱，请检查", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "输入不能为空，请检查", Toast.LENGTH_SHORT).show();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String data = (String) msg.obj;
            //这里解析服务器返回的数据
            //然后做一些接下来应该做的操作
            switch (msg.what) {
                case COUNT:
                    send_code_btn.setText(String.valueOf(msg.arg1) + "秒");
                    if (msg.arg1 < 1) {
                        send_code_btn.setEnabled(true);
                        send_code_btn.setText("重新发送验证码");
                    }
                    break;
                case Constants.REGISTER_SUCCESS_RETURN_CODE:
                    progressDialog.cancel();
                    parseJSONWithJSONObject(data);
                    break;
                case Constants.REGISTER_FAILED_TIMEOUT_CODE:
                    progressDialog.cancel();
                    Toast.makeText(RegisterActivity.this, "注册失败,网络故障", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    private void parseJSONWithJSONObject(String data) {
        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int rCode = jsonObject.getInt("code");
                if (rCode == Constants.REGISTER_FAILED_WRONG_IDENTIFIER_CODE) {
                    Toast.makeText(RegisterActivity.this, "注册失败,验证码错误", Toast.LENGTH_SHORT).show();
                } else if (rCode == Constants.REGISTER_SUCCESS_RETURN_CODE) {
                    Toast.makeText(RegisterActivity.this, "注册成功,可以登录啦", Toast.LENGTH_SHORT).show();
                } else if (rCode == Constants.REGISTER_FAILED_ACCPUNT_CONFLICT_CODE) {
                    Toast.makeText(RegisterActivity.this, "该账号已经存在", Toast.LENGTH_SHORT).show();
                } else if (rCode == Constants.REGISTER_FAILED_EMAIL_WRONG_CODE) {
                    Toast.makeText(RegisterActivity.this, "服务器故障，请稍后再试", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //注册
    private void doRegister(final String account, final String password, final String code) {
        String[] values = new String[]{account, password, code};
        final String token = MD5.getStringMD5(password);
        progressDialog.show();
        if (!checkNetState()) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("accid", account)
                            .add("token", token)
                            .add("captcha", code)
                            .build();
                    Request request = new Request.Builder()
                            .url(Constants.REGISTER_URL)
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Message message = new Message();
                    message.obj = responseData;
                    message.what = Constants.REGISTER_SUCCESS_RETURN_CODE;
                    handler.sendMessage(message);

                } catch (IOException e) {
                    Message message = new Message();
                    message.what = Constants.REGISTER_FAILED_TIMEOUT_CODE;
                    handler.sendMessage(message);
                    progressDialog.cancel();
                    Log.e(TAG, "run: register failed" + e);
                    e.printStackTrace();
                }
            }
        }).start();

//        HttpUtil.sendHttpClientRequest(values,Constants.REGISTER_URL, new HttpCallbackListener() {
//            @Override
//            public void onSuccess(String Response) {
//
//            }
//
//            @Override
//            public void onFailed(Exception e) {
//                e.printStackTrace();
//            }
//        });
    }
}
