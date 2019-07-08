package com.frxs.receipt.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.frxs.core.utils.LogUtils;
import com.frxs.core.widget.BadgeView;
import com.frxs.core.widget.MaterialDialog;
import com.frxs.receipt.R;
import com.frxs.receipt.comms.DataGenerator;
import com.frxs.receipt.fragment.MineFragment;
import com.frxs.receipt.fragment.PendingRcvOrderFragment;
import com.frxs.receipt.fragment.ReceivedOrderListFragment;
import com.frxs.receipt.fragment.ReceivingOrderFragment;
import com.frxs.receipt.rest.model.AjaxParams;
import com.frxs.receipt.rest.model.ApiResponse;
import com.frxs.receipt.rest.service.SimpleCallback;
import com.honeywell.aidc.BarcodeReadEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/05/02
 *     desc   : 使用TabLayout + Fragment实现底部导航栏
 *     version: 1.0
 * </pre>
 */
public class BottomTabActivity extends BaseScanActivity {

    @BindView(R.id.home_container_layout)
    FrameLayout homeContainerLayout;
    @BindView(R.id.bottom_tab_layout)
    TabLayout bottomTabLayout;
//    private PendingRcvOrderFragment pendingRcvOrderListFragment;
    private ReceivingOrderFragment receivingOrderFragment;
    private ReceivedOrderListFragment receivedOrderListFragment;
    private MineFragment mineFragment;
    private BadgeView recptingBadgeView;

