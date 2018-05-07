package com.jarry.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jarry.app.R;
import com.jarry.app.ui.activity.SendWeiBoActivity;

import butterknife.OnClick;

/**
 * 设置 Fragment
 */
public class ServiceFragment extends Fragment {


    public ServiceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service, container, false);
        initView(view);
        return view;
    }

    /**
     * @param view
     */
    private void initView(View view) {

    }


}
