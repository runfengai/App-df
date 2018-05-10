package com.jarry.app.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jarry.app.App;
import com.jarry.app.R;
import com.jarry.app.base.MVPBaseActivity;
import com.jarry.app.bean.Comments;
import com.jarry.app.bean.Status;
import com.jarry.app.bean.User;
import com.jarry.app.ui.presenter.CARPresenter;
import com.jarry.app.ui.view.ICARView;
import com.jarry.app.util.DataUtil;
import com.jarry.app.util.RxBus;
import com.jarry.app.util.RxEvents;
import com.jarry.app.util.StringUtil;
import com.jarry.app.util.ViewUtil;
import com.litesuits.orm.db.model.ConflictAlgorithm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 评论和转发界面
 */
public class CommentAndRepostActivity extends MVPBaseActivity<ICARView, CARPresenter> implements ICARView {

    private static final String WEIBO_STATUE = "status";
    private static final String COMMENT = "comment";
    private static final String TAG = "tag";

    private Status status;
    private Status statusRetreed;
    private String weibo_id;
    private Comments comments;
    private String comment_id;
    private String tag;

    @BindView(R.id.et_comment_repost)
    EditText et_comment_repost;
    @BindView(R.id.tv_weibo_number)
    TextView tv_weibo_number;
    @BindView(R.id.cb_is_repost)
    CheckBox cb_is_repost;

    //表情
    @BindView(R.id.weibo_emotion)
    ImageView weibo_emotion;
    @BindView(R.id.ll_emotion_dashboard)
    LinearLayout ll_emotion_dashboard;
    @BindView(R.id.vp_emotion_dashboard)
    ViewPager vp_emotion_dashboard;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected CARPresenter createPresenter() {
        return new CARPresenter(this);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_c_and_r;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseIntent();
        toolbar.setTitle(tag);
        setSupportActionBar(toolbar);
        initView();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.login_menu, menu);
//        return true;
//    }

    public static Intent newIntent(Context context, Status status, String tag, Comments comments) {
        Intent intent = new Intent(context, CommentAndRepostActivity.class);
        intent.putExtra(CommentAndRepostActivity.WEIBO_STATUE, status);
        intent.putExtra(CommentAndRepostActivity.COMMENT, comments);
        intent.putExtra(CommentAndRepostActivity.TAG, tag);
        return intent;
    }

    /**
     * 得到Intent传递的数据
     */
    private void parseIntent() {
        status = (Status) getIntent().getSerializableExtra(WEIBO_STATUE);
        statusRetreed = (Status) deepClone(status);
        Log.e("rettt", new Gson().toJson(statusRetreed));
        weibo_id = status.getIdstr();
        tag = getIntent().getStringExtra(TAG);
        if (tag.equals("回复微博")) {
            cb_is_repost.setText("评论并转发");
        } else if (tag.equals("转发微博")) {
            cb_is_repost.setText("转发并评论");
        } else if (tag.equals("回复评论")) {
            comments = (Comments) getIntent().getSerializableExtra(COMMENT);
            comment_id = comments.getIdstr();
            cb_is_repost.setText("同时转发到微博");
        }
    }

//    public Object deepClone(Object obj) throws IOException, OptionalDataException,
//            ClassNotFoundException {
//        // 将对象写到流里
//        ByteArrayOutputStream bo = new ByteArrayOutputStream();
//        ObjectOutputStream oo = new ObjectOutputStream(bo);
//        oo.writeObject(obj);
//        // 从流里读出来
//        ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
//        ObjectInputStream oi = new ObjectInputStream(bi);
//        return (oi.readObject());
//    }

