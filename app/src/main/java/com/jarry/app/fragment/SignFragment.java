package com.jarry.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.jarry.app.R;
import com.jarry.app.base.MVPBaseFragment;
import com.jarry.app.ui.activity.MainActivity;
import com.jarry.app.ui.activity.SendWeiBoActivity;
import com.jarry.app.ui.presenter.SignPresenter;
import com.jarry.app.ui.view.ISignView;
import com.jarry.app.util.RxBus;
import com.jarry.app.util.RxEvents;

import butterknife.BindView;
import butterknife.OnClick;

/**
 */
public class SignFragment extends MVPBaseFragment<ISignView, SignPresenter> implements ISignView {

    public SignFragment() {
        // Required empty public constructor
    }


    private LinearLayoutManager mLayoutManager;

    @BindView(R.id.content_list)
    RecyclerView mRecyclerView;

    //发校园风景
    @OnClick(R.id.send)
    void send() {
        startActivity(new Intent(getActivity(), SendWeiBoActivity.class));
    }

    @Override
    protected SignPresenter createPresenter() {
        return new SignPresenter(getContext());
    }

    @Override
    protected int createViewLayoutId() {
        return R.layout.fragment_sign;
    }

    ActionBar actionBar;

    @Override
    protected void initView(View rootView) {
        setHasOptionsMenu(true);
        actionBar = ((MainActivity) getActivity()).getSupportActionBar();

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        menu.clear();
//        inflater.inflate(R.menu.active_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_create_orz:
//                startActivity(new Intent(getActivity(), CreateOrgActivity.class));
////                Toast.makeText(getActivity(), "创建成功", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.menu_search_orz:
//                Toast.makeText(getActivity(), "搜索不到", Toast.LENGTH_SHORT).show();
//                break;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDataRefresh(true);
        mPresenter.getWeiBoTimeLine();
        mPresenter.scrollRecycleView();

        RxBus.getInstance().toObserverable().subscribe(event -> {
            if (event instanceof RxEvents.UpRefreshClick) {
                mRecyclerView.smoothScrollToPosition(0);
                requestDataRefresh();
            } else if (event instanceof RxEvents.WeiBoSetLike) {

            }
        });
    }

    @Override
    public void requestDataRefresh() {
        super.requestDataRefresh();
        setDataRefresh(true);
        mPresenter.getWeiBoTimeLine();
    }

    @Override
    public void setDataRefresh(Boolean refresh) {
        setRefresh(refresh);
    }

    @Override
    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public LinearLayoutManager getLayoutManager() {
        return mLayoutManager;
    }

}
