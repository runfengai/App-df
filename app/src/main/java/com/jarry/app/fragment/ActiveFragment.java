package com.jarry.app.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jarry.app.R;
import com.jarry.app.base.MVPBaseFragment;
import com.jarry.app.ui.activity.CreateOrgActivity;
import com.jarry.app.ui.activity.MainActivity;
import com.jarry.app.ui.activity.SendWeiBoActivity;
import com.jarry.app.ui.presenter.ActivePresenter;
import com.jarry.app.ui.presenter.FindPresenter;
import com.jarry.app.ui.view.IActiveView;
import com.jarry.app.ui.view.IFindView;
import com.jarry.app.util.RxBus;
import com.jarry.app.util.RxEvents;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Fragment
 */
public class ActiveFragment extends MVPBaseFragment<IActiveView, ActivePresenter> implements IActiveView {

    public ActiveFragment() {
        // Required empty public constructor
    }

    private LinearLayoutManager mLayoutManager;

    @BindView(R.id.content_list)
    RecyclerView mRecyclerView;

    @Override
    protected ActivePresenter createPresenter() {
        return new ActivePresenter(getContext());
    }

    @Override
    protected int createViewLayoutId() {
        return R.layout.fragment_active;
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
        menu.clear();
        inflater.inflate(R.menu.active_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_create_orz:
                startActivity(new Intent(getActivity(), CreateOrgActivity.class));
//                Toast.makeText(getActivity(), "创建成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_search_orz:
                showSearch();
//                Toast.makeText(getActivity(), "搜索不到", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSearch() {
        LayoutInflater factory = LayoutInflater.from(getActivity());//提示框
        final View view = factory.inflate(R.layout.dialog_edit, null);//这里必须是final的
        final EditText edit = (EditText) view.findViewById(R.id.editText);//获得输入框对象

        new AlertDialog.Builder(getActivity())
                .setTitle("搜索组织")//提示框标题
                .setView(view)
                .setPositiveButton("确定",//提示框的两个按钮
                        (dialog, which) -> {
                            //事件
                            //
                            String orzName = edit.getText().toString();
                            search(orzName);
                        }).setNegativeButton("取消", null).create().show();
    }

    //搜索组织
    private void search(String orzName) {
        requestDataRefresh();
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
