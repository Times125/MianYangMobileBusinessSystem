package com.example.lch.mianyangmobileoffcingsystem.main.Fragement;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.lch.mianyangmobileoffcingsystem.R;
import com.example.lch.mianyangmobileoffcingsystem.adapter.MyDingListViewAdapter;
import com.example.lch.mianyangmobileoffcingsystem.tools.Constants;
import com.netease.nim.uikit.team.helper.AnnouncementHelper;
import com.netease.nim.uikit.team.model.Announcement;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lch on 2017/3/7.
 */

public class DingFragment extends Fragment {
    private View view;
    private SwipeRefreshLayout refreshLayout;
    private ListView dinglist;
    private List<String> imageUrllist = new ArrayList<>();
    private List<String> namelist = new ArrayList<>();
    private List<Long> timelist = new ArrayList<>();
    private List<String> contentlist = new ArrayList<>();
    private List<String> titlelist = new ArrayList<>();
    private String accid;
    private static final String TAG = "DingFragment";
    MyDingListViewAdapter adapter;

    //记录有多少条公告
    private int itemCount = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.ding_fragment_layout, container, false);
        initViews();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initEvent();
        initData();

    }

    private void initData() {
        getAllUserTeam();
    }

    /*展现数据到ui界面上*/
    private void showOnUi(Map<String, List<Announcement>> map) {
        Set<String> set = map.keySet();
        for (String str : set) {
            for (int i = 0; i < map.get(str).size(); i++) {
                String name = map.get(str).get(i).getCreator();
                Long time = map.get(str).get(i).getTime();
                String content = map.get(str).get(i).getContent();
                String title = map.get(str).get(i).getTitle();
                String url = map.get(str).get(i).getTeamIcon();
                if (namelist.contains(name) && timelist.contains(time)) {//防止重复的数据显示到界面上
                    continue;
                }
                Log.e(TAG, "showOnUi: " + url);
                namelist.add(name);
                timelist.add(time);
                contentlist.add(content);
                titlelist.add(title);
                imageUrllist.add(url);
            }
        }
        adapter = new MyDingListViewAdapter(getActivity(), R.layout.ding_item, imageUrllist, namelist, timelist, contentlist, titlelist,dinglist);
        dinglist.setVerticalScrollBarEnabled(false);
        dinglist.setAdapter(adapter);
        if (refreshLayout.isRefreshing() && refreshLayout != null) {
            refreshLayout.setRefreshing(false);
        }
        dinglist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    //获取群资料
    private List<Team> getAllUserTeam() {
        final Map<String, List<Announcement>> map = new HashMap<>();
        NIMClient.getService(TeamService.class).queryTeamListByType(TeamTypeEnum.Advanced).setCallback(new RequestCallback<List<Team>>() {
            @Override
            public void onSuccess(List<Team> teams) {
                for (Team t : teams) {
                    String teamId = t.getId();
                    String url = t.getIcon();
                    String announcement = t.getAnnouncement();
                    //如果一个群里面没有公告，那么自己进行下次循环，不用保存头像url
                    if (TextUtils.isEmpty(announcement))
                        continue;

                    if (TextUtils.isEmpty(url)) {
                        //群头像还没有被设置，imserver没有头像，需要自己设置一个临时头像
                        url = Constants.TEAM_HEAD_VIEW_URL;
                    }
                    //解析公告内容
                    List<Announcement> reslist = AnnouncementHelper.getAnnouncementsWithUrl(teamId, announcement, url, 100);

                    map.put(teamId, reslist);
                    //Log.e(TAG, "onSuccess: " + url);
                    //Log.e(TAG, "onSuccess: " + t.getId() + ":" + announcement);
                }
                showOnUi(map);
            }

            @Override
            public void onFailed(int i) {

            }

            @Override
            public void onException(Throwable throwable) {

            }
        });
        return null;
    }


    private void initEvent() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
                //refreshLayout.setRefreshing(false);
            }
        });
    }

    private void initViews() {
        dinglist = (ListView) view.findViewById(R.id.ding_listview);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        //设置刷新图标的颜色
        refreshLayout.setColorSchemeResources(R.color.color_green_00d3a9, R.color.color_red_ccfa3c55,
                R.color.color_activity_blue_bg, R.color.color_yellow_b39729);
    }

      /*  *//*获取公告流程：
    * 1.将用户accid发给本地应用服务器
    * 2.应用服务器根据accid查询所在群里面的所有公告
    * 3.服务器将所有公告1以json格式数据传给app
    * 4.app解析数据，并以listview的形式展现在ui*//*

    *//*获取服务器公告数据*//*
    private void getDingData() {
        accid = MyCache.getAccount();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                OkHttpClient client = new OkHttpClient();
                try {

                    RequestBody body = new FormBody.Builder()
                            .add("accid", accid)
                            .build();
                    Request request = new Request.Builder()
                            .url(Constants.GET_DING_URL)
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();

                    msg.what = REFRESH_SUCCESS;
                    msg.obj = responseData;

                } catch (IOException e) {
                    msg.what = REFRESH_FAILED;
                    e.printStackTrace();
                } finally {
                    if (client != null) {
                        client = null;
                    }
                    handler.sendMessage(msg);
                }


            }
        }).start();

    }*/

    /*解析json公告数据*/
   /* private void parseJson(String data) {
        Message msg = new Message();
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String name = obj.optString("name");
                String time = obj.optString("time");
                String content = obj.optString("content");
                String url = obj.optString("url");
                namelist.add(name);
                timelist.add(time);
                contentlist.add(content);
                urllist.add(url);
            }
            msg.what = PARSE_DONE;
        } catch (Exception e) {
            msg.what = PARSE_EXCEPTION;
            e.printStackTrace();
        } finally {
            handler.sendMessage(msg);
        }

    }*/
   /*    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_SUCCESS:
                    break;
                case REFRESH_FAILED:
                    Toast.makeText(getActivity(), "刷新失败", Toast.LENGTH_SHORT).show();
                    break;
                case PARSE_DONE:
                    if (refreshLayout.isRefreshing() && refreshLayout != null) {
                        refreshLayout.setRefreshing(false);
                    }
                    break;
                case PARSE_EXCEPTION:
                    Log.e(TAG, "handleMessage: " + "PARSE_EXCEPTION");
                    break;

            }
        }
    };*/
}
