package com.example.lch.mianyangmobileoffcingsystem.config;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.example.lch.mianyangmobileoffcingsystem.R;
import com.example.lch.mianyangmobileoffcingsystem.main.activity.MainActivity;
import com.example.lch.mianyangmobileoffcingsystem.main.activity.WelcomeActivity;
import com.example.lch.mianyangmobileoffcingsystem.tools.SessionHelper;
import com.example.lch.mianyangmobileoffcingsystem.utils.SystemUtil;
import com.netease.nim.uikit.ImageLoaderKit;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.cache.FriendDataCache;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.contact.ContactProvider;
import com.netease.nim.uikit.custom.DefalutUserInfoProvider;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderThumbBase;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.friend.constant.VerifyType;
import com.netease.nimlib.sdk.msg.MessageNotifierCustomization;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lch on 2017/3/10.
 */

public class BaseApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = getApplicationContext();
        MyCache.setContext(context);

        // SDK初始化（启动后台服务，若已经存在用户登录信息， SDK 将完成自动登录）
        NIMClient.init(this, loginInfo(), options());
        if (inMainProcess()) {
            // 初始化UIKit模块
            initUIKit();
        }
    }

    private void initUIKit() {
        NimUIKit.init(this, infoProvider, contactProvider);
        SessionHelper.init();
       // NimUIKit.setLocationProvider(new TLocationProvider());
    }

    private UserInfoProvider infoProvider = new UserInfoProvider() {

        @Override
        public UserInfo getUserInfo(String account) {
            UserInfo userInfo = NimUserInfoCache.getInstance().getUserInfo(account);
            if (userInfo == null) {
                NimUserInfoCache.getInstance().getUserInfoFromRemote(account,null);
            }
            return userInfo;
        }

        @Override
        public int getDefaultIconResId() {
            return R.drawable.nim_avatar_default;
        }

        @Override
        public Bitmap getAvatarForMessageNotifier(String account) {
            UserInfo userInfo = getUserInfo(account);
            if (userInfo != null && !TextUtils.isEmpty(userInfo.getAvatar())) {
                return ImageLoaderKit.getNotificationBitmapFromCache(userInfo.getAvatar());
            }
            return null;
        }

        @Override
        public String getDisplayNameForMessageNotifier(String s, String s1, SessionTypeEnum sessionTypeEnum) {
            String nick = null;
            if (sessionTypeEnum.equals(SessionTypeEnum.P2P)) {
                nick = NimUserInfoCache.getInstance().getAlias(s);//获得别名
            }else if (sessionTypeEnum.equals(SessionTypeEnum.Team)) {
                nick = TeamDataCache.getInstance().getTeamNick(s1,s);
                if (TextUtils.isEmpty(nick)) {
                    nick = NimUserInfoCache.getInstance().getAlias(s);
                }
            }
            if (TextUtils.isEmpty(nick)) {
                return null;
            }
            return nick;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public Bitmap getTeamIcon(String s) {
            Drawable drawable = getResources().getDrawable(R.drawable.nim_avatar_default,null);
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }
            return null;
        }
    };

    private ContactProvider contactProvider = new ContactProvider() {
        @Override
        public List<UserInfoProvider.UserInfo> getUserInfoOfMyFriends() {
            List<NimUserInfo> userInfoList = NimUserInfoCache.getInstance().getAllUsersOfMyFriend();
            List<UserInfoProvider.UserInfo> userInfos = new ArrayList<UserInfoProvider.UserInfo>(userInfoList.size());
            if (!userInfoList.isEmpty()) {
                userInfos.addAll(userInfoList);
            }
            return userInfos;
        }

        @Override
        public int getMyFriendsCount() {
            return FriendDataCache.getInstance().getMyFriendCounts();
        }

        @Override
        public String getUserDisplayName(String account) {
            return NimUserInfoCache.getInstance().getUserDisplayName(account);
        }
    };
    private boolean inMainProcess() {
        String pkgName = getPackageName();
        String processName = SystemUtil.getProcessName(this);
        return pkgName.equals(processName);
    }

    private SDKOptions options() {

        SDKOptions options = new SDKOptions();

        // 如果将新消息通知提醒托管给SDK完成，需要添加以下配置。

        // load 应用的状态栏配置
        StatusBarNotificationConfig config = loadStatusBarNotificationConfig();

        // load 用户的 StatusBarNotificationConfig 设置项
        //StatusBarNotificationConfig userConfig = UserPreferences.getStatusConfig();
        //if (userConfig == null) {
        //userConfig = config;
        //UserPreferences.setStatusConfig(config);
        //}
        // SDK statusBarNotificationConfig 生效
        options.statusBarNotificationConfig = config;
        // 配置保存图片，文件，log等数据的目录
        String sdkPath = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/Dika";
        options.sdkStorageRootPath = sdkPath;

        // 配置数据库加密秘钥
        options.databaseEncryptKey = "DIKA";


        // 配置是否需要预下载附件缩略图
        options.preloadAttach = true;

        // 配置附件缩略图的尺寸大小，
        options.thumbnailSize = MsgViewHolderThumbBase.getImageMaxEdge();

        // 用户信息提供者
        options.userInfoProvider = new DefalutUserInfoProvider(this);

        // 定制通知栏提醒文案（可选，如果不定制将采用SDK默认文案）
        options.messageNotifierCustomization = messageNotifierCustomization;

        // 在线多端同步未读数
        options.sessionReadAck = true;

        return options;
    }

    // 这里开发者可以自定义该应用的 StatusBarNotificationConfig
    private StatusBarNotificationConfig loadStatusBarNotificationConfig() {
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        // 点击通知需要跳转到的界面
        config.notificationEntrance = MainActivity.class;
        config.notificationSmallIconId = R.mipmap.logo;

        // 通知铃声的uri字符串
        config.notificationSound = "android.resource://com.netease.nim.demo/raw/msg";

        // 呼吸灯配置
        config.ledARGB = Color.GREEN;
        config.ledOnMs = 1000;
        config.ledOffMs = 1500;

        // save cache，留做切换账号备用
        //MyCache.setNotificationConfig(config);
        return config;
    }

    private LoginInfo loginInfo() {
        String account = Preference.getUserAccount();
        String token = Preference.getUserToken();

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            MyCache.setAccount(account.toLowerCase());
            return new LoginInfo(account, token);
        } else {
            return null;
        }
    }

    private MessageNotifierCustomization messageNotifierCustomization = new MessageNotifierCustomization() {
        @Override
        public String makeNotifyContent(String nick, IMMessage message) {
            return null; // 采用SDK默认文案
        }

        @Override
        public String makeTicker(String nick, IMMessage message) {
            return null; // 采用SDK默认文案
        }
    };

    public static Context getContext() {
        return context;
    }
}
