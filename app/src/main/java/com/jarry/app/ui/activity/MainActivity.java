package com.jarry.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

import com.google.gson.Gson;
import com.jarry.app.App;
import com.jarry.app.R;
import com.jarry.app.bean.User;
import com.jarry.app.bean.UserComm;
import com.jarry.app.fragment.ActiveFragment;
import com.jarry.app.fragment.FindFragment;
import com.jarry.app.fragment.MyFragment;
import com.jarry.app.fragment.ServiceFragment;
import com.jarry.app.fragment.SignFragment;
import com.jarry.app.model.TabItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static UserComm sUser = null; // 当前登录用户
    public static final int TAB_WECHAT = 0;
    public static final int TAB_FRIEND = 1;
    public static final int TAB_CONTACTS = 2;
    public static final int TAB_SETTING = 3;

    private List<TabItem> mFragmentList;

    private FragmentTabHost mFragmentTabHost;
    ActionBar actionBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actionBar = getSupportActionBar();
        ButterKnife.bind(this);
        initTabItemData();
        User user = App.getUser();
        Long id = 0L;
        try {
            id = Long.valueOf(user.getId());
        } catch (Exception e) {
            id = 0L;
        }
        sUser = new UserComm(id, user.getScreen_name());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_exit) {
            finish();
            App.exit();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 更新新消息数量
     *
     * @param tabIndex tab 的索引
     * @param msgCount 消息数量
     */
    public void updateMsgCount(int tabIndex, int msgCount) {
        mFragmentList.get(tabIndex).setNewMsgCount(msgCount);
    }

    /**
     * 初始化 Tab 数据
     */
    private void initTabItemData() {
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new TabItem(
                R.mipmap.tab_wechat_normal,
                R.mipmap.tab_wechat_selected,
                "发现",
                FindFragment.class, R.color.colorTabText
        ));

        mFragmentList.add(new TabItem(
                R.mipmap.tab_friend_normal,
                R.mipmap.tab_friend_selected,
                "活动",
                ActiveFragment.class, R.color.colorTabText
        ));

        mFragmentList.add(new TabItem(
                R.mipmap.ic_sign_normal,
                R.mipmap.ic_sign_h,
                "标志",
                SignFragment.class, R.color.colorTabText
        ));

        mFragmentList.add(new TabItem(
                R.mipmap.tab_settings_normal,
                R.mipmap.tab_settings_selected, "服务",
                ServiceFragment.class, R.color.colorTabText
        ));
        mFragmentList.add(new TabItem(
                R.mipmap.tab_wechat_normal,
                R.mipmap.tab_wechat_selected, "我的",
                MyFragment.class, R.color.colorTabText
        ));

        mFragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        // 绑定 FragmentManager
        mFragmentTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        // 删除分割线
//        mFragmentTabHost.getTabWidget().setDividerDrawable(null);

        for (int i = 0; i < mFragmentList.size(); i++) {
            TabItem tabItem = mFragmentList.get(i);
            // 创建 tab
            TabHost.TabSpec tabSpec = mFragmentTabHost.newTabSpec(
                    tabItem.getTabText()).
                    setIndicator(tabItem.getTabView(MainActivity.this));
            // 将创建的 tab 添加到底部 tab 栏中（ @android:id/tabs ）
            // 将 Fragment 添加到页面中（ @android:id/tabcontent ）
            mFragmentTabHost.addTab(tabSpec, tabItem.getFragmentClass(), null);
            // 底部 tab 栏设置背景图片
            mFragmentTabHost.getTabWidget().setBackgroundResource(android.R.color.white);

            // 默认选中第一个 tab
            if (i == 0) {
                tabItem.setChecked(true);
                if (actionBar != null)
                    actionBar.setTitle(tabItem.getTabText());
            } else {
                tabItem.setChecked(false);
            }
        }

        // 切换 tab 时，回调此方法
        mFragmentTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                for (int i = 0; i < mFragmentList.size(); i++) {
                    TabItem tabItem = mFragmentList.get(i);
                    // 通过 tag 检查用户点击的是哪个 tab
                    if (tabId.equals(tabItem.getTabText())) {
                        tabItem.setChecked(true);
                        if (actionBar != null)
                            actionBar.setTitle(tabItem.getTabText());
                    } else {
                        tabItem.setChecked(false);
                    }
                }
            }
        });
    }

}
