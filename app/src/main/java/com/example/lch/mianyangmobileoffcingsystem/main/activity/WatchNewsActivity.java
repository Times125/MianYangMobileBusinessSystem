package com.example.lch.mianyangmobileoffcingsystem.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.lch.mianyangmobileoffcingsystem.R;
import com.example.lch.mianyangmobileoffcingsystem.adapter.NewsAdapter;
import com.example.lch.mianyangmobileoffcingsystem.callback.MyItemToucHelperCallback;
import com.example.lch.mianyangmobileoffcingsystem.callback.StartDragListener;
import com.example.lch.mianyangmobileoffcingsystem.tools.Constants;
import com.netease.nim.uikit.common.activity.UI;


import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by lch on 2017/3/18.
 */

public class WatchNewsActivity extends UI implements View.OnClickListener,StartDragListener {

    //title bar
    private TextView titleText;
    private TextView titleEditText;
    private ImageView back;
    private LinearLayout titleBarLayout;

    private RecyclerView recyclerView;
    private List<String> newsTitlelist = new ArrayList<>();
    private List<String> newsContentlist = new ArrayList<>();
    private static final String TAG = "WatchNewsActivity";

    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity_layout);
        initViews();
        initData();


    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    parseJson(msg.obj.toString());
                    break;
            }
        }
    };

    private void parseJson(String data) {
        try {
            JSONArray jsonArray = JSONArray.parseArray(data);
            for (int i = 0; i < (jsonArray.size() <= 50 ? jsonArray.size() : 50); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String url = jsonObject.getString("url");
                String content = jsonObject.getString("content");
                newsContentlist.add(content);
                newsTitlelist.add(url);
            }
            show();
        } catch (Exception e) {
            Log.e(TAG, "parseJson: " + "Exception" );
            e.printStackTrace();
        }

    }

    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        // 执行拖动效果
        mItemTouchHelper.startDrag(viewHolder);
    }

    private void show() {

        NewsAdapter adapter = new NewsAdapter(WatchNewsActivity.this,newsTitlelist, newsContentlist,this);
        recyclerView.setAdapter(adapter);
        //2、设置ItemTouchHelper
        ItemTouchHelper.Callback callback = new MyItemToucHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

    }

    private void initData() {
        newsTitlelist.clear();
        newsContentlist.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .build();
                    Request request = new Request.Builder()
                            .url(Constants.GET_NEWS_URL)
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Message message = new Message();
                    message.what = 0;
                    message.obj = responseData;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message message = new Message();
                    message.what = Constants.LOGIN_TIMEOUT_CODE;
                    handler.sendMessage(message);
                }

            }
        }).start();

    }

    private void initViews() {
        titleBarLayout = (LinearLayout) findViewById(R.id.news);
        titleEditText = (TextView) titleBarLayout.findViewById(R.id.edit_info_text);
        titleText = (TextView) titleBarLayout.findViewById(R.id.action_bar_title_text);
        titleText.setText("商务热点");
        titleEditText.setVisibility(View.GONE);
        back = (ImageView) titleBarLayout.findViewById(R.id.failed_back);
        back.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.failed_back:
                finish();
                break;
        }
    }
}
