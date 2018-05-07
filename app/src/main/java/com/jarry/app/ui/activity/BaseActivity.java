package com.jarry.app.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jarry.app.App;


/**
 * Created by Jarry on 2018/2/25.
 */

public class BaseActivity extends AppCompatActivity {
    protected BaseActivity self;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        App.addAct(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        self = null;
        App.removeAct(this);
    }
}
