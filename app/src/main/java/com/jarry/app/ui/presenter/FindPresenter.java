package com.jarry.app.ui.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jarry.app.App;
import com.jarry.app.R;
import com.jarry.app.adapter.FindListAdapter;
import com.jarry.app.base.BasePresenter;
import com.jarry.app.bean.Status;
import com.jarry.app.bean.UserComm;
import com.jarry.app.db.StatusDao;
import com.jarry.app.ui.activity.MainActivity;
import com.jarry.app.ui.view.IFindView;
import com.jarry.app.util.CommentFun;
import com.jarry.app.util.CustomTagHandler;
import com.jarry.app.util.PrefUtils;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.model.ConflictAlgorithm;

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


public class FindPresenter extends BasePresenter<IFindView> {

    private static final String TAG = "FindPresenter";

    private Context context;
    private IFindView homeView;
    private RecyclerView recyclerView;
    private FindListAdapter adapter;
    private List<Status> mList = new ArrayList<>();
    private List<Status> onePagelist = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private int lastVisibleItem;
    private boolean isLoadMore = false; // 是否加载过更多


    public FindPresenter(Context ctx) {
        this.context = ctx;
    }

    public void getWeiBoTimeLine() {
        homeView = getHomeView();
        if (homeView != null) {
            recyclerView = homeView.getRecyclerView();
            layoutManager = homeView.getLayoutManager();
            List<Status> cached = getPageOneDataFromDB();
            if (cached == null || cached.size() < 2)
                getPageOneDataFromNet();
            else {
                onePagelist = cached;
            }
            saveListToDB(onePagelist);
//            Log.e("TT", new Gson().toJson(onePagelist));
            ((Activity) context).runOnUiThread(() -> disPlayWeiBoList(onePagelist, context, homeView, recyclerView));
        }
    }

    private void saveListToDB(List<Status> onePagelist) {
        Gson gson = new Gson();
        for (Status status : onePagelist) {
            status.userStr = gson.toJson(status.getUser());
            status.mCommentStr = gson.toJson(status.mComment);
            status.likeUsersStr = gson.toJson(status.likeUsers);
        }
//        LiteOrm liteOrm = LiteOrm.newCascadeInstance(context, "find.db");
//        liteOrm.cascade().save(onePagelist);

        App.mDb.cascade().save(onePagelist);
//        for (Status status : onePagelist) {
//            App.mDb.insert(status);
//        }
        Log.e("TT", "= onePagelist.size()=" + onePagelist.size());

    }

    public void showSendWeibo(Status status) {
        mList.add(0, status);
        Gson gson = new Gson();
        status.userStr = gson.toJson(status.getUser());
        status.mCommentStr = gson.toJson(status.mComment);
        status.likeUsersStr = gson.toJson(status.likeUsers);
        //存储到本地数据库
        App.mDb.insert(status, ConflictAlgorithm.Replace);
        adapter.notifyDataSetChanged();
        StatusDao.save(status);
    }

    public void getMore() {
        homeView = getHomeView();
        if (homeView != null) {
            recyclerView = homeView.getRecyclerView();
            layoutManager = homeView.getLayoutManager();
            List<Status> more = getMoreData();
            App.mDb.cascade().save(more);
            disPlayWeiBoList(more, context, homeView, recyclerView);
        }
    }

    private List<Status> getMoreData() {
        if (onePagelist != null && onePagelist.size() > 0) {
            List<Status> res = deepCopy(onePagelist);
            return res;
        }
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

    private List<Status> getPageOneDataFromDB() {
        try {
            return StatusDao.getAll();
        } catch (Exception e) {
        }
        return null;
    }

    private List<Status> getPageOneDataFromNet() {
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

    private IFindView getHomeView() {
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
    private void disPlayWeiBoList(List<Status> list, Context context, IFindView homeView, RecyclerView recyclerView) {
        if (isLoadMore) {
            isLoadMore = false;
            if (max_id.equals("0")) {
                adapter.updateLoadStatus(adapter.LOAD_NONE);
                return;
            }
            mList.addAll(list);
            adapter.notifyDataSetChanged();
        } else {
            mList.clear();
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
                    inputComment(view, commentator);
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
    public void inputComment(final View v, UserComm receiver) {
        CommentFun.inputComment(((Activity) context), recyclerView, v, receiver, new CommentFun.InputCommentListener() {
            @Override
            public void onCommitComment() {
                adapter.notifyDataSetChanged();
            }
        });
    }

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

}
