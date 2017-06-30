package com.example.lch.mianyangmobileoffcingsystem.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lch.mianyangmobileoffcingsystem.R;
import com.example.lch.mianyangmobileoffcingsystem.about.FeedbackActivity;
import com.example.lch.mianyangmobileoffcingsystem.config.MyCache;
import com.example.lch.mianyangmobileoffcingsystem.tools.Constants;
import com.example.lch.mianyangmobileoffcingsystem.tools.Validator;
import com.netease.nim.uikit.common.activity.UI;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by lch on 2017/3/19.
 */

public class AddTodayTaskActivity extends UI implements View.OnClickListener {
    //title bar
    private TextView titleText;
    private TextView titleEditText;
    private ImageView back;
    private LinearLayout titleBarLayout;
    private EditText content;
    private Button commit;
    private String data;
    private String time;
    private static final String TAG = "AddTodayTaskActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_today_task_layout);
        Bundle bundle = getIntent().getExtras();
        time = bundle.getString("date");
        data = bundle.getString("content");
        initViews();
    }

    private void initEvents() {
        final String accid = MyCache.getAccount();
        final String finalData = content.getText().toString();//最终编辑得到的数据
        if (!TextUtils.isEmpty(finalData)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = new Message();
                    OkHttpClient client = new OkHttpClient();
                    RequestBody body = new FormBody.Builder()
                            .add("accid", accid)
                            .add("date", time)
                            .add("log", finalData)
                            .build();
                    Request request = new Request.Builder()
                            .url(Constants.SAVE_TASK_URL)
                            .post(body)
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                        String responseData = response.body().string();
                        message.obj = responseData;
                        message.what = 1;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        if (client != null) {
                            client = null;
                        }
                        handler.sendMessage(message);

                    }

                }
            }).start();
            Toast.makeText(AddTodayTaskActivity.this, "任务设置成功", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "亲，至少写点什么吧～～", Toast.LENGTH_SHORT).show();
        }
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String str = (String) (msg.obj);
                    Log.e(TAG, "handleMessage: "+  str);
                    if (str.equals("update")) {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("returnData",content.getText().toString());
                        intent.putExtras(bundle);
                        setResult(200,intent);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private void initViews() {
        content = (EditText) findViewById(R.id.task_info);
        content.append(data);
        titleBarLayout = (LinearLayout) findViewById(R.id.add_task_bar);
        titleEditText = (TextView) titleBarLayout.findViewById(R.id.edit_info_text);
        titleText = (TextView) titleBarLayout.findViewById(R.id.action_bar_title_text);
        titleText.setText("编辑任务");
        titleEditText.setVisibility(View.GONE);
        back = (ImageView) titleBarLayout.findViewById(R.id.failed_back);
        back.setOnClickListener(this);
        commit = (Button) findViewById(R.id.save_task);
        commit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.failed_back:
                finish();
                break;
            case R.id.save_task:
                initEvents();
                break;
            default:
                break;
        }
    }
}
