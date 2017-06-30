package com.example.lch.mianyangmobileoffcingsystem.main.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lch.mianyangmobileoffcingsystem.R;
import com.example.lch.mianyangmobileoffcingsystem.adapter.MyTeamChooseAdapter;
import com.example.lch.mianyangmobileoffcingsystem.config.MyCache;
import com.example.lch.mianyangmobileoffcingsystem.tools.Constants;
import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.netease.nim.uikit.team.activity.AdvancedTeamCreateAnnounceActivity;
import com.netease.nim.uikit.team.helper.AnnouncementHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by lch on 2017/3/17.
 */

public class CreateDingActivity extends UI implements View.OnClickListener {
    //title bar
    private TextView titleText;
    private TextView titleEditText;
    private ImageView back;
    private LinearLayout titleBarLayout;
    private ListView listView;
    private EditText ding_edit;
    private EditText ding_title;
    private List<String> teamHeadlist = new ArrayList<>();
    private List<String> teamNamelist = new ArrayList<>();
    private List<String> teamIdList = new ArrayList<>();

    private String selectedTeamID = null;
    private String announce;
    private int selectedItem = -1;
    MyTeamChooseAdapter adapter;
    private static final String TAG = "CreateDingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creat_ding_layout);
        initViews();
        getMyTeamInfo();
        initEvent();

    }

    /*获取我的群信息*/
    private void getMyTeamInfo() {
        NIMClient.getService(TeamService.class).queryTeamListByType(TeamTypeEnum.Advanced).setCallback(new RequestCallback<List<Team>>() {
            @Override
            public void onSuccess(List<Team> teams) {
                for (Team t : teams) {
                    teamIdList.add(t.getId());
                    teamNamelist.add(t.getName());
                    if (TextUtils.isEmpty(t.getIcon())) {
                        teamHeadlist.add(Constants.TEAM_HEAD_VIEW_URL);
                        continue;
                    }
                    teamHeadlist.add(t.getIcon());
                }
            }

            @Override
            public void onFailed(int i) {

            }

            @Override
            public void onException(Throwable throwable) {

            }
        });

    }

    private void initEvent() {
        adapter = new MyTeamChooseAdapter(this, teamNamelist, teamIdList,
                R.layout.choose_ding_rcvs_obj_item, teamHeadlist, listView);
        listView.setAdapter(adapter);
        //setListViewHeightBasedOnChildren(listView);
        //listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectedItem(position);
                adapter.notifyDataSetInvalidated();
                selectedTeamID = teamIdList.get(position);
                selectedItem = position;

            }
        });
    }

    private void initViews() {
        listView = (ListView) findViewById(R.id.choose_team_lv);
        titleBarLayout = (LinearLayout) findViewById(R.id.create_ding);
        titleEditText = (TextView) titleBarLayout.findViewById(R.id.edit_info_text);
        titleText = (TextView) titleBarLayout.findViewById(R.id.action_bar_title_text);
        ding_title = (EditText) findViewById(R.id.ding_title);
        titleText.setText("发布公告");
        titleEditText.setText("发送");
        ding_edit = (EditText) findViewById(R.id.ding_content);
        titleEditText.setOnClickListener(this);
        back = (ImageView) titleBarLayout.findViewById(R.id.failed_back);
        back.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.failed_back:
                finish();
                break;
            case R.id.edit_info_text:
                check();
                break;
            default:
                break;
        }
    }

    private void check() {
        String title = ding_title.getText().toString();
        String content = ding_edit.getText().toString();
        if (!TextUtils.isEmpty(content) && selectedItem != -1 && !TextUtils.isEmpty(title)) {
            requestAnnounceData();
        } else {
            Toast.makeText(this, "请检查是否有遗漏项，如未填写公告内容、标题或者未选择待接收公告的群组", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void requestAnnounceData() {
        if (!NetworkUtil.isNetAvailable(this)) {
            Toast.makeText(this, com.netease.nim.uikit.R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        // 请求群信息
        Team t = TeamDataCache.getInstance().getTeamById(selectedTeamID);
        if (t != null) {
            updateTeamData(t);
            postToServer();
        } else {
            TeamDataCache.getInstance().fetchTeamById(selectedTeamID, new SimpleCallback<Team>() {
                @Override
                public void onResult(boolean success, Team result) {
                    if (success && result != null) {
                        updateTeamData(result);
                        postToServer();
                    }
                }
            });
        }
    }

    /**
     * 获得最新公告内容
     *
     * @param team 群
     */
    private void updateTeamData(Team team) {
        if (team == null) {
            Toast.makeText(this, getString(com.netease.nim.uikit.R.string.team_not_exist), Toast.LENGTH_SHORT).show();
            showKeyboard(false);
            finish();
        } else {
            announce = team.getAnnouncement();
        }
    }



    /*把数据提交到服务器*/
    private void postToServer() {
        String announcement = AnnouncementHelper.makeAnnounceJson(announce, ding_title.getText().toString(),
                ding_edit.getText().toString());
        NIMClient.getService(TeamService.class).updateTeam(selectedTeamID, TeamFieldEnum.Announcement, announcement).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(CreateDingActivity.this, com.netease.nim.uikit.R.string.update_success, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(int i) {
                Toast.makeText(CreateDingActivity.this, "发送公告失败，请稍后再试", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onException(Throwable throwable) {
                Toast.makeText(CreateDingActivity.this, "发送异常，请稍后再试", Toast.LENGTH_SHORT).show();
            }
        });
        /****SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd HH:mm:ss");
         final String content = ding_edit.getText().toString();
         final String accid = MyCache.getAccount();
         final String time = sdf.format(new Date(System.currentTimeMillis()));
         final String url = "not-exists";
         Log.e(TAG, "postToServer: "+ time );
         new Thread(new Runnable() {
        @Override public void run() {
        OkHttpClient client = new OkHttpClient();
        try {
        RequestBody body = new FormBody.Builder()
        .add("name",accid)
        .add("time",time)
        .add("content",content)
        .add("url",url)
        .build();
        Request request = new Request.Builder()
        .url(Constants.POST_DING_URL)
        .post(body)
        .build();
        Response response = client.newCall(request).execute();
        String data = response.body().string();

        } catch (Exception e) {

        }
        }
        }).start();**/
    }

}