    private int receivingOrderCount = 0; //正在收货的订单数量

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_tab);
        ButterKnife.bind(this);
        LogUtils.e("onAttachFragment DataGenerator.getFragments");

        EventBus.getDefault().register(this);
        initViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventMainThread(Integer busEvent) {
        addReceivingOrderCount(busEvent);
    }

    private void initViews() {
        bottomTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                onTabItemSelected(tab.getPosition());

                for (int i = 0; i < bottomTabLayout.getTabCount(); i++) {
                    View view = bottomTabLayout.getTabAt(i).getCustomView();
                    ImageView icon = (ImageView) view.findViewById(R.id.tab_content_image);
                    TextView text = (TextView) view.findViewById(R.id.tab_content_text);
                    if (i == tab.getPosition()) {
                        icon.setImageResource(DataGenerator.mTabResPressed[i]);
                        text.setTextColor(ContextCompat.getColor(BottomTabActivity.this, R.color.themeColor));
                    } else {
                        icon.setImageResource(DataGenerator.mTabRes[i]);
                        text.setTextColor(ContextCompat.getColor(BottomTabActivity.this, android.R.color.darker_gray));
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // 提供自定义的布局添加Tab
        for (int i = 0; i < 3; i++) {
            View tabView =  DataGenerator.getTabView(this, i);
            if (i == 0) {
                View targetView = tabView.findViewById(R.id.target_view);
                recptingBadgeView = new BadgeView(this, targetView);
                recptingBadgeView.setId(R.id.home_tab_badgeview);
                recptingBadgeView.setTextSize(8);
                recptingBadgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                showBadgeView(receivingOrderCount);
            }
            bottomTabLayout.addTab(bottomTabLayout.newTab().setCustomView(tabView));
        }

        reqReceivingOrderNum();
    }

    private void onTabItemSelected(int position) {
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = null;
        switch (position) {
            /*case 0:
                if (null == pendingRcvOrderListFragment) {
                    pendingRcvOrderListFragment = PendingRcvOrderFragment.newInstance("CustomTabView Tab");
                }
                fragment = pendingRcvOrderListFragment;
                break;*/
            case 0:
                if (null == receivingOrderFragment) {
                    receivingOrderFragment = ReceivingOrderFragment.newInstance("CustomTabView Tab");
                }
                fragment = receivingOrderFragment;
                break;
            case 1:
                if (null == receivedOrderListFragment) {
                    receivedOrderListFragment = ReceivedOrderListFragment.newInstance("CustomTabView Tab");
                }
                fragment = receivedOrderListFragment;
                break;
            case 2:
                if (null == mineFragment) {
                    mineFragment = MineFragment.newInstance("CustomTabView Tab");
                }
                fragment = mineFragment;
                break;
            default:
                break;
        }
        if (fragment != null) {
            hideFragments(beginTransaction);

            if (!fragment.isAdded()) {
                beginTransaction.add(R.id.home_container_layout, fragment, String.valueOf(position));
            } else {
                beginTransaction.show(fragment);
            }

            beginTransaction.commitAllowingStateLoss();
        }
    }

    private void hideFragments(FragmentTransaction transaction) {
        /*if (null != pendingRcvOrderListFragment && pendingRcvOrderListFragment.isAdded()) {
            transaction.hide(pendingRcvOrderListFragment);
        }*/

        if (null != receivingOrderFragment && receivingOrderFragment.isAdded()) {
            transaction.hide(receivingOrderFragment);
        }

        if (null != receivedOrderListFragment && receivedOrderListFragment.isAdded()) {
            transaction.hide(receivedOrderListFragment);
        }

        if (null != mineFragment && mineFragment.isAdded()) {
            transaction.hide(mineFragment);
        }
    }

    public int getReceivingOrderCount() {
        return receivingOrderCount;
    }

    public void setReceivingOrderCount(int receivingOrderCount) {
        this.receivingOrderCount = receivingOrderCount;
        showBadgeView(receivingOrderCount);
    }

    public void addReceivingOrderCount(int num) {
        this.receivingOrderCount += num;
        showBadgeView(receivingOrderCount);
    }

    private void showBadgeView(int count) {
        if (count > 0) {
            if (count > 100) {
                recptingBadgeView.setText("99+");
            } else {
                recptingBadgeView.setText(String.valueOf(count));
            }
            recptingBadgeView.show();
        } else {
            recptingBadgeView.hide();
        }
    }

    private void reqReceivingOrderNum() {
        AjaxParams params = new AjaxParams();
        params.put("WID", getWID());
        params.put("ReceiveUserID", getUserID());
        getService().GetReceiveingOrderCount(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<Integer>>() {
            @Override
            public void onResponse(ApiResponse<Integer> result, int code, String msg) {
                if (result.getFlag().equals("0")) {
                    Integer num = result.getData();
                    if (null != num) {
                        setReceivingOrderCount(num.intValue());
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Integer>> call, Throwable t) {
            }
        });
    }

    private void showDialog() {
        final MaterialDialog materialDialog = new MaterialDialog(this);
        materialDialog.setMessage("是否退出应用？");
        materialDialog.setPositiveButton(R.string.confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        materialDialog.dismiss();
                        finish();
                        System.exit(0);
                    }
                }
        );
        materialDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        /*if (fragment instanceof HomeFragment) {
            pendingRcvOrderListFragment = (PendingRcvOrderFragment) fragment;
        }*/ if (fragment instanceof ReceivingOrderFragment) {
            receivingOrderFragment = (ReceivingOrderFragment) fragment;
        } else if (fragment instanceof ReceivedOrderListFragment) {
            receivedOrderListFragment = (ReceivedOrderListFragment) fragment;
        }else if (fragment instanceof MineFragment) {
            mineFragment = (MineFragment) fragment;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showDialog();// 应用程序退出对话框
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
           /* if (pendingRcvOrderListFragment != null && pendingRcvOrderListFragment.isVisible()) {
                pendingRcvOrderListFragment.onActivityResult(requestCode, resultCode, data);
            }*/
            if (receivingOrderFragment != null && receivingOrderFragment.isVisible()) {
                receivingOrderFragment.onActivityResult(requestCode, resultCode, data);
            }
            if (receivedOrderListFragment != null && receivedOrderListFragment.isVisible()) {
                receivedOrderListFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onSuccessEvent(BarcodeReadEvent event) {
        String barcodeData = event.getBarcodeData();
        if (!TextUtils.isEmpty(barcodeData)) {
            int currentPosition = bottomTabLayout.getSelectedTabPosition();
            Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(currentPosition));
            if (currentFragment instanceof PendingRcvOrderFragment) {
                ((PendingRcvOrderFragment)currentFragment).onSuccessEvent(barcodeData);
            } else if (currentFragment instanceof ReceivingOrderFragment) {
                ((ReceivingOrderFragment)currentFragment).onSuccessEvent(barcodeData);
            }
        }
    }

    @Override
    public void onSuccessEventStr(String event) {
        if (!TextUtils.isEmpty(event)) {
            int currentPosition = bottomTabLayout.getSelectedTabPosition();
            Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(currentPosition));
            if (currentFragment instanceof PendingRcvOrderFragment) {
                ((PendingRcvOrderFragment)currentFragment).onSuccessEvent(event);
            } else if (currentFragment instanceof ReceivingOrderFragment) {
                ((ReceivingOrderFragment)currentFragment).onSuccessEvent(event);
            }
        }
    }
}
