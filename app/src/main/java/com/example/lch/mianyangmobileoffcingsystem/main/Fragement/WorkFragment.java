package com.example.lch.mianyangmobileoffcingsystem.main.Fragement;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lch.mianyangmobileoffcingsystem.R;
import com.example.lch.mianyangmobileoffcingsystem.config.MyCache;
import com.example.lch.mianyangmobileoffcingsystem.main.activity.AddTodayTaskActivity;
import com.example.lch.mianyangmobileoffcingsystem.main.activity.WatchNewsActivity;
import com.example.lch.mianyangmobileoffcingsystem.tools.Constants;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by lch on 2017/3/7.
 * 任务逻辑
 * 1。用户点击日历上的时候，是日历下方显示该天的任务，需要向服务器读取任务记录。
 * 2。当用户点击日历下方的任务（textview）的时候，就是进入编辑任务的阶段，
 * 需要将已存在的任务信息也发送到任务编辑界面显示。
 **/

public class WorkFragment extends Fragment implements View.OnClickListener {
    private View view;
    private TextView today_task;
    private MaterialCalendarView materialCalendarView;

    private static final String TAG = "WorkFragment";

    private String clickDate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.work_fragment_layout, container, false);
        initViews();
        return view;
    }

    private void initViews() {
        materialCalendarView = (MaterialCalendarView) view.findViewById(R.id.calendarView);
        today_task = (TextView) view.findViewById(R.id.today_task);
        today_task.setEnabled(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initEvent();
    }

    private void initEvent() {
        today_task.setOnClickListener(this);
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                clickDate = date.getYear() + "-" + (date.getMonth() + 1) + "-" + date.getDay();
                clickDate = clickDate.toString();
                getDataFromServer();
                Toast.makeText(getActivity(), clickDate, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    today_task.setEnabled(true);
                    String str = (String) msg.obj;
                    if (str.equals("nothing！！！")) {
                        today_task.setText(R.string.nothing);
                    } else {
                        today_task.setText(str);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /*当用户点击日历上的具体日期时候，从服务器拿到该日期记录的数据*/
    private void getDataFromServer() {
        final String accid = MyCache.getAccount();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message messgae = new Message();
                OkHttpClient client = new OkHttpClient();
                try {
                    RequestBody body = new FormBody.Builder()
                            .add("accid", accid)
                            .add("date", clickDate)
                            .add("log", "")//这里传空，表示上获取数据，服务器就不做写数据到数据库的任务，而是读数据库数据。
                            .build();
                    Request request = new Request.Builder()
                            .url(Constants.SAVE_TASK_URL)
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    messgae.what = 1;
                    messgae.obj = responseData;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (client != null) {
                        client = null;
                    }
                    handler.sendMessage(messgae);
                }

            }
        }).start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode == 200) {
            if (requestCode == 100) {
                Bundle bundle = data.getExtras();
                String str = bundle.getString("returnData");
                today_task.setText(str);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.today_task:
                String existedData = today_task.getText().toString();
                if (TextUtils.isEmpty(clickDate)) {
                    Toast.makeText(getActivity(), "您还未选中日期，暂时不能编辑任务哟～～", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getActivity(), AddTodayTaskActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("date", clickDate);
                bundle.putString("content", existedData);
                intent.putExtras(bundle);
                startActivityForResult(intent, 100);//不能使用getActivity().startActivityForResult(intent, 100);不然没有返回的数据，
                                                    // 并且fragment中的onActivityResult（）方法也要加上super.onActivityResult(requestCode, resultCode, data);
                break;
            default:
                break;
        }
    }
}
