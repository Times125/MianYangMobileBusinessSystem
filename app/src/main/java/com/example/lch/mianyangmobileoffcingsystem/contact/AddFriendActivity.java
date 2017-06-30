package com.example.lch.mianyangmobileoffcingsystem.contact;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lch.mianyangmobileoffcingsystem.R;
import com.example.lch.mianyangmobileoffcingsystem.config.MyCache;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.ui.widget.ClearableEditTextWithIcon;
import com.netease.nim.uikit.model.ToolBarOptions;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

/**
 * Created by lch on 2017/3/13.
 */

public class AddFriendActivity extends UI implements View.OnClickListener {
    private ClearableEditTextWithIcon searchEdit;
    private ImageView back;
    private Button search;
    private static final String TAG = "AddFriendActivity";

    public static final void start(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, AddFriendActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friend_layout);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        findViews();
    }

    private void findViews() {
        searchEdit = findView(R.id.search_friend_edit);
        searchEdit.setDeleteImage(R.drawable.nim_grey_delete_icon);
        back = (ImageView) findViewById(R.id.addfriend_failed_back);
        search = (Button) findViewById(R.id.search_friend);
        back.setOnClickListener(this);
        search.setOnClickListener(this);
    }


    private void query() {
        DialogMaker.showProgressDialog(this, null, false);
        final String account = searchEdit.getText().toString().toLowerCase();
        NimUserInfoCache.getInstance().getUserInfoFromRemote(account, new RequestCallback<NimUserInfo>() {
            @Override
            public void onSuccess(NimUserInfo user) {
                DialogMaker.dismissProgressDialog();
                if (user == null) {
                    Log.e(TAG, "onSuccess: "+"user == null");
                    EasyAlertDialogHelper.showOneButtonDiolag(AddFriendActivity.this, R.string.user_not_exsit,
                            R.string.user_tips, R.string.ok, false, null);
                } else {
                    UserProfileActivity.start(AddFriendActivity.this, account);
                    Log.e(TAG, "onSuccess: "+"user != null"+account);
                }
            }

            @Override
            public void onFailed(int code) {
                DialogMaker.dismissProgressDialog();
                if (code == 408) {
                    Log.e(TAG, "onFailed: "+code );
                    Toast.makeText(AddFriendActivity.this, R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "onFailed: "+code );
                    Toast.makeText(AddFriendActivity.this, "on failed:" + code, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onException(Throwable exception) {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(AddFriendActivity.this, "on exception:" + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addfriend_failed_back:
                finish();
                break;
            case R.id.search_friend:
                if (TextUtils.isEmpty(searchEdit.getText().toString())) {
                    Log.e(TAG, "onClick: " + "不能为空");
                    Toast.makeText(AddFriendActivity.this, R.string.not_allow_empty, Toast.LENGTH_SHORT).show();
                } else if (searchEdit.getText().toString().equals(MyCache.getAccount())) {
                    Log.e(TAG, "onClick: " + "不能添加自己为好友");
                    Toast.makeText(AddFriendActivity.this, R.string.add_friend_self_tip, Toast.LENGTH_SHORT).show();
                } else {
                    query();
                    Log.e(TAG, "onClick: " + "query（）查询");
                }
                break;
        }
    }
}
