package com.example.lch.mianyangmobileoffcingsystem.main.Fragement;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lch.mianyangmobileoffcingsystem.R;
import com.example.lch.mianyangmobileoffcingsystem.config.Preference;
import com.example.lch.mianyangmobileoffcingsystem.helper.LogoutHelper;
import com.example.lch.mianyangmobileoffcingsystem.main.activity.LoginActivity;
import com.example.lch.mianyangmobileoffcingsystem.reminder.ReminderManager;
import com.example.lch.mianyangmobileoffcingsystem.tools.SessionHelper;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.recent.RecentContactsCallback;
import com.netease.nim.uikit.recent.RecentContactsFragment;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.attachment.AudioAttachment;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment;
import com.netease.nimlib.sdk.msg.attachment.LocationAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.VideoAttachment;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import static com.netease.nimlib.sdk.media.player.AudioPlayer.TAG;

/**
 * Created by lch on 2017/3/7.
 */

public class MessageFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;
    private View notifyBar;
    private TextView notifyBarText;
    private RecentContactsFragment fragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.message_fragment_layout, container, false);
        //swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.message_layout_refresh);
        findViews();
        initData();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViews();
        //initEvent();
        initData();
    }

    @Override
    public void onStart() {
        super.onStart();
        //initEvent();
        initData();
    }

    private void findViews() {
        notifyBar = view.findViewById(R.id.status_notify_bar);
        notifyBarText = (TextView) view.findViewById(R.id.status_desc_label);
        notifyBar.setVisibility(View.GONE);
    }

    private void initData() {
        registerObservers(true);
        addRecentContactsFragment();
    }

    private void registerObservers(boolean register) {
        //NIMClient.getService(AuthServiceObserver.class).observeOtherClients(clientsObserver, register);//监听用户在线客户端类型
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(userStatusObserver, register);// 监听用户状态
    }

    /**
     * 用户状态变化
     */
    Observer<StatusCode> userStatusObserver = new Observer<StatusCode>() {

        @Override
        public void onEvent(StatusCode code) {
            if (code.wontAutoLogin()) {
                kickOut(code);
            } else {
                if (code == StatusCode.NET_BROKEN) {
                    notifyBar.setVisibility(View.VISIBLE);
                    notifyBarText.setText(R.string.net_broken);
                } else if (code == StatusCode.UNLOGIN) {
                    notifyBar.setVisibility(View.VISIBLE);
                    notifyBarText.setText(R.string.nim_status_unlogin);
                } else if (code == StatusCode.CONNECTING) {
                    notifyBar.setVisibility(View.VISIBLE);
                    notifyBarText.setText(R.string.nim_status_connecting);
                } else if (code == StatusCode.LOGINING) {
                    notifyBar.setVisibility(View.VISIBLE);
                    notifyBarText.setText(R.string.nim_status_logining);
                } else {
                    notifyBar.setVisibility(View.GONE);
                }
            }
        }
    };


    private void addRecentContactsFragment() {
        fragment = new RecentContactsFragment();
        fragment.setContainerId(R.id.messages_fragment);
        UI activity = (UI) getActivity();
        fragment = (RecentContactsFragment) activity.addFragment(fragment);
        fragment.setCallback(new RecentContactsCallback() {
            @Override
            public void onRecentContactsLoaded() {
                Log.d("onRecentContactsLoaded: ", String.valueOf(11111111));
                // 最近联系人列表加载完毕
            }

            @Override
            public void onUnreadCountChange(int unreadCount) {
                ReminderManager.getInstance().updateSessionUnreadNum(unreadCount);
            }

            @Override
            public void onItemClick(RecentContact recent) {
                switch (recent.getSessionType()) {
                    case P2P:
                        SessionHelper.startP2PSession(getActivity(), recent.getContactId());
                        //NimUIKit.startP2PSession(getActivity(), recent.getContactId());
                        break;
                    case Team:
                        SessionHelper.startTeamSession(getActivity(), recent.getContactId());
                        //NimUIKit.startTeamSession(getActivity(), recent.getContactId());
                        break;
                }
            }

            // 设置自定义消息的摘要消息，展示在最近联系人列表的消息缩略栏上
            @Override
            public String getDigestOfAttachment(MsgAttachment attachment) {
                //主要有几种类别需要自己实现，比如语言消息，视频消息等
                if (attachment instanceof FileAttachment) {
                    return "[文件]";
                }else if (attachment instanceof ImageAttachment) {
                    return "[图片]";
                }else if (attachment instanceof AudioAttachment) {
                    return "[音频]";
                }else if (attachment instanceof VideoAttachment) {
                    return "[视频]";
                }else if (attachment instanceof LocationAttachment) {
                    return "[位置]";
                }
                return null;
            }

            //设置Tip消息的摘要信息，展示在最近联系人列表的消息缩略栏上
            @Override
            public String getDigestOfTipMsg(RecentContact recent) {
                String msgId = recent.getRecentMessageId();
                List<String> uuids = new ArrayList<String>(1);
                uuids.add(msgId);
                List<IMMessage> immsg = NIMClient.getService(MsgService.class).queryMessageListByUuidBlock(uuids);
                if (immsg != null && !immsg.isEmpty()) {
                    IMMessage IMSG = immsg.get(0);
                    Map<String, Object> content = IMSG.getRemoteExtension();
                    if (!content.isEmpty() && content != null) {
                        return (String) content.get("content");
                    }
                }
                return null;
            }
        });

    }

    private void initEvent() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                addRecentContactsFragment();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //执行刷新操作，donging....
                        int mat = (int) (Math.random() * 100 + 1);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 5000);
            }
        });
    }

    private void kickOut(StatusCode code) {
        Preference.saveUserToken("");

        if (code == StatusCode.PWD_ERROR) {
            LogUtil.e("Auth", "user password error");
            Toast.makeText(getActivity(), R.string.login_failed, Toast.LENGTH_SHORT).show();
        } else {
            LogUtil.i("Auth", "Kicked!");
        }
        onLogout();
    }

    private void onLogout() {
        // 清理缓存&注销监听&清除状态
        LogoutHelper.logout();

        LoginActivity.actionStart(getActivity(), null);
        getActivity().finish();
    }
}
