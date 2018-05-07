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
import com.jarry.app.adapter.SignListAdapter;
import com.jarry.app.base.BasePresenter;
import com.jarry.app.bean.SignBean;
import com.jarry.app.ui.view.ISignView;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


public class SignPresenter extends BasePresenter<ISignView> {

    private static final String TAG = "FindPresenter";

    private Context context;
    private ISignView homeView;
    private RecyclerView recyclerView;
    private SignListAdapter adapter;
    private List<SignBean> mList = new ArrayList<>();
    private List<SignBean> onePagelist = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private int lastVisibleItem;
    private boolean isLoadMore = false; // 是否加载过更多


    public SignPresenter(Context ctx) {
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
            List<SignBean> more = getMoreData();
            disPlayWeiBoList(more, context, homeView, recyclerView);
        }
    }

    private List<SignBean> getMoreData() {
        if (onePagelist != null && onePagelist.size() > 0) {
            List<SignBean> res = deepCopy(onePagelist);
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

    private List<SignBean> getPageOneData() {
        if (onePagelist != null && onePagelist.size() > 0) {
            return onePagelist;
        }
        try {
            InputStream inputStream = context.getAssets().open("signlist.json");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                stringBuilder.append(str);
            }
            String res = stringBuilder.toString();
            return onePagelist = new Gson().fromJson(res, new TypeToken<List<SignBean>>() {
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

    private ISignView getHomeView() {
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

    String max_id = "";

    // refresh data
    private void disPlayWeiBoList(List<SignBean> list, Context context, ISignView homeView, RecyclerView recyclerView) {
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
            adapter = new SignListAdapter(context, mList);
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
