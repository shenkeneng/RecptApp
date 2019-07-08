package com.frxs.receipt.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.frxs.core.utils.DisplayUtil;
import com.frxs.core.utils.SharedPreferencesHelper;
import com.frxs.core.utils.ToastUtils;
import com.frxs.core.widget.EmptyView;
import com.frxs.core.widget.PtrFrameLayoutEx;
import com.frxs.receipt.MyApplication;
import com.frxs.receipt.R;
import com.frxs.receipt.activity.AllOrderListActivity;
import com.frxs.receipt.activity.BottomTabActivity;
import com.frxs.receipt.activity.CaptureActivity;
import com.frxs.receipt.activity.MyBaseActivity;
import com.frxs.receipt.activity.ReceivingOrderDetailActivity;
import com.frxs.receipt.comms.Config;
import com.frxs.receipt.comms.GlobelDefines;
import com.frxs.receipt.greendao.DBHelper;
import com.frxs.receipt.greendao.entity.ProductEntity;
import com.frxs.receipt.greendao.gen.ProductEntityDao;
import com.frxs.receipt.model.PendingOrder;
import com.frxs.receipt.rest.model.AjaxParams;
import com.frxs.receipt.rest.model.ApiResponse;
import com.frxs.receipt.rest.service.RequestListener;
import com.frxs.receipt.rest.service.SimpleCallback;
import com.frxs.receipt.widget.ClearEditText;
import com.pacific.adapter.Adapter;
import com.pacific.adapter.AdapterHelper;
import org.greenrobot.eventbus.EventBus;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import retrofit2.Call;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/06/07
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class ReceivingOrderFragment extends MaterialStyleFragment {
    public static final String TAG = ReceivingOrderFragment.class.getSimpleName();

    @BindView(R.id.order_list_view)
    ListView orderListView;
    @BindView(R.id.empty_view)
    EmptyView emptyView;
    @BindView(R.id.action_scan_tv)
    TextView actionScanTv;
    @BindView(R.id.search_tv)
    ClearEditText searchTv;
    @BindView(R.id.ptr_frame_ll)
    PtrFrameLayoutEx ptrFrameLl;
    @BindView(R.id.bt_search)
    TextView tvSearch;
    private Adapter adapter;
    private List<PendingOrder.ItemsBean> orderList = new ArrayList<>();
    private List<PendingOrder.ItemsBean> searchResultOrderList = new ArrayList<PendingOrder.ItemsBean>();
    private ReceivingFragment parentFragment;

    /**
     * 传入需要的参数，设置给arguments
     *
     * @param argument
     * @return
     */
    public static ReceivingOrderFragment newInstance(String argument) {
        Bundle bundle = new Bundle();
        bundle.putString(TAG, argument);
        ReceivingOrderFragment contentFragment = new ReceivingOrderFragment();
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_receiving_order;
    }

    @Override
    public int getPtrFrameLayoutId() {
        return R.id.ptr_frame_ll;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            refreshData();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GlobelDefines.REQ_CODE_SCAN) {
                if (data != null) {
                    String resultTxt = data.getStringExtra("Result");
                    if (!TextUtils.isEmpty(resultTxt)) {
                        ScanSearchOrder(resultTxt);
                    }
                }
            } else {
                refreshData();
            }
        }
    }

    public void notifyDataSetChanged() {
        if (adapter != null && adapter.getCount() > 0) {
            adapter.notifyDataSetChanged();
        }
    }

    @OnClick({R.id.bt_search, R.id.action_scan_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_search:
                String searchContext = searchTv.getText().toString().trim();
                if (!TextUtils.isEmpty(searchContext)){
                    doSearch(searchContext);
                } else {
                    ToastUtils.show(mActivity, "搜索内容为空");
                }
                break;

            case R.id.action_scan_tv:
                Bundle bundle = new Bundle();
                bundle.putBoolean("to_result_page", true);
                bundle.putSerializable("order_list", (Serializable) orderList);
                if (((MyBaseActivity) mActivity).hasCameraPermissions()) {
                    mActivity.gotoActivityForResult(CaptureActivity.class, false, bundle, GlobelDefines.REQ_CODE_SCAN);
                }
                break;
        }
    }

    @Override
    protected void initViews(View view) {
        super.initViews(view);
        //actionScanTv.setVisibility(View.GONE);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)searchTv.getLayoutParams();
        layoutParams.setMargins(DisplayUtil.dip2px(mActivity, 10), 0, DisplayUtil.dip2px(mActivity, 10), 0);
        searchTv.setLayoutParams(layoutParams);
        parentFragment = (ReceivingFragment) getParentFragment();
    }

    @Override
    protected void initData() {
        if (needHideQrcodeScanView()) {
            actionScanTv.setVisibility(View.GONE);
        }

        ptrFrameLl.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                boolean iReturn = PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                return iReturn;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                searchTv.setText("");
                reqOrderList(false);
            }
        });

        adapter = new Adapter<PendingOrder.ItemsBean>(mActivity, R.layout.item_receiving_order) {
            @Override
            protected void convert(AdapterHelper helper, PendingOrder.ItemsBean item) {
                helper.setVisible(R.id.tv_order_status, View.GONE);
                helper.setText(R.id.order_time_tv, item.getPostingTime().replace("年","-").replace("月","-").replace("日",""));
                helper.setText(R.id.order_id_tv, item.getPreBuyID());
                String supplierInfo = item.getVendorCode() + "-" + item.getVendorName();
                helper.setText(R.id.supplier_tv, supplierInfo);
                helper.setText(R.id.total_row_tv, String.format(getString(R.string.total_product_row), item.getDetailsCount()));
                List<ProductEntity> localProductList = DBHelper.getProductEntityService().queryBuilder().where(ProductEntityDao.Properties.PreBuyID.eq(item.getPreBuyID()), ProductEntityDao.Properties.IsReceived.eq(true)).list();
                int receivedCount = localProductList != null ? localProductList.size() : 0;
                if (receivedCount > 0) {
                    helper.setVisible(R.id.label_tv, View.VISIBLE);
                } else {
                    helper.setVisible(R.id.label_tv, View.GONE);
                }
                helper.setText(R.id.total_product_tv, String.format(getString(R.string.received_product_num), String.valueOf(receivedCount)));

                if (TextUtils.isEmpty(item.getRemark())) {
                    helper.setVisible(R.id.remark_layout, View.GONE);
                } else {
                    helper.setVisible(R.id.remark_layout, View.VISIBLE);
                    helper.setText(R.id.remark_tv, getResources().getString(R.string.remark) + "：" + item.getRemark());
                }
            }
        };
        orderListView.setAdapter(adapter);
        adapter.replaceAll(orderList);

        orderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PendingOrder.ItemsBean item = (PendingOrder.ItemsBean) parent.getAdapter().getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString("PreBuyID", item.getPreBuyID());
               /* if (null != parentFragment) {
                    List<ProductEntity> productEntityList = parentFragment.getProductEntityList();
                    List<ProductEntity> orderProductList = new ArrayList<ProductEntity>();
                    if (null != productEntityList) {
                        for (ProductEntity product : productEntityList) {
                            if (product.getPreBuyID().equals(item.getPreBuyID())) {
                                orderProductList.add(product);
                            }
                        }
                    }

                    bundle.putSerializable("product_list", (Serializable) orderProductList);
                }*/
                mActivity.gotoActivityForResult(ReceivingOrderDetailActivity.class, false, bundle, GlobelDefines.REQ_CODE_ORDER_DETAIL);
            }
        });

        orderListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                PendingOrder.ItemsBean item = (PendingOrder.ItemsBean) parent.getAdapter().getItem(position);
                if (item != null) {
                    showCancelDialog(item.getVendorName(), item.getPreBuyID());
                }
                return true;
            }
        });

        reqOrderList(true);
    }

    private void doSearch(String keyword) {
        searchResultOrderList.clear();

        if (!TextUtils.isEmpty(keyword)) {
            for (PendingOrder.ItemsBean item : orderList) {
                if (item.getPreBuyID().contains(keyword)
                        || String.valueOf(item.getVendorCode()).contains(keyword)
                        || item.getVendorName().contains(keyword)) {
                    searchResultOrderList.add(item);
                }
            }
            // 搜索服务器数据
            reqSearchOrderList(keyword, true);
        }
    }

    private void showCancelDialog(String vendorName, final String preBuyId) {
        final Dialog confirmDlg = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        View contentView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_confirm, null);
        TextView contentTv = (TextView) contentView.findViewById(R.id.content_tv);
        contentTv.setText(String.format(getString(R.string.over_receive_cancel), vendorName, preBuyId));
        contentView.findViewById(R.id.confirm_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqReceiverOrderStop(preBuyId);
                confirmDlg.dismiss();
            }
        });

        contentView.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDlg.dismiss();
            }
        });
        confirmDlg.setContentView(contentView);
        confirmDlg.show();
    }

    private void reqOrderList(boolean needProgressDialog) {
        ((MyBaseActivity) mActivity).reqOrderList(8, needProgressDialog, new RequestListener() {
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
                            for (PendingOrder.ItemsBean order : orderList) {
                                order.setStatus(8);
                            }
                        } else {
                            initEmptyView(EmptyView.MODE_NODATA);
                        }
                    } else {
                        initEmptyView(EmptyView.MODE_NODATA);
                    }
                    adapter.replaceAll(orderList);

                } else {
                    initEmptyView(EmptyView.MODE_ERROR);
                    ToastUtils.show(mActivity, "请求结果错误：" + result.getInfo());
                }

                refreshComplete();
            }

            @Override
            public void handleExceptionResponse(String errMsg) {
                refreshComplete();
                initEmptyView(EmptyView.MODE_ERROR);
            }
        });
    }

    /**
     * 搜索结果 isStartOrderList 是否打开搜索列表
     * @param searchContent
     */
    private void reqSearchOrderList(final String searchContent, final boolean isStartSearchList) {
        ((MyBaseActivity) mActivity).reqOrderList(6, true, searchContent, new RequestListener() {
            @Override
            public void handleRequestResponse(ApiResponse result) {
                if (result.isSuccessful()) {
                    PendingOrder resultOrder = (PendingOrder) result.getData();
                    if (resultOrder != null) {
                        List<PendingOrder.ItemsBean> items = resultOrder.getItems();
                        if (items != null && items.size() > 0) {
                            for (PendingOrder.ItemsBean item : items) {
                                item.setStatus(6);
                                searchResultOrderList.add(item);
                            }
                        }
                    }

                    // 跳转搜索结果页
                    if (searchResultOrderList.size() > 0) {
                        if (searchResultOrderList.size() == 1 && !isStartSearchList) {
                            startReceiveOrder();
                        } else {
                            gotoAllOrderListActivity(searchContent);
                        }
                    } else {
                        ToastUtils.show(mActivity, "没有搜索到任何结果");
                    }
                } else {
                    ToastUtils.show(mActivity, "请求结果错误：" + result.getInfo());
                }
            }

            @Override
            public void handleExceptionResponse(String errMsg) {
                ToastUtils.show(mActivity, errMsg);
            }
        });
    }

    private void gotoAllOrderListActivity(String searchContent) {
        if (searchResultOrderList.size() <= 0) {
            ToastUtils.show(mActivity, "未找到代购订单，请重新扫描");
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString("search_content", searchContent);
        bundle.putSerializable("search_result_list", (Serializable) searchResultOrderList);
        mActivity.gotoActivityForResult(AllOrderListActivity.class, false, bundle, GlobelDefines.REQ_CODE_ORDER_DETAIL);
    }

    /**
     * 开始收货操作成功后，跳转商品详情页面
     */
    private void startReceiveOrder() {
        ((MyBaseActivity) mActivity).reqStartReceiveOrder(searchResultOrderList.get(0).getPreBuyID(), new RequestListener() {
            @Override
            public void handleRequestResponse(ApiResponse result) {
                if (result.isSuccessful()) {
                    EventBus.getDefault().post(1);
                    ToastUtils.show(mActivity, "开始收货操作成功");
                    gotoOrderDetailActivity();
                } else {
                    ToastUtils.show(mActivity, result.getInfo());
                }
            }

            @Override
            public void handleExceptionResponse(String errMsg) {

            }
        });
    }

    private void gotoOrderDetailActivity() {
        if (searchResultOrderList.size() <= 0) {
//            ToastUtils.show(mActivity, "未找到代购订单，请重新扫描");
            reqOrderList(true);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("PreBuyID",  searchResultOrderList.get(0).getPreBuyID());
        mActivity.gotoActivityForResult(ReceivingOrderDetailActivity.class, false, bundle, GlobelDefines.REQ_CODE_ORDER_DETAIL);
    }

    private void reqReceiverOrderStop(final String preBuyId) {
        mActivity.showProgressDialog();
        AjaxParams params = new AjaxParams();
        params.put("WID", getWID());
        params.put("SubWID", MyApplication.getInstance().getUserInfo().getSubWID());
        params.put("PreBuyID", preBuyId);

        getService().ReceiverOrderStop(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<String>>() {
            @Override
            public void onResponse(ApiResponse<String> result, int code, String msg) {
                mActivity.dismissProgressDialog();
                if (result.isSuccessful()) {
                    ToastUtils.show(mActivity, "已终止该订单收货");
                    SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(mActivity, Config.PREFS_NAME);
                    helper.putValue(Config.REFRESH_WAIT_RECEIVED, true);
                    List<ProductEntity> localProductList = DBHelper.getProductEntityService().queryBuilder().where(ProductEntityDao.Properties.PreBuyID.eq(preBuyId)).list();
                    for (ProductEntity item: localProductList) {
                        DBHelper.getReceivedListEntityService().delete(item.getReceivedListEntities()); //删除关联的已收货列表
                        item.resetReceivedListEntities();
                    }
                    DBHelper.getProductEntityService().delete(localProductList);
                    refreshData();
                } else {
                    ToastUtils.show(mActivity, "该订单终止收货失败");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                super.onFailure(call, t);
                mActivity.dismissProgressDialog();
                ToastUtils.show(mActivity, t.getMessage());
            }
        });
    }

    public void setData(List<PendingOrder.ItemsBean> orderList) {
        if (null != orderList) {
            this.orderList = orderList;
        }
    }

    public void refreshData() {
        reqOrderList(true);
    }

    public void refreshData(List<PendingOrder.ItemsBean> orderList) {
        this.orderList = orderList;
        if (orderList != null && null != adapter) {
            adapter.replaceAll(orderList);
        }
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    public void onSuccessEvent(String barcodeData) {
        ScanSearchOrder(barcodeData);
    }

    private void ScanSearchOrder(String barcodeData) {
        if (!TextUtils.isEmpty(barcodeData)) {
            searchResultOrderList.clear();
            if (barcodeData.length() == 13 || barcodeData.length() == 14) {
                // 先搜索本地数据
                if (null != orderList) {
                    for (PendingOrder.ItemsBean item : orderList) {
                        List<ProductEntity> productEntityList = DBHelper.getProductEntityService().queryBuilder().where(ProductEntityDao.Properties.PreBuyID.eq(item.getPreBuyID())).list();
                        for (ProductEntity product : productEntityList) {
                            if (product.getBarCode().contains(barcodeData) || product.getBigUnitBarCode().contains(barcodeData)){
                                if (!searchResultOrderList.contains(item)) {
                                    searchResultOrderList.add(item);
                                }
                            }
                        }
                    }
                    reqSearchOrderList(barcodeData, true);
                } else {
                    // 搜索服务器数据
                    reqSearchOrderList(barcodeData, true);
                }
            } else {
                // 先搜索本地数据
                if (null != orderList) {
                    for (PendingOrder.ItemsBean item : orderList) {
                        if (item.getPreBuyID().contains(barcodeData)) {
                            searchResultOrderList.add(item);
                        }
                    }

                   if (searchResultOrderList.size() == 1) {
                        // 有且唯一跳转商品详情页面
                       gotoOrderDetailActivity();
                    } else {
                        // 本地没有数据搜索服务器
                        reqSearchOrderList(barcodeData, false);
                    }
                } else {
                    // 搜索服务器数据
                    reqSearchOrderList(barcodeData, false);
                }
            }
        }
    }

    public boolean needHideQrcodeScanView() {
        //brand=Honeywell model=Glory50
        String brand = Build.BRAND;
        // String model = Build.MODEL;
        if (!TextUtils.isEmpty(brand) && brand.equals("Honeywell")) {
            return true;
        }else if (!TextUtils.isEmpty(brand) && brand.equals("A8")) {
            return true;
        } else {
            return false;
        }
    }
}
