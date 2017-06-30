package com.example.lch.mianyangmobileoffcingsystem.main.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import com.example.lch.mianyangmobileoffcingsystem.config.MyCache;
import com.netease.nim.uikit.common.activity.UI;

import java.io.File;

/**
 * Created by lch on 2017/3/17.
 */

public class OpenNewApkActivity extends UI {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        installApk();
        finish();
    }
    private void installApk() {
        String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        String filePath = directory + MyCache.getFileName();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(this, "com.example.lch.mianyangmobileoffcingsystem.fileprovider", new File(filePath));
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(intent);

    }
}
