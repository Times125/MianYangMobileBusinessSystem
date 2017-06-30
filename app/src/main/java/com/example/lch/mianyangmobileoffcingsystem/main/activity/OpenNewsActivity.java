package com.example.lch.mianyangmobileoffcingsystem.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lch.mianyangmobileoffcingsystem.R;
import com.netease.nim.uikit.common.activity.UI;

/**
 * Created by lch on 2017/3/18.
 */

public class OpenNewsActivity extends UI implements View.OnClickListener {
    //title bar
    private TextView titleText;
    private TextView titleEditText;
    private ImageView back;
    private LinearLayout titleBarLayout;
    private WebView webView;
    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_new_activity_layout);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        initViews();

    }

    private void initViews() {
        webView = (WebView) findViewById(R.id.news_webview);
        titleBarLayout = (LinearLayout) findViewById(R.id.open_news);
        titleEditText = (TextView) titleBarLayout.findViewById(R.id.edit_info_text);
        titleText = (TextView) titleBarLayout.findViewById(R.id.action_bar_title_text);
        titleText.setText("浏览网页中...");
        titleEditText.setVisibility(View.GONE);
        back = (ImageView) titleBarLayout.findViewById(R.id.failed_back);
        back.setOnClickListener(this);
        WebSettings settings = webView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(true);
        settings.setAllowContentAccess(true);
        webView.loadUrl(url);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.failed_back:
                finish();
                break;
            default:
                break;
        }
    }
}
