package com.example.lch.mianyangmobileoffcingsystem.helper;

/**
 * Created by lch on 2017/3/15.
 */

public class SystemMessageUnreadManager {

    private static SystemMessageUnreadManager instance = new SystemMessageUnreadManager();

    public static SystemMessageUnreadManager getInstance() {
        return instance;
    }

    private int sysMsgUnreadCount = 0;

    public int getSysMsgUnreadCount() {
        return sysMsgUnreadCount;
    }

    public synchronized void setSysMsgUnreadCount(int unreadCount) {
        this.sysMsgUnreadCount = unreadCount;
    }
}
