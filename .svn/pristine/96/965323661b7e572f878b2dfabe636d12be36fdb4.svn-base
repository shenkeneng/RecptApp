package com.frxs.receipt.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.frxs.core.utils.ToastUtils;
import com.frxs.core.widget.BadgeView;
import com.frxs.core.widget.EmptyView;
import com.frxs.receipt.R;
import com.frxs.receipt.activity.BottomTabActivity;
import com.frxs.receipt.activity.CaptureActivity;
import com.frxs.receipt.activity.MyBaseActivity;
import com.frxs.receipt.activity.SearchResultRcvProductActivity;
import com.frxs.receipt.comms.GlobelDefines;
import com.frxs.receipt.greendao.DBHelper;
import com.frxs.receipt.greendao.entity.ProductEntity;
import com.frxs.receipt.model.PendingOrder;
import com.frxs.receipt.model.ProductInfo;
import com.frxs.receipt.rest.model.ApiResponse;
import com.frxs.receipt.rest.service.RequestListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/06/07
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class ReceivingFragment extends MyBaseFragment {
    public static final String TAG = ReceivingFragment.class.getSimpleName();
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.search_tv)
    TextView searchTv;
    @BindView(R.id.empty_view)
    EmptyView emptyView;
    private ReceivingOrderFragment orderFragment;
    private ReceivingProductFragment productFragment;
    private List<PendingOrder.ItemsBean> orderList = new ArrayList<>();
    private List<ProductEntity> productEntityList = new ArrayList<>();
    private List<BadgeView> mBadgeViews = new ArrayList<>();

    public static ReceivingFragment newInstance(String argument) {
        Bundle bundle = new Bundle();
        bundle.putString(TAG, argument);
        ReceivingFragment contentFragment = new ReceivingFragment();
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_receiving;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            refreshData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        syncProductList();
        if (productFragment != null && productEntityList.size() > 0) {
            productFragment.refreshData(productEntityList);
        }

        if (null != orderFragment) {
            if (viewPager.getCurrentItem() == 0) {
                orderFragment.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    @Override
    protected void initViews(View view) {
        searchTv.setHint(getString(R.string.search_product_prompt));
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(this.getChildFragmentManager());
        viewPager.setAdapter(myPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < myPagerAdapter.getCount(); i++) {
            View tabView = myPagerAdapter.getTabView(mActivity, i);
            View targetView = tabView.findViewById(R.id.target_view);
            BadgeView badgeView = new BadgeView(mActivity, targetView);
            badgeView.setTextSize(10);
            badgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
            mBadgeViews.add(badgeView);
            tabLayout.getTabAt(i).setCustomView(tabView);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    if (null == orderFragment) {
                        orderFragment = new ReceivingOrderFragment();
                        orderFragment.setData(orderList);
                    }
                    orderFragment.notifyDataSetChanged();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void initData() {
        refreshData();
    }

    public void refreshData() {
        reqOrderList();
        reqOrderDetails();
    }

    public List<ProductEntity> getProductEntityList() {
        return productEntityList;
    }

    protected void initEmptyView(int mode) {
        emptyView.setVisibility(View.VISIBLE);
        emptyView.setMode(mode);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });
    }

    private void reqOrderList() {
        ((MyBaseActivity) mActivity).reqOrderList(8, true, new RequestListener() {
            @Override
            public void handleRequestResponse(ApiResponse result) {
                if (result.isSuccessful()) {
                    orderList.clear();
                    PendingOrder pendingOrder = (PendingOrder) result.getData();
                    if (null != pendingOrder) {
                        orderList = pendingOrder.getItems();
                        int orderCount = orderList != null ? orderList.size() : 0;
                        ((BottomTabActivity) mActivity).setReceivingOrderCount(orderCount);
                        if (orderCount > 0) {
                            emptyView.setVisibility(View.GONE);
                        } else {
                            initEmptyView(EmptyView.MODE_NODATA);
                        }
                    } else {
//                        Snackbar.make(tabLayout, "没有正在收货的订单数据", Snackbar.LENGTH_LONG).show();
                        initEmptyView(EmptyView.MODE_NODATA);
                    }

                    if (null != orderFragment) {
                        orderFragment.refreshData(orderList);
                    }
                    if (mBadgeViews.size() > 0) {
                        showBadgeView(mBadgeViews.get(0), orderList.size());
                    }
                } else {
                    initEmptyView(EmptyView.MODE_ERROR);
                    ToastUtils.show(mActivity, "请求结果错误：" + result.getInfo());
                }
            }

            @Override
            public void handleExceptionResponse(String errMsg) {
                initEmptyView(EmptyView.MODE_ERROR);
            }
        });
    }

    private void reqOrderDetails() {
        ((MyBaseActivity) mActivity).reqOrderDetails(null, 8, new RequestListener() {
            @Override
            public void handleRequestResponse(ApiResponse result) {
                if (result.isSuccessful()) {
                    productEntityList.clear();
                    ProductInfo productInfo = (ProductInfo) result.getData();
                    if (null != productInfo && productInfo.getItems().size() > 0) {
                        productEntityList = productInfo.getItems();
                        syncProductList();
                    } else {
//                        Snackbar.make(tabLayout, "没有正在收货的订单数据", Snackbar.LENGTH_LONG).show();
                    }
                    if (null != productFragment) {
                        productFragment.refreshData(productEntityList);
                    }
                    if (mBadgeViews.size() > 1) {
                        showBadgeView(mBadgeViews.get(1), productEntityList.size());
                    }
                } else {
                    ToastUtils.show(mActivity, "请求结果错误：" + result.getInfo());
                }
            }

            @Override
            public void handleExceptionResponse(String errMsg) {

            }
        });
    }

    /**
     *
     */
    private void syncProductList() {
        List<ProductEntity> localProductList = DBHelper.getProductEntityService().queryAll();
        HashMap<String, ProductEntity> localProductsHashMap = new HashMap<String, ProductEntity>();
        if (null != localProductList) {
            for (ProductEntity localProduct: localProductList) {
                String key = localProduct.getProductId() + localProduct.getPreBuyID();
                localProductsHashMap.put(key, localProduct);
            }
        }

        if (localProductsHashMap.size() > 0) {
            for (ProductEntity item : productEntityList) {
                ProductEntity localProduct = localProductsHashMap.get(item.getProductId() + item.getPreBuyID());
                if (null != localProduct) {
                    item.setIsReceived(localProduct.getIsReceived());
                    item.setReceivedQty(localProduct.getReceivedQty());
                    item.setReceivedUnit(localProduct.getReceivedUnit());
                    item.setReceivedRemark(localProduct.getReceivedRemark());
                    DBHelper.getReceivedListEntityService().saveOrUpdate(localProduct.getReceivedListEntities());
                    item.resetReceivedListEntities();
                }
            }
        }

        if (productEntityList.size() > 0) {
            DBHelper.getProductEntityService().deleteAll();
            DBHelper.getProductEntityService().saveOrUpdate(productEntityList);
        }
    }

    private void showBadgeView(BadgeView badgeView, int count) {
        if (count > 0) {
            badgeView.setText(String.valueOf(count));
            badgeView.show();
        } else {
            badgeView.hide();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GlobelDefines.REQ_CODE_SCAN) {
                String result = data.getStringExtra("Result");
                if (!TextUtils.isEmpty(result)) {
//                    searchTv.setText(result);
//                    doSearch(result);
                }
            } else if (requestCode == GlobelDefines.REQ_CODE_ORDER_DETAIL) {
                refreshData();
            }
        }
    }

    @OnClick({R.id.action_scan_tv, R.id.search_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_scan_tv: {
                Bundle bundle = new Bundle();
                bundle.putBoolean("to_result_page", true);
                bundle.putSerializable("product_list", (Serializable) productEntityList);
                if (((MyBaseActivity) mActivity).hasCameraPermissions()) {
                    ((MyBaseActivity) mActivity).gotoActivityForResult(CaptureActivity.class, false, bundle, GlobelDefines.REQ_CODE_SCAN);
                }
                //((MyBaseActivity)mActivity).hasCameraPermissions(bundle, true);
                break;
            }
            case R.id.search_tv: {
                Bundle bundle = new Bundle();
                bundle.putSerializable("product_list", (Serializable) productEntityList);
                mActivity.gotoActivity(SearchResultRcvProductActivity.class, false, bundle);
                break;
            }
        }
    }

    public void onSuccessEvent(String barcodeData) {
        if (!TextUtils.isEmpty(barcodeData)) {
            Bundle bundle = new Bundle();
            bundle.putInt("search_type", SearchResultRcvProductActivity.TYPE_GLOBAL_SEARCH);
            bundle.putString("Result", barcodeData);
            bundle.putBoolean("scan_search", true);
            bundle.putSerializable("product_list", (Serializable) productEntityList);
            mActivity.gotoActivity(SearchResultRcvProductActivity.class, false, bundle);
        }
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        private String tabTitles[] = new String[]{getResources().getString(R.string.receiving_order), getResources().getString(R.string.receiving_product_list)};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: {
                    if (null == orderFragment) {
                        orderFragment = new ReceivingOrderFragment();
                        orderFragment.setData(orderList);
                    }
                    return orderFragment;
                }
                case 1: {
                    if (null == productFragment) {
                        productFragment = new ReceivingProductFragment();
                        productFragment.setData(productEntityList);
                    }
                    return productFragment;
                }
                default:
                    break;
            }
            return null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            if (orderFragment == null && fragment instanceof ReceivingOrderFragment) {
                orderFragment =  (ReceivingOrderFragment) fragment;
            } else if (productFragment == null && fragment instanceof  ReceivingProductFragment) {
                productFragment = (ReceivingProductFragment) fragment;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        public View getTabView(Context context, int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.view_tab_content2, null);
            TextView tabText = (TextView) view.findViewById(R.id.tab_content_text);
            tabText.setText(tabTitles[position]);

            return view;
        }
    }
}
