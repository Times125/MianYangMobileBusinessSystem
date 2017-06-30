package com.example.lch.mianyangmobileoffcingsystem.helper;

import com.example.lch.mianyangmobileoffcingsystem.config.MyCache;
import com.netease.nim.uikit.LoginSyncDataStatusObserver;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.common.ui.drop.DropManager;

/**
 * Created by lch on 2017/3/13.
 */

public class LogoutHelper {
    public static void logout() {
        // 清理缓存&注销监听&清除状态
        NimUIKit.clearCache();
        //ChatRoomHelper.logout();
        MyCache.clear();
        LoginSyncDataStatusObserver.getInstance().reset();
        DropManager.getInstance().destroy();
    }
}