package com.example.lch.mianyangmobileoffcingsystem.interfaces;

/**
 * Created by lch on 2017/3/10.
 */

public interface HttpCallbackListener {
    void onSuccess(String Response);

    void onFailed(Exception e);
}
