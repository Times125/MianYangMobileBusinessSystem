package com.example.lch.mianyangmobileoffcingsystem.main.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lch.mianyangmobileoffcingsystem.config.BaseApplication;
import com.example.lch.mianyangmobileoffcingsystem.config.MyCache;
import com.example.lch.mianyangmobileoffcingsystem.config.Preference;
import com.example.lch.mianyangmobileoffcingsystem.contact.AddFriendActivity;
import com.example.lch.mianyangmobileoffcingsystem.contact.TeamCreateHelper;
import com.example.lch.mianyangmobileoffcingsystem.main.Fragement.ContactFragment;
import com.example.lch.mianyangmobileoffcingsystem.main.Fragement.DingFragment;
import com.example.lch.mianyangmobileoffcingsystem.main.Fragement.MessageFragment;
import com.example.lch.mianyangmobileoffcingsystem.main.Fragement.MineFragment;
import com.example.lch.mianyangmobileoffcingsystem.main.Fragement.MyFragmentPagerAdapter;
import com.example.lch.mianyangmobileoffcingsystem.main.Fragement.WorkFragment;
import com.example.lch.mianyangmobileoffcingsystem.R;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.contact_selector.activity.ContactSelectActivity;
import com.netease.nim.uikit.team.helper.TeamHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.lch.mianyangmobileoffcingsystem.helper.LogoutHelper.logout;

public class MainActivity extends UI implements View.OnClickListener {
    private ViewPager viewPager;
    private View top_title_message_view, top_title_ding_view, top_title_work_view, top_title_contact_view, top_title_mine_view;
    private LinearLayout ll_message, ll_ding, ll_work, ll_contact, ll_mine, ll_top_title;
    private ImageView iv_message, iv_ding, iv_work, iv_contact, iv_mine, iv_current;
    private TextView tv_message, tv_ding, tv_work, tv_contact, tv_mine, tv_current;
    private ImageView addFriend;
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private static final int MESSAGE = 0;
    private static final int DING = 1;
    private static final int WORK = 2;
    private static final int CONTACT = 3;
    private static final int MINE = 4;
    private Animation animation;
    private static final String TAG = "SignActivity";
    private static final int REQUEST_CODE_NORMAL = 1;//创建讨论组
    private static final int REQUEST_CODE_ADVANCED = 2;//创建群
    //弹出窗
    private PopupWindow popupWindow,logoutWindow;
    private View contentView,logoutView;
    //back 2次返回
    private static final long TWICE_CLICK_NTERVAL = 1000;
    private long firstClick = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        animation = AnimationUtils.loadAnimation(this, R.anim.anim_alpha_tab_bottom);
        initViews();
        initEvent();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if (popupWindow == null || (popupWindow!=null && !popupWindow.isShowing())) {
            if ((System.currentTimeMillis() - firstClick) > TWICE_CLICK_NTERVAL) {
                firstClick = System.currentTimeMillis();
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            } else {
                finish();
                System.exit(0);
            }
        }else if (popupWindow!=null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    private void showPopupWindow() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.pop_layout, null);
        popupWindow = new PopupWindow(contentView,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        ColorDrawable color = new ColorDrawable(0xbFFFFFF);
        popupWindow.setBackgroundDrawable(color);
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        if (popupWindow.isShowing()) {
            return;
        } else {
            popupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
        }
        contentView.findViewById(R.id.create_group).setOnClickListener(this);
        contentView.findViewById(R.id.create_team).setOnClickListener(this);


    }
    private void popLogoutWindow() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        logoutView = inflater.inflate(R.layout.mine_pop_layout, null);
        logoutWindow = new PopupWindow(logoutView,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        logoutWindow.setTouchable(true);
        logoutWindow.setOutsideTouchable(true);
        ColorDrawable color = new ColorDrawable(0xbFFFFFF);
        logoutWindow.setBackgroundDrawable(color);
        logoutWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        if (logoutWindow.isShowing()) {
            return;
        } else {
            logoutWindow.showAtLocation(logoutView, Gravity.BOTTOM, 0, 0);
        }
        logoutView.findViewById(R.id.logout).setOnClickListener(this);

    }

