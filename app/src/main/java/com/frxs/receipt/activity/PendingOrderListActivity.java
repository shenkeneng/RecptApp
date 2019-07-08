package com.frxs.receipt.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.frxs.core.utils.MathUtils;
import com.frxs.core.utils.ToastUtils;
import com.frxs.core.widget.EmptyView;
import com.frxs.receipt.R;
import com.frxs.receipt.comms.GlobelDefines;
import com.frxs.receipt.model.PendingOrder;
import com.frxs.receipt.rest.model.ApiResponse;
import com.frxs.receipt.rest.service.RequestListener;
import com.pacific.adapter.Adapter;
import com.pacific.adapter.AdapterHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/06/08
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class PendingOrderListActivity extends MyBaseActivity {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.search_result_lv)
    ListView searchResultOrderLv;
    @BindView(R.id.search_keywords_tv)
    TextView searchKeywordsTv;
    @BindView(R.id.empty_view)
    EmptyView emptyView;
    private Adapter adapter;
    private String searchContent = "";
    private List<PendingOrder.ItemsBean> searchResultOrderList = new ArrayList<PendingOrder.ItemsBean>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_order_list);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        titleTv.setText(R.string.pending_rcv_order);

        adapter = new Adapter<PendingOrder.ItemsBean>(this, R.layout.item_child_pending_order) {
            @Override
            protected void convert(AdapterHelper helper, final PendingOrder.ItemsBean item) {
                helper.setText(R.id.order_date_tv, item.getOrderDate());
                helper.setVisible(R.id.label_tv, View.GONE);
                helper.setText(R.id.order_id_tv, item.getPreBuyID());
                String supplierInfo = item.getVendorCode() + "-" + item.getVendorName();
                helper.setText(R.id.supplier_tv, supplierInfo);
                helper.setText(R.id.total_row_tv, String.format(getString(R.string.total_row), item.getDetailsCount()));
                helper.setText(R.id.total_product_tv, String.format(getString(R.string.total_num), MathUtils.doubleTrans(item.getProductTotalQty())));
                if (TextUtils.isEmpty(item.getRemark())) {
                    helper.setVisible(R.id.remark_layout, View.GONE);
                } else {
                    helper.setVisible(R.id.remark_layout, View.VISIBLE);
                    helper.setText(R.id.remark_tv, item.getRemark());
                }
                helper.setOnClickListener(R.id.start_receive_btn, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PendingOrderListActivity.this.reqStartReceiveOrder(item.getPreBuyID(), new RequestListener() {
                            @Override
                            public void handleRequestResponse(ApiResponse result) {
                                if (result.isSuccessful()) {
//                                    ((BottomTabActivity) mActivity).addReceivingOrderCount(1);
                                    EventBus.getDefault().post(1);
                                    //删除已经开始收货的订单
                                    for (PendingOrder.ItemsBean order : searchResultOrderList) {
                                        if (item.getPreBuyID().equals(order.getPreBuyID())) {
                                            searchResultOrderList.remove(order);
                                            break;
                                        }
                                    }
                                    adapter.replaceAll(searchResultOrderList);
                                    if (searchResultOrderList.size() == 0) {
                                        initEmptyView(EmptyView.MODE_NODATA);
                                    }
                                    ToastUtils.show(PendingOrderListActivity.this, "开始收货操作成功");

                                    Bundle bundle = new Bundle();
                                    bundle.putString("PreBuyID", item.getPreBuyID());
                                    gotoActivityForResult(ReceivingOrderDetailActivity.class, false, bundle, GlobelDefines.REQ_CODE_ORDER_DETAIL);
                                } else {
                                    ToastUtils.show(PendingOrderListActivity.this, result.getInfo());
                                }
                            }

                            @Override
                            public void handleExceptionResponse(String errMsg) {

                            }
                        });
                    }
                });
            }
        };
        searchResultOrderLv.setAdapter(adapter);

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            searchContent = bundle.getString("search_content", "");
        }

        if (TextUtils.isEmpty(searchContent)) {
            searchKeywordsTv.setVisibility(View.GONE);
        } else {
            searchKeywordsTv.setText(String.format(getString(R.string.pending_search_result), searchContent));
        }

        reqSearchOrderList();
    }

    @OnClick(R.id.action_back_tv)
    public void onClick() {
        finish();
    }

    private void reqSearchOrderList() {
        reqOrderList(6, true, searchContent, new RequestListener() {
            @Override
            public void handleRequestResponse(ApiResponse result) {
                if (result.isSuccessful()) {
                    searchResultOrderList.clear();
                    PendingOrder resultOrder = (PendingOrder) result.getData();
                    if (resultOrder != null) {
                        searchResultOrderList = resultOrder.getItems();
                    }

                    if (searchResultOrderList.size() > 0) {
                        adapter.replaceAll(searchResultOrderList);
                        emptyView.setVisibility(View.GONE);
                    } else {
                        initEmptyView(EmptyView.MODE_NODATA);
                    }
                } else {
                    initEmptyView(EmptyView.MODE_ERROR);
                    ToastUtils.show(PendingOrderListActivity.this, "请求结果错误：" + result.getInfo());
                }
            }

            @Override
            public void handleExceptionResponse(String errMsg) {
                ToastUtils.show(PendingOrderListActivity.this, errMsg);
            }
        });
    }

    protected void initEmptyView(int mode) {
        emptyView.setVisibility(View.VISIBLE);
        emptyView.setMode(mode);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqSearchOrderList();
            }
        });
    }
}
