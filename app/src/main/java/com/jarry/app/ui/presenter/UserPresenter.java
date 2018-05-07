package com.jarry.app.ui.presenter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import com.jarry.app.R;
import com.jarry.app.base.BasePresenter;
import com.jarry.app.ui.view.IUserView;


public class UserPresenter extends BasePresenter<IUserView> {

    private Context context;
    private IUserView userView;
    private LinearLayoutManager layoutManager;
    private int lastVisibleItem;
    private boolean isLoadMore = false; // 是否加载过更多

    public UserPresenter(Context context) {
        this.context = context;
    }

    public void getUserWeiBoTimeLine(String uid) {
        userView = getView();
    }

    private void loadError(Throwable throwable) {
        throwable.printStackTrace();
        Toast.makeText(context, R.string.load_error, Toast.LENGTH_SHORT).show();
        if (userView != null) {
            userView.setDataRefresh(false);
        }
    }
}