    private void initViews() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        ll_message = (LinearLayout) findViewById(R.id.message_layout);
        ll_ding = (LinearLayout) findViewById(R.id.ding_layout);
        ll_work = (LinearLayout) findViewById(R.id.work_layout);
        ll_contact = (LinearLayout) findViewById(R.id.contact_layout);
        ll_mine = (LinearLayout) findViewById(R.id.mine_layout);
        ll_top_title = (LinearLayout) findViewById(R.id.top_title_layout);

        top_title_message_view = View.inflate(this, R.layout.message_top_title_layout, null);
        top_title_ding_view = View.inflate(this, R.layout.ding_top_title_layout, null);
        top_title_work_view = View.inflate(this, R.layout.work_top_title_layout, null);
        top_title_contact_view = View.inflate(this, R.layout.contact_top_title_layout, null);
        top_title_mine_view = View.inflate(this, R.layout.mine_top_title_layout, null);

        top_title_contact_view.findViewById(R.id.contact_add_friend_btn).setOnClickListener(this);
        top_title_message_view.findViewById(R.id.message_more_btn).setOnClickListener(this);
        top_title_ding_view.findViewById(R.id.ding_create_btn).setOnClickListener(this);
        top_title_work_view.findViewById(R.id.watch_news).setOnClickListener(this);
        top_title_mine_view.findViewById(R.id.mine_more_btn).setOnClickListener(this);

        top_title_message_view.findViewById(R.id.search).setOnClickListener(this);
        top_title_ding_view.findViewById(R.id.search).setOnClickListener(this);
        top_title_mine_view.findViewById(R.id.search).setOnClickListener(this);


        ll_message.setOnClickListener(this);
        ll_ding.setOnClickListener(this);
        ll_work.setOnClickListener(this);
        ll_contact.setOnClickListener(this);
        ll_mine.setOnClickListener(this);

        iv_message = (ImageView) findViewById(R.id.iv_message);
        iv_ding = (ImageView) findViewById(R.id.iv_ding);
        iv_work = (ImageView) findViewById(R.id.iv_work);
        iv_contact = (ImageView) findViewById(R.id.iv_contact);
        iv_mine = (ImageView) findViewById(R.id.iv_mine);

        tv_message = (TextView) findViewById(R.id.tv_message);
        tv_ding = (TextView) findViewById(R.id.tv_ding);
        tv_work = (TextView) findViewById(R.id.tv_work);
        tv_contact = (TextView) findViewById(R.id.tv_contact);
        tv_mine = (TextView) findViewById(R.id.tv_mine);

        //进入首页默认选择work页
        tv_message.setSelected(true);
        iv_message.setSelected(true);
        ll_top_title.addView(top_title_message_view);


