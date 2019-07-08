package com.frxs.receipt.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.frxs.core.utils.MathUtils;
import com.frxs.core.utils.SortListUtil;
import com.frxs.core.utils.ToastUtils;
import com.frxs.receipt.R;
import com.frxs.receipt.model.RcvOrderSectionListItem;
import com.frxs.receipt.model.ReceivedOrder;
import com.frxs.receipt.model.SectionListItem;
import com.frxs.receipt.rest.model.AjaxParams;
import com.frxs.receipt.rest.model.ApiResponse;
import com.frxs.receipt.rest.service.SimpleCallback;
import com.pacific.adapter.Adapter;
import com.pacific.adapter.AdapterHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
public class ReceivedOrderListActivity extends MyBaseActivity {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.received_order_lv)
    ListView receivedOrderLv;
    @BindView(R.id.action_right_tv)
    TextView actionRightTv;
    private Adapter adapter;
    private List<RcvOrderSectionListItem> sectionList = new ArrayList<RcvOrderSectionListItem>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiving_list);
        ButterKnife.bind(this);
        // 筛选条件
        actionRightTv.setVisibility(View.VISIBLE);
        actionRightTv.setText("查看全部");
        actionRightTv.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(ReceivedOrderListActivity.this, R.mipmap.navi_unfold), null);

        initData();
    }

    private void initData() {
        titleTv.setText(R.string.receipt_details);
        adapter = new Adapter<RcvOrderSectionListItem>(this, R.layout.item_received_order_list) {
            @Override
            protected void convert(AdapterHelper helper, RcvOrderSectionListItem item) {
                ReceivedOrder.ItemsBean orderItem = (ReceivedOrder.ItemsBean)item.getItem();
                if (item.getType() == SectionListItem.SECTION) {
                    helper.setVisible(R.id.head_layout, View.VISIBLE);
                    helper.setText(R.id.tv_order_date, item.getSection());
                    helper.setText(R.id.tv_order_count, String.format(getString(R.string.order_num_0), MathUtils.doubleTrans(item.getTotalPrepareQty())));
                    helper.setText(R.id.tv_actual_number, String.format(getString(R.string.actual_received_num), MathUtils.doubleTrans(item.getTotalReceiveQty())));
                } else {
                    helper.setVisible(R.id.head_layout, View.GONE);
                }
                helper.setText(R.id.order_id_tv, orderItem.getBuyID());
                String supplierInfo = orderItem.getVendorCode() + "-" + orderItem.getVendorName();
                helper.setText(R.id.supplier_tv, supplierInfo);
                helper.setText(R.id.order_num_tv, String.format(getString(R.string.item_order_num), MathUtils.doubleTrans(orderItem.getPrepareQty()), ""));
                helper.setText(R.id.actual_received_num_tv, String.format(getString(R.string.item_actual_received_num), MathUtils.doubleTrans(orderItem.getReceiveQty()), ""));
            }
        };
        receivedOrderLv.setAdapter(adapter);
        receivedOrderLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReceivedOrder.ItemsBean item = (ReceivedOrder.ItemsBean)((RcvOrderSectionListItem)parent.getAdapter().getItem(position)).getItem();
                Bundle bundle = new Bundle();
                bundle.putString("BuyID", item.getBuyID());
                gotoActivity(OrderDetailActivity.class, false, bundle);
            }
        });

        reqReceivedOrderList();
    }

    private void packageSectionList(List<ReceivedOrder.ItemsBean> itemList) {
        sectionList.clear();
        int sectionPosition = -1;

        for (int i = 0; i < itemList.size(); i++) {
            String currentStr = itemList.get(i).getOrderDate();
            String previewStr = (i - 1) >= 0 ? itemList.get(i - 1).getOrderDate() : " ";
            if (!previewStr.equals(currentStr)) {
                RcvOrderSectionListItem sectionItem = new RcvOrderSectionListItem(itemList.get(i), SectionListItem.SECTION, itemList.get(i).getOrderDate());
                double PrepareQty = itemList.get(i).getPrepareQty();
                double ReceiveQty = itemList.get(i).getReceiveQty();
                sectionItem.setTotalPrepareQty(PrepareQty);
                sectionItem.setTotalReceiveQty(ReceiveQty);
                sectionList.add(sectionItem);
                sectionPosition = i;
            } else {
                RcvOrderSectionListItem listItem = new RcvOrderSectionListItem(itemList.get(i), SectionListItem.ITEM, itemList.get(i).getOrderDate());
                if (sectionPosition >= 0) {
                    RcvOrderSectionListItem sectionItem = sectionList.get(sectionPosition);
                    double totalPrepareQty = sectionItem.getTotalPrepareQty() + itemList.get(i).getPrepareQty();
                    double totalReceiveQty = sectionItem.getTotalReceiveQty() + itemList.get(i).getReceiveQty();
                    sectionItem.setTotalPrepareQty(totalPrepareQty);
                    sectionItem.setTotalReceiveQty(totalReceiveQty);
                    sectionList.add(listItem);
                }
            }
        }
    }

    private void reqReceivedOrderList() {
        showProgressDialog();
        AjaxParams params = new AjaxParams();
        params.put("WID", getWID());
        params.put("ReceiveUserID", getUserID());
        getService().GetReceivedCompletedOrders(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<ReceivedOrder>>() {
            @Override
            public void onResponse(ApiResponse<ReceivedOrder> result, int code, String msg) {
                if (result.isSuccessful()) {
                    ReceivedOrder orderDetail = result.getData();
                    if (null != orderDetail) {
                        List<ReceivedOrder.ItemsBean> orderList = orderDetail.getItems();
                        if (orderList != null) {
                            SortListUtil.sort(orderList, "getOrderDate", SortListUtil.ORDER_BY_DESC);
                            packageSectionList(orderList);
                            adapter.replaceAll(sectionList);
                        }
                    } else {
                        ToastUtils.show(ReceivedOrderListActivity.this, "返回数据为空");
                    }
                } else {
                    ToastUtils.show(ReceivedOrderListActivity.this, "请求结果错误：" + result.getInfo());
                }

                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<ApiResponse<ReceivedOrder>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
            }
        });
    }

    @OnClick(R.id.action_back_tv)
    public void onClick() {
        finish();
    }
}
