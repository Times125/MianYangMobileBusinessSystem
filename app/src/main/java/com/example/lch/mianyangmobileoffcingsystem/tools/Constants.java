package com.example.lch.mianyangmobileoffcingsystem.tools;

import com.example.lch.mianyangmobileoffcingsystem.R;

/**
 * Created by lch on 2017/3/10.
 */

public class Constants {
    //颜色资源
    public static  final int[] colors = {R.color.red, R.color.orange, R.color.yellow, R.color.green,
            R.color.blue, R.color.purple, R.color.deepbluesky};
    //默认群头像url
    public static final String TEAM_HEAD_VIEW_URL = "http://120.27.118.159/nim_avatar_default.jpg";
    //保存任务
    public static final String SAVE_TASK_URL = "http://120.27.118.159:5000/log";
    //获取新闻
    public static final String GET_NEWS_URL = "http://120.27.118.159:5000/getnewsinfo";
    //反馈地址
    public static final String FEEDBACK_URL = "http://120.27.118.159:5000/feedback";
    //检查更新地址
    public static final String CHECK_UPDATE_URL = "http://120.27.118.159:5000/checkUpdate";
    //注册地址
    public static final String REGISTER_URL = "http://120.27.118.159:5000/register";
    public static final String CAPTCHA_URL = "http://120.27.118.159:5000/captcha";
    public static final String LOGIN_URL = "http://120.27.118.159:5000/login";
    //注册返回的code码
    public static final int REGISTER_SUCCESS_RETURN_CODE = 500;//注册成功
    public static final int REGISTER_FAILED_WRONG_IDENTIFIER_CODE = 501;//验证码错误
    public static final int REGISTER_FAILED_TIMEOUT_CODE = 502;
    public static final int REGISTER_FAILED_ACCPUNT_CONFLICT_CODE = 503;//有相同账号，账号冲突
    public static final  int REGISTER_FAILED_EMAIL_WRONG_CODE = 507;//邮件发送错误
    //登录返回code码
    public static final int LOGIN_FAILED_CODE = 504;
    public static final int LOGIN_SUCCESS_CODE = 505;
    public static final int LOGIN_TIMEOUT_CODE = 506;
    //检查更新校验码
    public static final int CAN_UPDATE = 510;
    public static final int CAN_NOT_UPDATE = 511;
    //请求公告返回码


}