    public static Object deepClone(Object src) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(byteOut);
            out.writeObject(src);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            Object dest = in.readObject();
            return dest;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private void initView() {
        et_comment_repost.addTextChangedListener(StringUtil.textNumberListener(et_comment_repost, tv_weibo_number, this));
        et_comment_repost.clearFocus();
        new ViewUtil(this, vp_emotion_dashboard, et_comment_repost).initEmotion();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.car_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_send) {
            commentAndRepost();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean canBack() {
        return true;
    }

    @Override
    public void finishAndToast() {
        finish();
        Toast.makeText(this, R.string.comment_succe, Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.weibo_emotion)
    void insertEmotion() {
        //软键盘，显示或隐藏
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        if (ll_emotion_dashboard.getVisibility() == View.VISIBLE) {
            // 显示表情面板时点击,将按钮图片设为笑脸按钮,同时隐藏面板
            weibo_emotion.setImageResource(R.drawable.btn_insert_emotion);
            ll_emotion_dashboard.setVisibility(View.GONE);
        } else {
            // 未显示表情面板时点击,将按钮图片设为键盘,同时显示面板
            weibo_emotion.setImageResource(R.drawable.btn_insert_keyboard);
            ll_emotion_dashboard.setVisibility(View.VISIBLE);
            et_comment_repost.clearFocus();
        }
    }

    @OnClick(R.id.weibo_topic)
    void insertTopic() {
        int curPosition = et_comment_repost.getSelectionStart();
        StringBuilder sb = new StringBuilder(et_comment_repost.getText().toString());
        sb.insert(curPosition, "##");
        // 特殊文字处理,将表情等转换一下
        et_comment_repost.setText(sb);
        // 将光标设置到新增完表情的右侧
        et_comment_repost.setSelection(curPosition + 1);
    }

    private void commentAndRepost() {
        String text = et_comment_repost.getText().toString();
        if (text.equals("")) {
            Toast.makeText(this, R.string.not_null, Toast.LENGTH_LONG).show();
        } else {
            status.setText(text);
            status.setUser(App.getUser());
            status.setRetweeted_status(statusRetreed);
            Log.e("ret", new Gson().toJson(statusRetreed));
            status.setCreated_at(DataUtil.convertGMTToLoacale(new Date()));
            status.setAttitudes_count("0");
            status.setComments_count("0");
            status.setReposts_count("0");

            status.setSource("转发");
            status.setCreated_at(DataUtil.convertGMTToLoacale(new Date()));
            status.setAttitudes_count("0");
            status.setComments_count("0");
            status.setReposts_count("0");
            Toast.makeText(this, tag + "成功！", Toast.LENGTH_LONG).show();
            //转发微博
            status.setRetweeted_statusStr();
            App.mDb.insert(status, ConflictAlgorithm.Replace);
            Intent intent = new Intent();
//            intent.putExtra("weibo", status);
//            setResult(RESULT_OK, intent);
            RxBus.getInstance().send(new RxEvents.UpRefreshClick());
            finish();
//            if (tag.equals("回复微博")) {
//                mPresenter.postComment(text, weibo_id);
//                if (cb_is_repost.isChecked()) {
//                    mPresenter.postRepost(getRepostText(text), weibo_id);
//                }
//            } else if (tag.equals("转发微博")) {
//                mPresenter.postRepost(getRepostText(text), weibo_id);
//                if (cb_is_repost.isChecked()) {
//                    mPresenter.postComment(text, weibo_id);
//                }
//            } else if (tag.equals("回复评论")) {
//                mPresenter.postCommentToReply(text, comment_id, weibo_id);
//                if (cb_is_repost.isChecked()) {
//                    mPresenter.postRepost(getCommentReplyText(text), weibo_id);
//                }
//            }
        }
    }

    private String getRepostText(String text) {
        if (status.getRetweeted_status() != null) {
            String repostText = text + "//@" + status.getUser().getScreen_name() + ":" + status.getText();
            return repostText;
        } else {
            return text;
        }
    }

    private String getCommentReplyText(String text) {
        String repostText = "回复" + "@" + comments.getUser().getScreen_name() + ":" + text + "//" + "@" + comments.getUser().getScreen_name() + ":" + comments.getText();
        return repostText;
    }

}
