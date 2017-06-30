package com.example.lch.mianyangmobileoffcingsystem.main.Fragement;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lch.mianyangmobileoffcingsystem.R;
import com.example.lch.mianyangmobileoffcingsystem.config.MyCache;
import com.example.lch.mianyangmobileoffcingsystem.helper.SystemMessageUnreadManager;
import com.example.lch.mianyangmobileoffcingsystem.reminder.ReminderId;
import com.example.lch.mianyangmobileoffcingsystem.reminder.ReminderItem;
import com.example.lch.mianyangmobileoffcingsystem.reminder.ReminderManager;
import com.example.lch.mianyangmobileoffcingsystem.tools.BlackListActivity;
import com.example.lch.mianyangmobileoffcingsystem.tools.SessionHelper;
import com.example.lch.mianyangmobileoffcingsystem.tools.SystemMessageActivity;
import com.example.lch.mianyangmobileoffcingsystem.tools.TeamListActivity;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.contact.ContactsCustomization;
import com.netease.nim.uikit.contact.ContactsFragment;
import com.netease.nim.uikit.contact.core.item.AbsContactItem;
import com.netease.nim.uikit.contact.core.item.ItemTypes;
import com.netease.nim.uikit.contact.core.model.ContactDataAdapter;
import com.netease.nim.uikit.contact.core.viewholder.AbsContactViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lch on 2017/3/7.
 */

public class ContactFragment extends Fragment {
    private ContactsFragment contactsFragment;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.contact_fragment_layout, container, false);
        initViews();
        return view;
    }

    private void initViews() {

    }

    /**
     * ******************************** 功能项定制 ***********************************
     */
    public final static class FuncItem extends AbsContactItem {
        static final FuncItem VERIFY = new FuncItem();
        static final FuncItem NORMAL_TEAM = new FuncItem();
        static final FuncItem ADVANCED_TEAM = new FuncItem();
        static final FuncItem BLACK_LIST = new FuncItem();

        @Override
        public int getItemType() {
            return ItemTypes.FUNC;
        }

        @Override
        public String belongsGroup() {
            return null;
        }

        public static final class FuncViewHolder extends AbsContactViewHolder<FuncItem> {
            private ImageView image;
            private TextView funcName;
            private TextView unreadNum;

            @Override
            public View inflate(LayoutInflater inflater) {
                View view = inflater.inflate(R.layout.func_contacts_item, null);
                this.image = (ImageView) view.findViewById(R.id.img_head);
                this.funcName = (TextView) view.findViewById(R.id.tv_func_name);
                this.unreadNum = (TextView) view.findViewById(R.id.tab_new_msg_label);
                return view;
            }

            @Override
            public void refresh(ContactDataAdapter contactAdapter, int position, FuncItem item) {
                if (item == VERIFY) {
                    funcName.setText("验证提醒");
                    image.setImageResource(R.mipmap.icon_verify_remind);
                    image.setScaleType(ImageView.ScaleType.FIT_XY);
                    int unreadCount = SystemMessageUnreadManager.getInstance().getSysMsgUnreadCount();
                    updateUnreadNum(unreadCount);

                    ReminderManager.getInstance().registerUnreadNumChangedCallback(new ReminderManager.UnreadNumChangedCallback() {
                        @Override
                        public void onUnreadNumChanged(ReminderItem item) {
                            if (item.getId() != ReminderId.CONTACT) {
                                return;
                            }

                            updateUnreadNum(item.getUnread());
                        }
                    });
                } else if (item == NORMAL_TEAM) {
                    funcName.setText("讨论组");
                    image.setImageResource(R.mipmap.ic_secretary);
                } else if (item == ADVANCED_TEAM) {
                    funcName.setText("高级群");
                    image.setImageResource(R.mipmap.ic_advanced_team);
                } else if (item == BLACK_LIST) {
                    funcName.setText("黑名单");
                    image.setImageResource(R.mipmap.ic_black_list);
                }

                if (item != VERIFY) {
                    image.setScaleType(ImageView.ScaleType.FIT_XY);
                    unreadNum.setVisibility(View.GONE);
                }
            }

            private void updateUnreadNum(int unreadCount) {
                // 2.*版本viewholder复用问题
                if (unreadCount > 0 && funcName.getText().toString().equals("验证提醒")) {
                    unreadNum.setVisibility(View.VISIBLE);
                    unreadNum.setText("" + unreadCount);
                } else {
                    unreadNum.setVisibility(View.GONE);
                }
            }
        }

        static List<AbsContactItem> provide() {
            List<AbsContactItem> items = new ArrayList<AbsContactItem>();
            items.add(VERIFY);
            items.add(NORMAL_TEAM);
            items.add(ADVANCED_TEAM);
            items.add(BLACK_LIST);

            return items;
        }

        //这4个界面还需要修改
        static void handle(Context context, AbsContactItem item) {
            if (item == VERIFY) {
                SystemMessageActivity.start(context);//验证消息提醒
            } else if (item == NORMAL_TEAM) {
                TeamListActivity.start(context, ItemTypes.TEAMS.NORMAL_TEAM);//讨论组
            } else if (item == ADVANCED_TEAM) {
                TeamListActivity.start(context, ItemTypes.TEAMS.ADVANCED_TEAM);//群
            } else if (item == BLACK_LIST) {
                BlackListActivity.start(context);//黑名单
            }
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void initData() {
        addContactFragment();
    }

    // 将通讯录列表fragment动态集成进来。
    private void addContactFragment() {
        contactsFragment = new ContactsFragment();
        contactsFragment.setContainerId(R.id.contact_fragment);
        UI activity = (UI) getActivity();
        contactsFragment = (ContactsFragment) activity.addFragment(contactsFragment);
        // 功能项定制
        contactsFragment.setContactsCustomization(new ContactsCustomization() {
            @Override
            public Class<? extends AbsContactViewHolder<? extends AbsContactItem>> onGetFuncViewHolderClass() {
                return FuncItem.FuncViewHolder.class;
            }

            @Override
            public List<AbsContactItem> onGetFuncItems() {
                return FuncItem.provide();
            }

            @Override
            public void onFuncItemClick(AbsContactItem item) {
                FuncItem.handle(getActivity(), item);
            }
        });

    }
}
