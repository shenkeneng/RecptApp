package com.frxs.receipt.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.frxs.core.utils.MathUtils;
import com.frxs.core.utils.SharedPreferencesHelper;
import com.frxs.core.utils.ToastUtils;
import com.frxs.core.widget.EmptyView;
import com.frxs.core.widget.PtrFrameLayoutEx;
import com.frxs.receipt.R;
import com.frxs.receipt.activity.BottomTabActivity;
import com.frxs.receipt.activity.CaptureActivity;
import com.frxs.receipt.activity.MyBaseActivity;
import com.frxs.receipt.activity.PendingOrderDetailActivity;
import com.frxs.receipt.activity.ReceivingOrderDetailActivity;
import com.frxs.receipt.comms.Config;
import com.frxs.receipt.comms.GlobelDefines;
import com.frxs.receipt.model.PendingOrder;
import com.frxs.receipt.rest.model.ApiResponse;
import com.frxs.receipt.rest.service.RequestListener;
import com.pacific.adapter.Adapter;
import com.pacific.adapter.AdapterHelper;
import com.pacific.adapter.BaseAdapterHelper;
import com.pacific.adapter.ExpandableAdapter;
import com.pacific.adapter.ExpandableAdapterHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/05/09
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class HomeFragment extends MaterialStyleFragment {
    public static final String TAG = HomeFragment.class.getSimpleName();
    @BindView(R.id.search_tv)
    EditText searchTv;
    @BindView(R.id.tv_order_number)
    TextView tvOrderNumber;
    @BindView(R.id.pending_receive_order_elv)
    ExpandableListView pendingReceiveOrderElv;
    @BindView(R.id.search_result_lv)
    ListView searchResultLv;
    @BindView(R.id.pending_rcv_layout)
    LinearLayout pendingRcvLayout;
    @BindView(R.id.ptr_frame_ll)
    PtrFrameLayoutEx ptrFrameLl;
    @BindView(R.id.empty_view)
    EmptyView emptyView;
    private ExpandableAdapter expandableAdapter;
    private Adapter searchResultAdapter;
    private List<PendingOrder.ItemsBean> orderList = new ArrayList<PendingOrder.ItemsBean>(); //所有订单的列表，方便搜索取值
    private List<PendingOrder> pendingOrderList = new ArrayList<>();
    private List<PendingOrder.ItemsBean> searchResultOrderList = new ArrayList<PendingOrder.ItemsBean>();

    /**
     * 传入需要的参数，设置给arguments
     *
     * @param argument
     * @return
     */
    public static HomeFragment newInstance(String argument) {
        Bundle bundle = new Bundle();
        bundle.putString(TAG, argument);
        HomeFragment contentFragment = new HomeFragment();
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initViews(View view) {
        super.initViews(view);
        tvOrderNumber.setText(String.format(getString(R.string.pending_receive_order), 0));
    }

    @Override
    public int getPtrFrameLayoutId() {
        return R.id.ptr_frame_ll;
    }

    @Override
    protected void initData() {
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

        searchTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchOrder(s.toString());
            }
        });

        expandableAdapter = new ExpandableAdapter<PendingOrder, PendingOrder.ItemsBean>(mActivity, R.layout.item_group_pending_order, R.layout.item_child_pending_order) {
            @Override
            protected List getChildren(int groupPosition) {
                return get(groupPosition).getItems();
            }

            @Override
            protected void convertGroupView(boolean isExpanded, ExpandableAdapterHelper helper, PendingOrder item) {
                TextView groupNaviTv = helper.getView(R.id.group_navi_tv);
                if (isExpanded) {
                    groupNaviTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_goods_up, 0);
                } else {
                    groupNaviTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_goods_down, 0);
                }
                helper.setText(R.id.tv_order_date, item.getDate());
            }

            @Override
            protected void convertChildView(boolean isLastChild, ExpandableAdapterHelper helper, final PendingOrder.ItemsBean item) {
                convertListItemView(helper, item);
            }
        };

        pendingReceiveOrderElv.setGroupIndicator(null);
        pendingReceiveOrderElv.setAdapter(expandableAdapter);
        pendingReceiveOrderElv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                PendingOrder.ItemsBean item = pendingOrderList.get(groupPosition).getItems().get(childPosition);
                gotoPendingOrderDetailActivity(item);
                return true;
            }
        });

        searchResultAdapter = new Adapter<PendingOrder.ItemsBean>(mActivity, R.layout.item_child_pending_order) {
            @Override
            protected void convert(AdapterHelper helper, PendingOrder.ItemsBean item) {
                convertListItemView(helper, item);
            }
        };
        searchResultLv.setAdapter(searchResultAdapter);

        searchResultLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PendingOrder.ItemsBean item = (PendingOrder.ItemsBean) parent.getAdapter().getItem(position);
                gotoPendingOrderDetailActivity(item);
            }
        });

    }

    private void convertListItemView(BaseAdapterHelper helper, final PendingOrder.ItemsBean item) {
        helper.setVisible(R.id.label_tv, View.GONE);
        helper.setText(R.id.order_id_tv, item.getPreBuyID());
        String supplierInfo = item.getVendorCode() + "-" + item.getVendorName();
        helper.setText(R.id.supplier_tv, supplierInfo);
        helper.setText(R.id.total_row_tv, String.format(getString(R.string.total_product_row), item.getDetailsCount()));
        helper.setText(R.id.total_product_tv, String.format(getString(R.string.total_product_num), MathUtils.doubleTrans(item.getProductTotalQty())));
        if (TextUtils.isEmpty(item.getRemark())) {
            helper.setVisible(R.id.remark_layout, View.GONE);
        } else {
            helper.setVisible(R.id.remark_layout, View.VISIBLE);
            helper.setText(R.id.remark_tv, item.getRemark());
        }
        helper.setOnClickListener(R.id.start_receive_btn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MyBaseActivity) mActivity).reqStartReceiveOrder(item.getPreBuyID(), new RequestListener() {
                    @Override
                    public void handleRequestResponse(ApiResponse result) {
                        if (result.isSuccessful()) {
                            ((BottomTabActivity) mActivity).addReceivingOrderCount(1);
                            //删除已经开始收货的订单
                            for (PendingOrder.ItemsBean order : orderList) {
                                if (item.getPreBuyID().equals(order.getPreBuyID())) {
                                    orderList.remove(order);
                                    syncSearchResultList();
                                    //searchResultOrderList.remove(order);
                                    break;
                                }
                            }

                            //重新组装ExpandableListView列表数据，然后更新
                            pendingOrderList.clear();
                            packagePendingOrderList();

                            updateUI();
                            isShowEmptyView(orderList);
                            ToastUtils.show(mActivity, "开始收货操作成功");

                            //v2.6直接跳转收货页面详情页
                            Bundle bundle = new Bundle();
                            bundle.putString("PreBuyID", item.getPreBuyID());
                            mActivity.gotoActivityForResult(ReceivingOrderDetailActivity.class, false, bundle, GlobelDefines.REQ_CODE_ORDER_DETAIL);
                        } else {
                            ToastUtils.show(mActivity, result.getInfo());
                        }
                    }

                    @Override
                    public void handleExceptionResponse(String errMsg) {

                    }
                });
            }
        });
    }

    private void isShowEmptyView(List<PendingOrder.ItemsBean> list) {
        if (list != null && list.size() > 0) {
            emptyView.setVisibility(View.GONE);
        } else {
            initEmptyView(EmptyView.MODE_NODATA);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!this.isHidden()) {
            reqOrderList(true);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(mActivity, Config.PREFS_NAME);
            boolean isRefresh = helper.getBoolean(Config.REFRESH_WAIT_RECEIVED, false);
            if (isRefresh){
                helper.putValue(Config.REFRESH_WAIT_RECEIVED, false);
                reqOrderList(true);
            }
        }
    }

    private void searchOrder(String result) {
        //v2.6新增14位商品条码找订单列表
        if (!TextUtils.isEmpty(result) && (result.length() == 13 || result.length() == 14)) {
            reqSearchOrderList(result);
        } else {
            doSearch(result);
        }
    }

    private void reqOrderList(boolean needProgressDialog) {
        ((MyBaseActivity) mActivity).reqOrderList(6, needProgressDialog, new RequestListener() {
            @Override
            public void handleRequestResponse(ApiResponse result) {
                if (result.isSuccessful()) {
                    pendingOrderList.clear();
                    orderList.clear();
                    PendingOrder pendingOrder = (PendingOrder) result.getData();
                    if (null != pendingOrder) {
                        orderList = pendingOrder.getItems();
                        if (orderList != null && orderList.size() > 0) {
                            emptyView.setVisibility(View.GONE);
                            packagePendingOrderList();
                            if (!TextUtils.isEmpty(searchTv.getText().toString().trim())) {
                                searchOrder(searchTv.getText().toString().trim());
                            }
                        } else {
                            searchResultOrderList.clear();
                            initEmptyView(EmptyView.MODE_NODATA);
                        }
                    } else {
                        initEmptyView(EmptyView.MODE_NODATA);
                        ToastUtils.show(mActivity, "返回数据为空");
                    }
                    updateUI();
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

    private void reqSearchOrderList(String searchText) {
        ((MyBaseActivity) mActivity).reqOrderList(6, true, searchText, new RequestListener() {
            @Override
            public void handleRequestResponse(ApiResponse result) {
                if (result.isSuccessful()) {
                    List<PendingOrder.ItemsBean> searchResultList;
                    PendingOrder resultOrder = (PendingOrder) result.getData();
                    if (resultOrder != null) {
                        searchResultLv.setVisibility(View.VISIBLE);
                        pendingRcvLayout.setVisibility(View.GONE);
                        searchResultOrderList = resultOrder.getItems();
                        searchResultAdapter.replaceAll(searchResultOrderList);
                        isShowEmptyView(searchResultOrderList);
                    } else {
                        isShowEmptyView(orderList);
                        searchResultOrderList.clear();
                        searchResultLv.setVisibility(View.GONE);
                        pendingRcvLayout.setVisibility(View.VISIBLE);
                    }
                }



            }

            @Override
            public void handleExceptionResponse(String errMsg) {
                ToastUtils.show(mActivity, errMsg);
            }
        });
    }

    private void updateUI() {
        expandableAdapter.replaceAll(pendingOrderList);
        pendingReceiveOrderElv.expandGroup(0);
        tvOrderNumber.setText(String.format(getString(R.string.pending_receive_order), orderList.size()));

        searchResultAdapter.replaceAll(searchResultOrderList);
    }

    private void doSearch(String keyword) {
        if (!TextUtils.isEmpty(keyword)) {
            searchResultLv.setVisibility(View.VISIBLE);
            pendingRcvLayout.setVisibility(View.GONE);
            searchResultOrderList.clear();
            for (PendingOrder.ItemsBean item : orderList) {
                if (item.getPreBuyID().contains(keyword)
                        || String.valueOf(item.getVendorCode()).contains(keyword)
                        || item.getVendorName().contains(keyword)) {
                    searchResultOrderList.add(item);
                }
            }
            searchResultAdapter.replaceAll(searchResultOrderList);
            isShowEmptyView(searchResultOrderList);
        } else {
            isShowEmptyView(orderList);
            searchResultOrderList.clear();
            searchResultLv.setVisibility(View.GONE);
            pendingRcvLayout.setVisibility(View.VISIBLE);
        }
    }

    private void gotoPendingOrderDetailActivity(PendingOrder.ItemsBean item) {
        Bundle bundle = new Bundle();
        bundle.putString("PreBuyID", item.getPreBuyID());
        mActivity.gotoActivityForResult(PendingOrderDetailActivity.class, false, bundle, GlobelDefines.REQ_CODE_ORDER_DETAIL);
    }

    /**
     * 数据源orderList发生变化之后，重新组装pendingOrderList
     */
    private void packagePendingOrderList() {
        TreeMap<String, ArrayList> dateTreeMap = groupItemByDate(orderList);
        Iterator titer = dateTreeMap.entrySet().iterator();
        while (titer.hasNext()) {
            Map.Entry ent = (Map.Entry) titer.next();
            String key = (String) ent.getKey();
            ArrayList value = (ArrayList) ent.getValue();
            PendingOrder subPendingOrder = new PendingOrder();
            subPendingOrder.setDate(key);
            subPendingOrder.setItems(value);
            pendingOrderList.add(subPendingOrder);
        }
    }

    private void syncSearchResultList() {
        HashMap<String, PendingOrder.ItemsBean> orderMap = new HashMap<String, PendingOrder.ItemsBean>();
        for (PendingOrder.ItemsBean order : orderList) {
            orderMap.put(order.getPreBuyID(), order);
        }

        Iterator<PendingOrder.ItemsBean> iterator = searchResultOrderList.iterator();
        while (iterator.hasNext()) {
            PendingOrder.ItemsBean next = iterator.next();
            PendingOrder.ItemsBean itemsBean = orderMap.get(next.getPreBuyID());
            if (itemsBean == null) {
                iterator.remove();
            }
        }
    }

    public TreeMap<String, ArrayList> groupItemByDate(List<PendingOrder.ItemsBean> list) {
        TreeMap tm = new TreeMap();
        for (int i = 0; i < list.size(); i++) {
            PendingOrder.ItemsBean item = list.get(i);
            if (tm.containsKey(item.getOrderDate())) {//
                ArrayList productList = (ArrayList) tm.get(item.getOrderDate());
                productList.add(item);
            } else {
                ArrayList<PendingOrder.ItemsBean> tem = new ArrayList<PendingOrder.ItemsBean>();
                tem.add(item);
                tm.put(item.getOrderDate(), tem);
            }

        }
        return tm;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @OnClick(R.id.action_scan_tv)
    public void onClick() {
        if (((MyBaseActivity) mActivity).hasCameraPermissions()) {
            ((MyBaseActivity) mActivity).gotoActivityForResult(CaptureActivity.class, false, null, GlobelDefines.REQ_CODE_SCAN);
        }
        //((MyBaseActivity) mActivity).hasCameraPermissions(null, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GlobelDefines.REQ_CODE_SCAN && null != data) {
                String result = data.getStringExtra("Result");
                searchTv.setText(result);
                searchOrder(result);
            } else if (requestCode == GlobelDefines.REQ_CODE_ORDER_DETAIL && null != data) {
                ((BottomTabActivity) mActivity).addReceivingOrderCount(1);
            }
        }
    }

    protected void initEmptyView(int mode) {
        emptyView.setVisibility(View.VISIBLE);
        emptyView.setMode(mode);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqOrderList(true);
            }
        });
    }

    public void onSuccessEvent(String barcodeData) {
        if (!TextUtils.isEmpty(barcodeData)) {
//            searchTv.setText(barcodeData);
            List<PendingOrder.ItemsBean> scanResultList = new ArrayList<>();
            if (null != orderList) {
                for (PendingOrder.ItemsBean item : orderList) {
                    if (item.getPreBuyID().equals(barcodeData)) {
                        gotoPendingOrderDetailActivity(item);
                        break;
                    }
                }
            }

            if (scanResultList.size() == 0) {
                ToastUtils.show(mActivity, "没有找到" + barcodeData);
            }
        }
    }
}
