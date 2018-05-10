package com.jarry.app.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jarry.app.App;
import com.jarry.app.R;
import com.jarry.app.adapter.ViewPagerFgAdapter;
import com.jarry.app.base.MVPBaseFragment;
import com.jarry.app.bean.Status;
import com.jarry.app.bean.User;
import com.jarry.app.db.UserDao;
import com.jarry.app.ui.activity.CreateOrgActivity;
import com.jarry.app.ui.activity.LoginActivity;
import com.jarry.app.ui.presenter.UserPresenter;
import com.jarry.app.ui.view.IUserView;
import com.jarry.app.util.PrefUtils;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.model.ConflictAlgorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import me.iwf.photopicker.PhotoPicker;

import static android.app.Activity.RESULT_OK;

/**
 *
 */
public class MyFragment extends MVPBaseFragment<IUserView, UserPresenter> implements IUserView {

    public MyFragment() {
        // Required empty public constructor
    }

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

    @BindView(R.id.message_tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.message_viewPager)
    ViewPager mViewPager;

    @Override
    protected void initView(View rootView) {
        initUserInfo();
        initTabView();
    }

    @Override
    protected UserPresenter createPresenter() {
        return new UserPresenter(getContext());
    }

    @Override
    protected int createViewLayoutId() {
        return R.layout.activity_user;
    }

    private User user = new User();

    private void initUserInfo() {
        user = getUser();
        userName.setText(user.getScreen_name());
        tv_user_desc.setText(user.getDescription());
//        tv_user_friends_count.setText("关注 " + user.getFriends_count());
//        tv_user_flowers_count.setText("粉丝 " + user.getFollowers_count());
        Glide.with(this).load(user.getAvatar_hd()).centerCrop().into(iv_user_icon);

    }


    @OnClick(R.id.tv_user_name)
    public void editName() {
        LayoutInflater factory = LayoutInflater.from(getActivity());//提示框
        final View view = factory.inflate(R.layout.dialog_edit, null);//这里必须是final的
        final EditText edit = (EditText) view.findViewById(R.id.editText);//获得输入框对象
        edit.setText(userName.getText().toString());
        new AlertDialog.Builder(getActivity())
                .setTitle("编辑名字")//提示框标题
                .setView(view)
                .setPositiveButton("确定",//提示框的两个按钮
                        (dialog, which) -> {
                            //事件
                            //
                            String orzName = edit.getText().toString();
                            userName.setText(orzName);
                            //存库
                            user = getUser();
                            App.mDb.delete(user);
                            user.setScreen_name(orzName);
                            //保存
                            App.mDb.insert(user, ConflictAlgorithm.Replace);

                        }).setNegativeButton("取消", null).create().show();
    }

    @OnClick(R.id.tv_user_desc)
    public void editDesc() {
        LayoutInflater factory = LayoutInflater.from(getActivity());//提示框
        final View view = factory.inflate(R.layout.dialog_edit, null);//这里必须是final的
        final EditText edit = (EditText) view.findViewById(R.id.editText);//获得输入框对象
        edit.setText(tv_user_desc.getText().toString());

        new AlertDialog.Builder(getActivity())
                .setTitle("编辑个性签名")//提示框标题
                .setView(view)
                .setPositiveButton("确定",//提示框的两个按钮
                        (dialog, which) -> {
                            //事件
                            //
                            String orzName = edit.getText().toString();
                            tv_user_desc.setText(orzName);
                            user = getUser();
                            App.mDb.delete(user);
                            user.setDescription(orzName);
                            //保存
                            App.mDb.insert(user, ConflictAlgorithm.Replace);
                        }).setNegativeButton("取消", null).create().show();
    }

    //适配6.0，权限申请
    private static final int PERMISSION_READ_DATA = 222;

    @OnClick(R.id.iv_user_icon)
    public void selectPic() {

        //
        //photo pick
        //先检查权限，如果没有权限，就关闭
        if (Build.VERSION.SDK_INT >= 23) {//权限检查
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_DATA);
                return;
            } else {
                photoPick();
            }
        } else {
            photoPick();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            //就像onActivityResult一样这个地方就是判断你是从哪来的。
            case PERMISSION_READ_DATA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    photoPick();
                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), "读取SD卡权限被禁用。。", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void photoPick() {
        PhotoPicker.builder()
                .setPhotoCount(1)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(false)
                .start((Activity) getActivity(), this, PhotoPicker.REQUEST_CODE);
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


    private User getUser() {

        String userSp = PrefUtils.getString(getActivity(), "userId", "");

        QueryBuilder<User> qb = new QueryBuilder<>(User.class);
        qb.whereEquals("id", userSp);
        List<User> users = App.mDb.query(qb);
        if (users == null) return User.getLoginUser();
        return users.get(0);
//        try {
//            InputStream inputStream = getActivity().getAssets().open("user.json");
//            BufferedReader reader = new BufferedReader(
//                    new InputStreamReader(inputStream));
//            StringBuilder stringBuilder = new StringBuilder();
//            String str;
//            while ((str = reader.readLine()) != null) {
//                stringBuilder.append(str);
//            }
//            String res = stringBuilder.toString();
//            return user = new Gson().fromJson(res, new TypeToken<User>() {
//            }.getType());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> listExtra =
                        data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                if (listExtra != null && listExtra.size() > 0) {
                    Glide.with(this).load(new File(listExtra.get(0))).centerCrop().into(iv_user_icon);
                }
            }
        }
    }

    private List<MVPBaseFragment> fragmentList;

    //初始化Tab滑动
    public void initTabView() {

        fragmentList = new ArrayList<>();
        fragmentList.add(MessageFragment.newInstance("my_active"));
        fragmentList.add(MessageFragment.newInstance("get_like"));
        fragmentList.add(MessageFragment.newInstance("send_comment"));

        mViewPager.setAdapter(new ViewPagerFgAdapter(getChildFragmentManager(), fragmentList, "Message"));//给ViewPager设置适配器
        tabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_exit:
                getActivity().finish();
                App.exit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
