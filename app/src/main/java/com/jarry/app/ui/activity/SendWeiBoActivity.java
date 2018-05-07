package com.jarry.app.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jarry.app.R;
import com.jarry.app.base.MVPBaseActivity;
import com.jarry.app.bean.Status;
import com.jarry.app.bean.User;
import com.jarry.app.ui.presenter.SendPresenter;
import com.jarry.app.ui.view.ISendView;
import com.jarry.app.util.DataUtil;
import com.jarry.app.util.StringUtil;
import com.jarry.app.util.ViewUtil;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import me.iwf.photopicker.PhotoPicker;


public class SendWeiBoActivity extends MVPBaseActivity<ISendView, SendPresenter> implements ISendView {

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;//

    @BindView(R.id.et_weibo)
    EditText et_weibo;
    @BindView(R.id.tv_weibo_number)
    TextView tv_weibo_number;
    @BindView(R.id.weibo_type)
    TextView weibo_type;
    @BindView(R.id.weibo_photo_grid)
    RecyclerView weibo_photo_grid;
    //表情
    @BindView(R.id.weibo_emotion)
    ImageView weibo_emotion;
    @BindView(R.id.ll_emotion_dashboard)
    LinearLayout ll_emotion_dashboard;
    @BindView(R.id.vp_emotion_dashboard)
    ViewPager vp_emotion_dashboard;


    ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected SendPresenter createPresenter() {
        return new SendPresenter(this);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_send_weibo;
    }

    @Override
    public boolean canBack() {
        return true;
    }

    private void initView() {
        toolbar = getSupportActionBar();
//        setHasOptionsMenu(true);
        toolbar.setTitle("发现");
        et_weibo.addTextChangedListener(StringUtil.textNumberListener(et_weibo, tv_weibo_number, this));
        weibo_photo_grid.setLayoutManager(new GridLayoutManager(this, 4));
        new ViewUtil(this, vp_emotion_dashboard, et_weibo).initEmotion();
    }

    @OnClick(R.id.weibo_photo)
    void pickphoto() {
        mPresenter.pickPhoto();
    }

    @OnClick(R.id.weibo_emotion)
    void insertEmotion() {
        //软键盘，显示或隐藏
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        if (ll_emotion_dashboard.getVisibility() == View.VISIBLE) {
            // 显示表情面板时点击,将按钮图片设为笑脸按钮,同时隐藏面板
            weibo_emotion.setImageResource(R.drawable.btn_insert_emotion);
            ll_emotion_dashboard.setVisibility(View.GONE);
            imm.showSoftInput(et_weibo, 0);
        } else {
            // 未显示表情面板时点击,将按钮图片设为键盘,同时显示面板
            weibo_emotion.setImageResource(R.drawable.btn_insert_keyboard);
            ll_emotion_dashboard.setVisibility(View.VISIBLE);
            imm.hideSoftInputFromWindow(et_weibo.getWindowToken(), 0);
        }
    }

    @OnClick(R.id.weibo_topic)
    void insertTopic() {
        int curPosition = et_weibo.getSelectionStart();
        StringBuilder sb = new StringBuilder(et_weibo.getText().toString());
        sb.insert(curPosition, "##");
        // 特殊文字处理,将表情等转换一下
        et_weibo.setText(sb);
        // 将光标设置到新增完表情的右侧
        et_weibo.setSelection(curPosition + 1);
    }

    @OnClick(R.id.weibo_type)
    void selectType() {
        //选择类型
        showPop();
    }

    String[] typeStrs = {"寻物招领", "兼职招聘信息", "二手交易", "校园新闻"};
    int typeIndex = -1;

    private void showPop() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)//设置标题的图片
                .setTitle("选择类型")//设置对话框的标题
                .setItems(typeStrs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        weibo_type.setText(typeStrs[which]);
                        typeIndex = which;
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    /**
     * 判断当前权限是否允许,弹出提示框来选择
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void PermissionToVerify() {
        // 需要验证的权限
        int hasWriteContactsPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            // 弹窗询问 ，让用户自己判断
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        mPresenter.photoPick();

    }

    /**
     * 用户进行权限设置后的回调函数 , 来响应用户的操作，无论用户是否同意权限，Activity都会
     * 执行此回调方法，所以我们可以把具体操作写在这里
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //获取图片
                    mPresenter.photoPick();
                } else {
                    Toast.makeText(SendWeiBoActivity.this, "权限没有开启", Toast.LENGTH_SHORT).show();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> listExtra =
                        data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                mPresenter.loadAdapter(listExtra);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.car_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_send:
                if (et_weibo.getText().toString().equals("")) {
                    Toast.makeText(this, R.string.not_null, Toast.LENGTH_SHORT).show();
                } else {
                    //设置微博刷新
                    String weiboText = et_weibo.getText().toString();
                    //自己拼接一个Bean
                    Status status = new Status();
                    status.setText(weiboText);
                    User user = new User();
                    user.setProfile_image_url("https://timgsa.baidu.com/timg?image\u0026quality\u003d80\u0026size\u003db10000_10000\u0026sec\u003d1525236570\u0026di\u003de3abeecb6e1208c7b6290adb5ee8f99f\u0026src\u003dhttp://img.taopic.com/uploads/allimg/110314/1517-110314134R642.jpg");
                    user.setName(User.getLoginUser().getName());
                    user.setScreen_name(User.getLoginUser().getName());
                    status.setUser(user);
                    if (typeIndex != -1) {
                        status.setSource(typeStrs[typeIndex]);
                    } else status.setSource(typeStrs[0]);
                    status.setCreated_at(DataUtil.convertGMTToLoacale(new Date()));
                    status.setAttitudes_count("0");
                    status.setComments_count("0");
                    status.setReposts_count("0");

                    //
                    ArrayList<Status.ThumbnailPic> pic_urls = null;
                    if (mPresenter.photos != null && mPresenter.photos.size() > 0) {
                        pic_urls = new ArrayList<>();
                        for (String path : mPresenter.photos) {
                            Status.ThumbnailPic thumbnailPic = new Status.ThumbnailPic();
                            thumbnailPic.localPic = path;
                            pic_urls.add(thumbnailPic);
                        }
                    }
                    status.setPic_urls(pic_urls);

                    Intent intent = new Intent();
                    intent.putExtra("weibo", status);
                    setResult(RESULT_OK, intent);
                    Toast.makeText(this, "发送成功", Toast.LENGTH_SHORT).show();
                    finish();
//                    mPresenter.sendWeiBo(et_weibo.getText().toString());
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finishAndToast() {
        finish();
        Toast.makeText(this, R.string.comment_succe, Toast.LENGTH_LONG).show();
    }

    @Override
    public void permissionSetting() {
        PermissionToVerify();
    }

    @Override
    public RecyclerView getPhotoGrid() {
        return weibo_photo_grid;
    }

}
