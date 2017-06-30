package com.example.lch.mianyangmobileoffcingsystem.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.example.lch.mianyangmobileoffcingsystem.R;
import com.example.lch.mianyangmobileoffcingsystem.config.BaseApplication;
import com.example.lch.mianyangmobileoffcingsystem.config.MyCache;
import com.example.lch.mianyangmobileoffcingsystem.interfaces.DownloadListener;
import com.example.lch.mianyangmobileoffcingsystem.main.activity.MainActivity;
import com.example.lch.mianyangmobileoffcingsystem.main.activity.OpenNewApkActivity;
import com.example.lch.mianyangmobileoffcingsystem.tools.DownloadTask;

import java.io.File;

/**
 * Created by lch on 2017/3/16.
 */

public class DownloadService extends Service {

    private DownloadTask downloadTask;
    private String downloadUrl;
    public DownloadListener downloadListener = new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            getNotificationManager().notify(1, getNotification("正在下载...", progress));
        }

        @Override
        public void onSuccess() {
            downloadTask = null;
            stopForeground(true);//让service 后台运行
            getNotificationManager().notify(1, getNotification("下载更新成功", -1));
            Toast.makeText(DownloadService.this, "下载更新成功", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onFailed() {
            downloadTask = null;
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("下载更新失败", -1));
            Toast.makeText(DownloadService.this, "下载更新失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused() {
            downloadTask = null;
            Toast.makeText(DownloadService.this, "暂停下载", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCanceled() {
            downloadTask = null;
            stopForeground(true);
            Toast.makeText(DownloadService.this, "取消更新", Toast.LENGTH_SHORT).show();
        }
    };

    private Notification getNotification(String s, int progress) {
        Intent intent = new Intent(this, OpenNewApkActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.logo);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.logob));
        builder.setContentIntent(pi);
        builder.setContentTitle(s);
        if (progress > 0) {
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }
        return builder.build();
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private DownloadBinder binder = new DownloadBinder();

    public class DownloadBinder extends Binder {
        public void startDownload(String url) {
            if (downloadTask == null) {
                downloadUrl = url;
                downloadTask = new DownloadTask(downloadListener);
                downloadTask.execute(downloadUrl);
                startForeground(1, getNotification("下载更新中...", 0));
                Toast.makeText(DownloadService.this, "下载更新中...", Toast.LENGTH_SHORT).show();

            }
        }

        public void pauseDownload() {
            if (downloadTask != null) {
                downloadTask.PausedDownload();
            }
        }

        public void cancelDownload() {
            if (downloadTask != null) {
                downloadTask.CancelDownload();
            } else {
                if (downloadUrl != null) {
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(directory + fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                    getNotificationManager().cancel(1);
                    stopForeground(true);
                    Toast.makeText(DownloadService.this, "取消下载", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
