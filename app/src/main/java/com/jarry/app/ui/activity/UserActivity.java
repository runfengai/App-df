package com.jarry.app.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jarry.app.R;
import com.jarry.app.base.MVPBaseActivity;
import com.jarry.app.bean.User;
import com.jarry.app.ui.presenter.UserPresenter;
import com.jarry.app.ui.view.IUserView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;


public class UserActivity extends MVPBaseActivity<IUserView, UserPresenter> implements IUserView {

    private static final String USER = "user";
    private User user;

    @BindView(R.id.tv_user_name)
    TextView userName;
    @BindView(R.id.tv_user_desc)
    TextView tv_user_desc;
    @BindView(R.id.tv_user_friends_count)
    TextView tv_user_friends_count;
    @BindView(R.id.tv_user_flowers_count)
    TextView tv_user_flowers_count;
    @BindView(R.id.iv_user_icon)
    CircleImageView iv_user_icon;
    @BindView(R.id.iv_user_bg)
    ImageView iv_user_bg;


    @OnClick(R.id.iv_back)
    void back() {
        finish();
    }


    @Override
    protected UserPresenter createPresenter() {
        return new UserPresenter(this);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_user_info;
    }


    private void parseIntent() {
        user = new User();
//        user = (User) getIntent().getSerializableExtra(USER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseIntent();
        initUserInfo();
    }

    private void initUserInfo() {
        userName.setText(user.getScreen_name());
        tv_user_desc.setText(user.getDescription());
        tv_user_friends_count.setText("关注 " + user.getFriends_count());
        tv_user_flowers_count.setText("粉丝 " + user.getFollowers_count());
        Glide.with(this).load(user.getAvatar_hd()).centerCrop().into(iv_user_icon);
        Glide.with(this).load(user.getCover_image_phone()).centerCrop().into(iv_user_bg);
    }

    private User getUserInfoFromDB() {
        User user = new User();
        return user;

    }

    @Override
    public void setDataRefresh(Boolean refresh) {

    }

    @Override
    public RecyclerView getRecyclerView() {
        return null;
    }

    @Override
    public LinearLayoutManager getLayoutManager() {
        return null;
    }
}
