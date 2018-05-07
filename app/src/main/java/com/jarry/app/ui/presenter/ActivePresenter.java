package com.jarry.app.ui.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jarry.app.R;
import com.jarry.app.adapter.ActiveListAdapter;
import com.jarry.app.base.BasePresenter;
import com.jarry.app.bean.ActiveBean;
import com.jarry.app.ui.view.IActiveView;
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


public class ActivePresenter extends BasePresenter<IActiveView> {

    private static final String TAG = "FindPresenter";

    private Context context;
    private IActiveView homeView;
    private RecyclerView recyclerView;
    private ActiveListAdapter adapter;
    private List<ActiveBean> mList = new ArrayList<>();
    private List<ActiveBean> onePagelist = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private int lastVisibleItem;
    private boolean isLoadMore = false; // 是否加载过更多


    public ActivePresenter(Context ctx) {
        this.context = ctx;
    }

    public void getWeiBoTimeLine() {
        homeView = getHomeView();
        if (homeView != null) {
            recyclerView = homeView.getRecyclerView();
            layoutManager = homeView.getLayoutManager();
            getPageOneData();
            ((Activity) context).runOnUiThread(() -> disPlayWeiBoList(onePagelist, context, homeView, recyclerView));
        }
    }

    public void getMore() {
        homeView = getHomeView();
        if (homeView != null) {
            recyclerView = homeView.getRecyclerView();
            layoutManager = homeView.getLayoutManager();
            List<ActiveBean> more = getMoreData();
            disPlayWeiBoList(more, context, homeView, recyclerView);
        }
    }

    private List<ActiveBean> getMoreData() {
        if (onePagelist != null && onePagelist.size() > 0) {
            List<ActiveBean> res = deepCopy(onePagelist);
            Log.e(TAG, "RES========>" + res);
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

    private List<ActiveBean> getPageOneData() {
        if (onePagelist != null && onePagelist.size() > 0) {
            return onePagelist;
        }
        try {
            InputStream inputStream = context.getAssets().open("activelist.json");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                stringBuilder.append(str);
            }
            String res = stringBuilder.toString();
            return onePagelist = new Gson().fromJson(res, new TypeToken<List<ActiveBean>>() {
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

    private IActiveView getHomeView() {
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
    private void disPlayWeiBoList(List<ActiveBean> list, Context context, IActiveView homeView, RecyclerView recyclerView) {
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
            adapter = new ActiveListAdapter(context, mList);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        homeView.setDataRefresh(false);
        // save max_id
//        PrefUtils.setString(context, "max_id", friendsTimeLine.getMax_id());
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
