package com.frxs.receipt.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;

import com.frxs.receipt.MyApplication;
import com.frxs.receipt.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/04/11
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class HomeActivity extends MyBaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        toolbar.setTitle("HomeActivity");
    }


    @OnClick(R.id.fab)
    public void onClick() {
        Snackbar.make(fab, "click     FloatingActionButton fab", Snackbar.LENGTH_LONG).show();
    }
}
