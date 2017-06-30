package com.example.lch.mianyangmobileoffcingsystem.about;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lch.mianyangmobileoffcingsystem.R;
import com.netease.nim.uikit.common.activity.UI;

/**
 * Created by lch on 2017/3/17.
 */

public class AboutActivity extends UI implements View.OnClickListener {
    //title bar
    private TextView titleText;
    private TextView titleEditText;
    private ImageView back;
    private LinearLayout titleBarLayout;
    private TextView git_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);
        String version = "1.0";
        version = getVersion();
        initViews(version);

    }

    private String getVersion() {
        String version;
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            version = packageInfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1.0";
    }
    private void initViews(String version) {
        titleBarLayout = (LinearLayout) findViewById(R.id.about);
        titleEditText = (TextView) titleBarLayout.findViewById(R.id.edit_info_text);
        titleText = (TextView) titleBarLayout.findViewById(R.id.action_bar_title_text);
        titleText.setText("关于");
        titleEditText.setVisibility(View.GONE);
        back = (ImageView) titleBarLayout.findViewById(R.id.failed_back);
        back.setOnClickListener(this);
        git_version = (TextView) findViewById(R.id.version_detail_git);
        git_version.setText("嘀卡 v" + version);

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
