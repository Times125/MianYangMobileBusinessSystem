package com.example.lch.mianyangmobileoffcingsystem.about;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lch.mianyangmobileoffcingsystem.R;
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
 * Created by lch on 2017/3/17.
 */

public class FeedbackActivity extends UI implements View.OnClickListener {
    //title bar
    private TextView titleText;
    private TextView titleEditText;
    private ImageView back;
    private LinearLayout titleBarLayout;
    private EditText email, content;
    private Button commit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_layout);
        initViews();
    }

    private void initEvents() {
        final String em = email.getText().toString();
        final String fb = content.getText().toString();
        if (!TextUtils.isEmpty(em) && !TextUtils.isEmpty(fb)) {
            if (Validator.isEmail(em)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client = new OkHttpClient();
                        RequestBody body = new FormBody.Builder()
                                .add("email",em)
                                .add("content",fb)
                                .build();
                        Request request = new Request.Builder()
                                .url(Constants.FEEDBACK_URL)
                                .post(body)
                                .build();
                        try {
                            Response response = client.newCall(request).execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
                Toast.makeText(FeedbackActivity.this,"提交成功，感谢亲的反馈哟～～",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,"亲，你填了个假邮箱～～",Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this,"亲，至少写点什么吧～～",Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        email = (EditText) findViewById(R.id.contact_email);
        content = (EditText) findViewById(R.id.feedback_info);
        titleBarLayout = (LinearLayout) findViewById(R.id.feedback);
        titleEditText = (TextView) titleBarLayout.findViewById(R.id.edit_info_text);
        titleText = (TextView) titleBarLayout.findViewById(R.id.action_bar_title_text);
        titleText.setText("问题反馈");
        titleEditText.setVisibility(View.GONE);
        back = (ImageView) titleBarLayout.findViewById(R.id.failed_back);
        back.setOnClickListener(this);
        commit = (Button) findViewById(R.id.commit_feedback);
        commit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.failed_back:
                finish();
                break;
            case R.id.commit_feedback:
                initEvents();
                break;

            default:
                break;
        }
    }
}
