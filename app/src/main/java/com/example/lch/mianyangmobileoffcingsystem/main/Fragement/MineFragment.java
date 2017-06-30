package com.example.lch.mianyangmobileoffcingsystem.main.Fragement;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lch.mianyangmobileoffcingsystem.R;
import com.example.lch.mianyangmobileoffcingsystem.about.AboutActivity;
import com.example.lch.mianyangmobileoffcingsystem.about.CommomProblemActivity;
import com.example.lch.mianyangmobileoffcingsystem.about.FeedbackActivity;
import com.example.lch.mianyangmobileoffcingsystem.adapter.MyListViewAdapter;
import com.example.lch.mianyangmobileoffcingsystem.config.MyCache;
import com.example.lch.mianyangmobileoffcingsystem.contact.UserProfileSettingActivity;
import com.example.lch.mianyangmobileoffcingsystem.service.DownloadService;
import com.example.lch.mianyangmobileoffcingsystem.tools.Constants;
import com.example.lch.mianyangmobileoffcingsystem.utils.ScoreUtils;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by lch on 2017/3/7.
 */

public class MineFragment extends Fragment implements AdapterView.OnItemClickListener {

    ProgressDialog progress;
    private View view;
    HeadImageView circleImageView;
    private TextView name;
    private ListView setList;
    private List<String> des = new ArrayList<>();
    private List<Integer> res = new ArrayList<>();
    public static final String[] des1 = {"好友分享", "检查更新", "赏个好评", "常见问题", "意见反馈", "关于"};
    public static final int[] res1 = {R.mipmap.share, R.mipmap.check_update,
            R.mipmap.admire, R.mipmap.common_problem, R.mipmap.feedback, R.mipmap.about};
    private static final String TAG = "MineFragment";
    private DownloadService.DownloadBinder binder;
    //弹出窗
    private PopupWindow popupWindow;
    private View contentView;
    private ListView shareList;
    List<String> list = new ArrayList<>();
    List<String> list1 = new ArrayList<>();
    private String url;

    private ServiceConnection con = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (DownloadService.DownloadBinder) service;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.mine_fragment_layout, container, false);
        initData();
        initViews();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initEvent();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String data = (String) msg.obj;
                    pareData(data);
                    break;
            }

        }
    };

    //检查更新
    private void checkUpdate() {
        progress = new ProgressDialog(getActivity());
        progress.setTitle("检查更新");
        progress.setMessage("正在检查更新，请稍后...");
        progress.setCancelable(true);
        progress.show();
        final String version = getVersion();
        Log.e(TAG, "checkUpdate: " + version);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody body = new FormBody.Builder()
                            .add("version", version)
                            .build();
                    Request request = new Request.Builder()
                            .url(Constants.CHECK_UPDATE_URL)
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    String data = response.body().string();
                    Message msg = new Message();
                    msg.what  = 1;
                    msg.obj = data;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void pareData(String response) {
        try {
            Log.e(TAG, "pareData: " + response);
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int code = jsonObject.getInt("code");
                String versionInfo = jsonObject.getString("versionInfo");
                final String downloadUrl = jsonObject.getString("url");
                url = downloadUrl;
                Log.e(TAG, "pareData: " + code + "-" + versionInfo + "-" + downloadUrl);
                if (code == Constants.CAN_NOT_UPDATE) {
                    progress.dismiss();
                    Toast.makeText(getActivity(), "您当前应用为最新版本", Toast.LENGTH_SHORT).show();
                } else if (code == Constants.CAN_UPDATE) {
                    progress.dismiss();
                    Log.e(TAG, "pareData: "+ "可以更新" );
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle("更新");
                    dialog.setMessage("发现可更新的版本" + versionInfo + " 是否更新？");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getActivity(), DownloadService.class);
                            //getActivity().startService(intent);
                            getActivity().bindService(intent, con, Context.BIND_AUTO_CREATE);
                            if (binder != null)
                                binder.startDownload("http://"+ downloadUrl);
                            else {
                                handler.postDelayed(runnable,2000);
                            }
                        }
                    });
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "pareData: " + " JsonException");
        }
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            binder.startDownload("http://" + url);
        }
    };
    private String getVersion() {
        String version;
        try {
            PackageManager packageManager = getActivity().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
            version = packageInfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "-1.0";
    }

    //系统简单的分享功能
    private void share() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/*");
        share.putExtra(Intent.EXTRA_SUBJECT, "嘀卡");
        share.putExtra(Intent.EXTRA_TEXT, "我正在使用【嘀卡移动办公软件，快来一起使用吧！打开应用宝搜索【嘀卡】，下载即可使用！");
        share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(share, "分享给好友"));
    }

    //初始化事件
    private void initEvent() {
        circleImageView.loadBuddyAvatar(MyCache.getAccount());
        name.setText(MyCache.getAccount());
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileSettingActivity.start(getActivity(), MyCache.getAccount());
            }
        });
        setList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        share();
                        break;
                    case 1:
                        checkUpdate();//检查更新
                        break;
                    case 2:
                        admire();
                        break;
                    case 3:
                        commonProblem();
                        break;
                    case 4:
                        feedback();
                        break;
                    case 5:
                        about();
                        break;
                }
            }
        });
    }

    private void feedback() {
        getActivity().startActivity(new Intent(getActivity(), FeedbackActivity.class));
    }

    private void about() {
        getActivity().startActivity(new Intent(getActivity(), AboutActivity.class));
    }

    private void commonProblem() {
        getActivity().startActivity(new Intent(getActivity(), CommomProblemActivity.class));
    }

    private void showPopupWindow() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.share_pop_layout, null);
        shareList = (ListView) contentView.findViewById(R.id.list_market);
        list = ScoreUtils.InstalledAPPs(getActivity());
        list1 = ScoreUtils.InstalledAPPsName(getActivity(), list);
        String[] data = list1.toArray(new String[list1.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.my_simple_list_item, data);
        shareList.setAdapter(adapter);
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
        shareList.setOnItemClickListener(this);

    }

    private void admire() {
        showPopupWindow();
    }

    private void initData() {
        for (int i = 0; i < des1.length; i++) {
            des.add(des1[i]);
            res.add(res1[i]);
        }
    }

    private void initViews() {
        circleImageView = (HeadImageView) view.findViewById(R.id.head_image);
        name = (TextView) view.findViewById(R.id.user_name);
        MyListViewAdapter adapter = new MyListViewAdapter(getActivity(), des, res, R.layout.list_view_item_one);
        setList = (ListView) view.findViewById(R.id.setting_lv);
        setList.setAdapter(adapter);
        setListViewHeightBasedOnChildren(setList);
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String packageName = null;
        try {
            PackageManager packageManager = getActivity().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
            packageName = packageInfo.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        ScoreUtils.launchAppDetail(packageName, list.get(position));
    }
}
