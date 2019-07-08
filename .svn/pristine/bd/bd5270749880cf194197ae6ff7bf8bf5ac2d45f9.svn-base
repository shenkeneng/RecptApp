package com.frxs.receipt.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.frxs.core.utils.ToastUtils;
import com.frxs.core.widget.EmptyView;
import com.frxs.receipt.R;
import com.frxs.receipt.comms.GlobelDefines;
import com.frxs.receipt.greendao.DBHelper;
import com.frxs.receipt.greendao.entity.ProductEntity;
import com.frxs.receipt.greendao.gen.ProductEntityDao;
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
 * Created by Chentie on 2017/11/14.
 */

public class AllOrderListActivity extends MyBaseActivity{

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.search_result_lv)
    ListView searchResultOrderLv;
    @BindView(R.id.search_keywords_tv)
    TextView searchKeywordsTv;
    @BindView(R.id.empty_view)
    EmptyView emptyView;
    private Adapter adapter;
    private List<PendingOrder.ItemsBean> searchResultOrderList = new ArrayList<PendingOrder.ItemsBean>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_order_list);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        titleTv.setText("扫描收货订单");
        emptyView.setVisibility(View.GONE);
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            String searchContent = bundle.getString("search_content");
            if (!TextUtils.isEmpty(searchContent)) {
                searchKeywordsTv.setText(String.format(getString(R.string.pending_search_result), searchContent));
            }
            searchResultOrderList = (ArrayList<PendingOrder.ItemsBean>) bundle.getSerializable("search_result_list");
        }
        adapter = new Adapter<PendingOrder.ItemsBean>(this, R.layout.item_receiving_order) {
            @Override
            protected void convert(AdapterHelper helper, PendingOrder.ItemsBean item) {
                helper.setText(R.id.order_time_tv, item.getPostingTime().replace("年","-").replace("月","-").replace("日",""));
                helper.setText(R.id.order_id_tv, item.getPreBuyID());
                String supplierInfo = item.getVendorCode() + "-" + item.getVendorName();
                helper.setText(R.id.supplier_tv, supplierInfo);
                helper.setText(R.id.total_row_tv, String.format(getString(R.string.total_product_row), item.getDetailsCount()));
                List<ProductEntity> localProductList = DBHelper.getProductEntityService().queryBuilder().where(ProductEntityDao.Properties.PreBuyID.eq(item.getPreBuyID()), ProductEntityDao.Properties.IsReceived.eq(true)).list();
                int receivedCount = localProductList != null ? localProductList.size() : 0;
                helper.setVisible(R.id.label_tv, View.GONE);
                if (item.getStatus() == 6) {
                    helper.setVisible(R.id.tv_order_status, View.VISIBLE);
                    helper.setText(R.id.total_product_tv, String.format(getString(R.string.total_product_num), String.valueOf(item.getProductTotalQty())));
                } else {
                    helper.setVisible(R.id.tv_order_status, View.GONE);
                    helper.setText(R.id.total_product_tv, String.format(getString(R.string.received_product_num), String.valueOf(receivedCount)));
                }
                //helper.setVisible(R.id.total_product_tv, item.getStatus() == 1 ? View.VISIBLE : View.GONE);

                if (TextUtils.isEmpty(item.getRemark())) {
                    helper.setVisible(R.id.remark_layout, View.GONE);
                } else {
                    helper.setVisible(R.id.remark_layout, View.VISIBLE);
                    helper.setText(R.id.remark_tv, getResources().getString(R.string.remark) + "：" + item.getRemark());
                }
            }
        };
        searchResultOrderLv.setAdapter(adapter);
        adapter.replaceAll(searchResultOrderList);

        searchResultOrderLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final PendingOrder.ItemsBean item = (PendingOrder.ItemsBean) parent.getAdapter().getItem(position);
                if (item.getStatus() == 8) {
                    gotoReceiveingOrderDeailActivity(item);
                } else {
                    // 开始收货
                    reqStartReceiveOrder(item.getPreBuyID(), new RequestListener() {
                        @Override
                        public void handleRequestResponse(ApiResponse result) {
                            if (result.isSuccessful()) {
                                EventBus.getDefault().post(1);
                                //删除已经开始收货的订单
                                for (PendingOrder.ItemsBean order : searchResultOrderList) {
                                    if (item.getPreBuyID().equals(order.getPreBuyID())) {
                                        order.setStatus(8);
                                        break;
                                    }
                                }
                                adapter.replaceAll(searchResultOrderList);
                                if (searchResultOrderList.size() == 0) {
                                    searchKeywordsTv.setText(String.format(getString(R.string.pending_search_result), "暂无"));
                                }
                                ToastUtils.show(AllOrderListActivity.this, "开始收货操作成功");
                                gotoReceiveingOrderDeailActivity(item);
                            } else {
                                ToastUtils.show(AllOrderListActivity.this, result.getInfo());
                            }
                        }

                        @Override
                        public void handleExceptionResponse(String errMsg) {

                        }
                    });
                }
            }
        });
    }

    private void gotoReceiveingOrderDeailActivity(PendingOrder.ItemsBean item) {
        Bundle bundle = new Bundle();
        bundle.putString("PreBuyID", item.getPreBuyID());
        gotoActivityForResult(ReceivingOrderDetailActivity.class, false, bundle, GlobelDefines.REQ_CODE_ORDER_DETAIL);
    }

    @OnClick({R.id.action_back_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_back_tv:
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String preBuyId = data.getStringExtra("prebuyid");
                if (!TextUtils.isEmpty(preBuyId)) {
                    for (int i = 0; i < searchResultOrderList.size(); i++)
//                    for (PendingOrder.ItemsBean order : searchResultOrderList) {
                        if (searchResultOrderList.get(i).getPreBuyID().equals(preBuyId)) {
                            searchResultOrderList.remove(i);
                            break;
                        }
                    }
                    adapter.replaceAll(searchResultOrderList);
                }
            }
        }
}
