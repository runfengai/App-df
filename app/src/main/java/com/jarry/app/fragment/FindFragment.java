package com.jarry.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jarry.app.R;
import com.jarry.app.base.MVPBaseFragment;
import com.jarry.app.bean.Status;
import com.jarry.app.bean.UserComm;
import com.jarry.app.ui.activity.SendWeiBoActivity;
import com.jarry.app.ui.presenter.FindPresenter;
import com.jarry.app.ui.view.IActiveView;
import com.jarry.app.ui.view.IFindView;
import com.jarry.app.util.CommentFun;
import com.jarry.app.util.RxBus;
import com.jarry.app.util.RxEvents;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * @Author jarry
 * created at 2018/5/1 13:25
 */

public class FindFragment extends MVPBaseFragment<IFindView, FindPresenter> implements IFindView {

    public FindFragment() {
        // Required empty public constructor
    }

    private LinearLayoutManager mLayoutManager;
    public static final int SEND_REQ_CODE = 100;
    @BindView(R.id.content_list)
    RecyclerView mRecyclerView;

    @Override
    protected FindPresenter createPresenter() {
        return new FindPresenter(getContext());
    }

    @Override
    protected int createViewLayoutId() {
        return R.layout.fragment_find;
    }

    @Override
    protected void initView(View rootView) {
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
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
                RxEvents.WeiBoSetLike like = (RxEvents.WeiBoSetLike) event;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case SEND_REQ_CODE:
                    Status status = (Status) data.getSerializableExtra("weibo");
                    mPresenter.showSendWeibo(status);
                    break;
            }
        }
    }

    //发微博
    @OnClick(R.id.send_weibo)
    void sendWeibo() {
        startActivityForResult(new Intent(getActivity(), SendWeiBoActivity.class), SEND_REQ_CODE);
    }

}