        tv_current = tv_message;
        iv_current = iv_message;

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changedTab(position);
                //tv_title.startAnimation(animation);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setOffscreenPageLimit(4);//左右都缓存4个页面
    }

    private void initEvent() {
        Fragment messageFragment = new MessageFragment();
        Fragment dingFragment = new DingFragment();
        Fragment workFragment = new WorkFragment();
        Fragment contactFragment = new ContactFragment();
        Fragment mineFragment = new MineFragment();

        fragmentList.add(messageFragment);
        fragmentList.add(dingFragment);
        fragmentList.add(workFragment);
        fragmentList.add(contactFragment);
        fragmentList.add(mineFragment);

        MyFragmentPagerAdapter myAdapter = new MyFragmentPagerAdapter(getFragmentManager(), fragmentList);
        viewPager.setAdapter(myAdapter);
    }

    public void changedTab(int id) {
       // Log.e(TAG, "changedTab: " + id);
        switch (id) {
            case MESSAGE:
                iv_current.setSelected(false);
                tv_current.setSelected(false);
                ll_top_title.removeAllViews();
                ll_top_title.addView(top_title_message_view);
                iv_message.setSelected(true);
                tv_message.setSelected(true);
                iv_message.startAnimation(animation);
                iv_current = iv_message;
                tv_current = tv_message;
                break;
            case DING:
                iv_current.setSelected(false);
                tv_current.setSelected(false);
                ll_top_title.removeAllViews();
                ll_top_title.addView(top_title_ding_view);
                iv_ding.setSelected(true);
                tv_ding.setSelected(true);
                iv_ding.startAnimation(animation);
                iv_current = iv_ding;
                tv_current = tv_ding;
                break;
            case WORK:
                iv_current.setSelected(false);
                tv_current.setSelected(false);
                ll_top_title.removeAllViews();
                ll_top_title.addView(top_title_work_view);
                iv_work.setSelected(true);
                iv_work.startAnimation(animation);
                tv_work.setSelected(true);
                iv_current = iv_work;
                tv_current = tv_work;
                break;
            case CONTACT:
                iv_current.setSelected(false);
                tv_current.setSelected(false);
                ll_top_title.removeAllViews();
                ll_top_title.addView(top_title_contact_view);
                iv_contact.setSelected(true);
                tv_contact.setSelected(true);
                iv_contact.startAnimation(animation);
                iv_current = iv_contact;
                tv_current = tv_contact;
                break;
            case MINE:
                iv_current.setSelected(false);
                tv_current.setSelected(false);
                ll_top_title.removeAllViews();
                ll_top_title.addView(top_title_mine_view);
                iv_mine.setSelected(true);
                tv_mine.setSelected(true);
                iv_mine.startAnimation(animation);
                iv_current = iv_mine;
                tv_current = tv_mine;
                break;
            //布局点击事件，跳转响应pagechange事件
            case R.id.message_layout:
                viewPager.setCurrentItem(MESSAGE);
                break;
            case R.id.ding_layout:
                viewPager.setCurrentItem(DING);
                break;
            case R.id.work_layout:
                viewPager.setCurrentItem(WORK);
                break;
            case R.id.contact_layout:
                viewPager.setCurrentItem(CONTACT);
                break;
            case R.id.mine_layout:
                viewPager.setCurrentItem(MINE);
                break;

            //title右上角的+号事件
            case R.id.contact_add_friend_btn://contact界面的添加朋友按钮
                AddFriendActivity.start(this);
                break;
            case R.id.message_more_btn://建群或者建立讨论组
                showPopupWindow();//底部弹出一个框
                break;
            case R.id.ding_create_btn:
                startActivity(new Intent(MainActivity.this,CreateDingActivity.class));
                break;
            case R.id.watch_news:
                startActivity(new Intent(MainActivity.this,WatchNewsActivity.class));
                break;
            case R.id.mine_more_btn:
                popLogoutWindow();
                break;
            case R.id.logout:
                alert();
                break;
            case R.id.create_group:
                ContactSelectActivity.Option option = TeamHelper.getCreateContactSelectOption(null, 50);
                NimUIKit.startContactSelect(MainActivity.this, option, REQUEST_CODE_NORMAL);
                break;
            case R.id.create_team:
                ContactSelectActivity.Option advancedOption = TeamHelper.getCreateContactSelectOption(null, 50);
                NimUIKit.startContactSelect(MainActivity.this, advancedOption, REQUEST_CODE_ADVANCED);
                break;
            case R.id.search:
                AddFriendActivity.start(this);
                break;

        }
    }

    private void alert() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("注销");
        dialog.setMessage("您确定注销登录吗？");
        dialog.setCancelable(false);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyCache.clear();
                NIMClient.getService(AuthService.class).logout();
                Preference.saveUserToken("");
                LoginActivity.actionStart(MainActivity.this,null);
                finish();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_NORMAL) {
                final ArrayList<String> selected = data.getStringArrayListExtra(ContactSelectActivity.RESULT_DATA);
                if (selected != null && !selected.isEmpty()) {
                    TeamCreateHelper.createNormalTeam(MainActivity.this, selected, false, null);
                } else {
                    Toast.makeText(MainActivity.this, "请选择至少一个联系人！", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == REQUEST_CODE_ADVANCED) {
                final ArrayList<String> selected = data.getStringArrayListExtra(ContactSelectActivity.RESULT_DATA);
                TeamCreateHelper.createAdvancedTeam(MainActivity.this, selected);
            }
        }

    }

    @Override
    public void onClick(View v) {
        changedTab(v.getId());
    }

    public static void actionStart(Context context, String param1) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
}
