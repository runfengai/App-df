package com.jarry.app.ui.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jarry.app.R;
import com.jarry.app.adapter.FindListAdapter;
import com.jarry.app.base.BasePresenter;
import com.jarry.app.bean.Comments;
import com.jarry.app.bean.FriendsTimeLine;
import com.jarry.app.bean.MentionComment;
import com.jarry.app.bean.Status;
import com.jarry.app.bean.UserComm;
import com.jarry.app.ui.activity.MainActivity;
import com.jarry.app.ui.view.IFindView;
import com.jarry.app.ui.view.IMessageFgView;
import com.jarry.app.util.CommentFun;
import com.jarry.app.util.CustomTagHandler;
import com.jarry.app.util.PrefUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jarry.app.db.StatusDao.getComments;
import static com.jarry.app.db.StatusDao.getLike;
import static com.jarry.app.db.StatusDao.getMy;

/**
 * Created by Jarry 2016/8/11.
 */
public class MessageFgPresenter extends BasePresenter<IMessageFgView> {

    private static final String TAG = "FindPresenter";

    private Context context;
    private IMessageFgView homeView;
    private RecyclerView recyclerView;
    private FindListAdapter adapter;
    private List<Status> mList = new ArrayList<>();
    private List<Status> onePagelist = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private int lastVisibleItem;
    private boolean isLoadMore = false; // 是否加载过更多


    public MessageFgPresenter(Context ctx) {
        this.context = ctx;
    }

    public void getWeiBoTimeLine() {
        homeView = getHomeView();
        if (homeView != null) {
            recyclerView = homeView.getRecyclerView();
            layoutManager = homeView.getLayoutManager();
            getPageOneData();
//            Log.e("TT", new Gson().toJson(onePagelist));
            ((Activity) context).runOnUiThread(() -> disPlayWeiBoList(onePagelist, context, homeView, recyclerView));
        }
    }


    public void getMore() {
        isLoadMore = true;
        homeView = getHomeView();
        if (homeView != null) {
            recyclerView = homeView.getRecyclerView();
            layoutManager = homeView.getLayoutManager();
            List<Status> more = getMoreData();
            disPlayWeiBoList(more, context, homeView, recyclerView);
        }
    }

    private List<Status> getMoreData() {
        return null;
    }

    public static <T> List<T> deepCopy(List<T> src) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(byteOut);
            out.writeObject(src);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            List<T> dest = (List<T>) in.readObject();
            return dest;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private List<Status> getPageOneData() {
        if (onePagelist != null && onePagelist.size() > 0) {
            return onePagelist;
        }
        try {
            InputStream inputStream = context.getAssets().open("findlist.json");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                stringBuilder.append(str);
            }
            String res = stringBuilder.toString();
            return onePagelist = new Gson().fromJson(res, new TypeToken<List<Status>>() {
            }.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 点赞POST请求
    public void setLike(String id) {
//        Oauth2AccessToken token = readToken(context);
//        weiBoApi.setLike(getLikeMap(token.getToken(), id))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(postComments -> {
//
//                });
    }

    private IMessageFgView getHomeView() {
        if (isViewAttached()) {
            return getView();
        } else {
            return null;
        }
    }

    private void loadError(Throwable throwable) {
        throwable.printStackTrace();
        Toast.makeText(context, R.string.load_error, Toast.LENGTH_SHORT).show();
        if (homeView != null) {
            homeView.setDataRefresh(false);
        }
    }

//    private Oauth2AccessToken readToken(Context context) {
//        return AccessTokenKeeper.readAccessToken(context);
//    }

    String max_id = "";

    // get request params
    private Map<String, Object> getRequestMap(String token, String count) {
        max_id = PrefUtils.getString(context, "max_id", "0");
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", token);
        map.put("count", count);
        if (isLoadMore) {
            map.put("max_id", Long.valueOf(max_id));
        }
        return map;
    }

    private Map<String, Object> getLikeMap(String token, String id) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", token);
        map.put("attitude", "simle");
        map.put("id", id);
        return map;
    }

    // refresh data
    private void disPlayWeiBoList(List<Status> list, Context context, IMessageFgView homeView, RecyclerView recyclerView) {
        if (isLoadMore) {
            if (list != null) {
                mList.addAll(list);
            } else {
                adapter.updateLoadStatus(adapter.LOAD_NONE);
                return;
            }
            adapter.notifyDataSetChanged();
        } else {
            mList.clear();
            if (list != null)
                mList.addAll(list);
            adapter = new FindListAdapter(context, mList, "home_fg", new CustomTagHandler(context, new CustomTagHandler.OnCommentClickListener() {
                @Override
                public void onCommentatorClick(View view, UserComm commentator) {
                    Toast.makeText(context, commentator.mName, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onReceiverClick(View view, UserComm receiver) {
                    Toast.makeText(context, receiver.mName, Toast.LENGTH_SHORT).show();
                }

                // 点击评论内容，弹出输入框回复评论
                @Override
                public void onContentClick(View view, UserComm commentator, UserComm receiver) {
                    if (commentator != null && commentator.mId == MainActivity.sUser.mId) { // 不能回复自己的评论
                        return;
                    }
//                    inputComment(view, commentator);
                }
            }), recyclerView);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        homeView.setDataRefresh(false);
        // save max_id
//        PrefUtils.setString(context, "max_id", friendsTimeLine.getMax_id());
    }

    /**
     * 弹出评论对话框
     *
     * @param v
     * @param receiver
     */
//    public void inputComment(final View v, UserComm receiver) {
//        CommentFun.inputComment(((Activity) context), recyclerView, v, receiver, new CommentFun.InputCommentListener() {
//            @Override
//            public void onCommitComment() {
//                adapter.notifyDataSetChanged();
//            }
//        });
//    }

    /**
     * recyclerView Scroll listener , maybe in here is wrong ?
     */

    public void scrollRecycleView() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    lastVisibleItem = layoutManager
                            .findLastVisibleItemPosition();
                    if (lastVisibleItem + 1 == layoutManager
                            .getItemCount()) {
                        adapter.updateLoadStatus(adapter.LOAD_PULL_TO);
                        isLoadMore = true;
                        adapter.updateLoadStatus(adapter.LOAD_MORE);
                        new Handler().postDelayed(() -> getMore(), 1000);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
    }

    public void getMessageActive() {
        isLoadMore = false;
        homeView = getHomeView();
        if (homeView != null) {
            recyclerView = homeView.getRecyclerView();
            layoutManager = homeView.getLayoutManager();
//            getPageOneData();
            onePagelist = getMy();
            ((Activity) context).runOnUiThread(() -> disPlayWeiBoList(onePagelist, context, homeView, recyclerView));
        }
    }

    public void getMessageLikeComment() {
        homeView = getHomeView();
        if (homeView != null) {
            recyclerView = homeView.getRecyclerView();
            layoutManager = homeView.getLayoutManager();
            onePagelist = getLike();
            ((Activity) context).runOnUiThread(() -> disPlayWeiBoList(onePagelist, context, homeView, recyclerView));
        }
    }

    public void getMessageSendComment() {
        homeView = getHomeView();
        if (homeView != null) {
            recyclerView = homeView.getRecyclerView();
            layoutManager = homeView.getLayoutManager();
            onePagelist = getComments();
            ((Activity) context).runOnUiThread(() -> disPlayWeiBoList(onePagelist, context, homeView, recyclerView));
        }
    }
}
