package com.jarry.app.ui.presenter;

import android.content.Context;
import android.widget.Toast;

import com.jarry.app.R;
import com.jarry.app.base.BasePresenter;
import com.jarry.app.ui.view.ICARView;

import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class CARPresenter extends BasePresenter<ICARView> {

    private Context context;

    public CARPresenter(Context context) {
        this.context = context;
    }

    public void postComment(String text, String id) {

    }

    public void postRepost(String text, String id) {

    }

    public void postCommentToReply(String text, String comment_id, String weibo_id) {

    }

    private void loadError(Throwable throwable) {
        throwable.printStackTrace();
        Toast.makeText(context, R.string.load_error, Toast.LENGTH_SHORT).show();
    }

    // get request params
    private Map<String, Object> getCommentMap(String token, String comment, String id) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", token);
        map.put("comment", comment);
        map.put("id", id);
        return map;
    }

    private Map<String, Object> getRepostMap(String token, String comment, String id) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", token);
        map.put("status", comment);
        map.put("id", id);
        return map;
    }

    private Map<String, Object> getCommentToReplyMap(String token, String comment, String comment_id, String weibo_id) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", token);
        map.put("comment", comment);
        map.put("id", weibo_id);
        map.put("cid", comment_id);
        return map;
    }


}
