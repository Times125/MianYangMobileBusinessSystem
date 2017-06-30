package com.example.lch.mianyangmobileoffcingsystem.interfaces;

/**
 * Created by lch on 2017/3/16.
 */

public interface DownloadListener {

    void onProgress(int progress);

    void onSuccess();

    void onFailed();

    void onPaused();

    void onCanceled();
}
