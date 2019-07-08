package com.frxs.receipt.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.frxs.core.utils.MathUtils;
import com.frxs.core.utils.ToastUtils;
import com.frxs.core.widget.EmptyView;
import com.frxs.receipt.R;
import com.frxs.receipt.fragment.ProductListFragment;
import com.frxs.receipt.greendao.entity.ProductEntity;
import com.frxs.receipt.model.ProductInfo;
import com.frxs.receipt.rest.model.ApiResponse;
import com.frxs.receipt.rest.service.RequestListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.frxs.receipt.R.id.action_done_btn;
import static com.frxs.receipt.R.id.title_tv;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/06/08
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class PendingOrderDetailActivity extends MyBaseActivity {

    @BindView(R.id.total_row_tv)
    TextView totalRowTv;
    @BindView(R.id.total_product_tv)
    TextView totalProductTv;
//    @BindView(R.id.product_list_view)
//    ListView productListView;
    @BindView(action_done_btn)
    Button actionDoneBtn;
    @BindView(title_tv)
    TextView titleTv;
//    private Adapter adapter;
    private List<ProductEntity> productEntityList;
    private String preBuyId;
    private ProductListFragment productListFragment;
    private List<ProductEntity> searchResultProductList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_order_detail);
        ButterKnife.bind(this);

        productListFragment = (ProductListFragment) getSupportFragmentManager().findFragmentById(R.id.product_list_fragment);
        initData();
    }

    private void initData() {
        titleTv.setText(R.string.order_detail);
        actionDoneBtn.setText(R.string.start_receive);
        totalRowTv.setText(String.format(getString(R.string.total_product_row), 0));
        totalProductTv.setText(String.format(getString(R.string.total_product_num), "0"));

//        adapter = new Adapter<ProductEntity>(this, R.layout.item_product) {
//            @Override
//            protected void convert(AdapterHelper helper, ProductEntity item) {
//                int position = helper.getPosition();
//                if (position == 0) {
//                    helper.setVisible(R.id.supplier_tv, View.VISIBLE);
//                    helper.setText(R.id.supplier_tv, item.getVendorName());
//                } else {
//                    helper.setVisible(R.id.supplier_tv, View.GONE);
//                }
//                helper.setVisible(R.id.received_switch_btn, View.GONE);
//                helper.setText(R.id.product_name_tv, item.getProductName());
//                helper.setText(R.id.product_code_tv, item.getSKU());
//                helper.setText(R.id.product_barcode_tv, item.getBarCode());
//                helper.setText(R.id.product_location_tv, item.getShelfCode());
//                helper.setText(R.id.product_package_tv, String.format("%1$.2f", item.getBuyPackingQty()));
//                helper.setText(R.id.product_num_tv, String.format("%1$.2f%2$s", item.getBuyQty(), item.getBuyUnit()));
//            }
//        };
//        productListView.setAdapter(adapter);

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            preBuyId = bundle.getString("PreBuyID");
            if (!TextUtils.isEmpty(preBuyId)) {
                actionDoneBtn.setEnabled(true);
                reqOrderDetails(preBuyId, 6, new RequestListener() {
                    @Override
                    public void handleRequestResponse(ApiResponse result) {
                        if (result.isSuccessful()) {
                            ProductInfo productInfo = (ProductInfo) result.getData();
                            if (null != productInfo && productInfo.getItems().size() > 0) {
                                productEntityList = productInfo.getItems();
//                                adapter.replaceAll(productEntityList);
                                productListFragment.refreshData(productEntityList, 6);

                                int totalRow = productEntityList.size();
                                double totalProductNum = 0;
                                for (ProductEntity item : productEntityList) {
                                    totalProductNum = MathUtils.add(totalProductNum, item.getUnitQty());
                                }
                                totalRowTv.setText(String.format(getString(R.string.total_product_row), totalRow));
                                totalProductTv.setText(String.format(getString(R.string.total_product_num), MathUtils.doubleTrans(totalProductNum)));
                            } else {
                                productListFragment.initEmptyView(EmptyView.MODE_NODATA);
                                ToastUtils.show(PendingOrderDetailActivity.this, "返回数据为空");
                            }
                        } else {
                            productListFragment.initEmptyView(EmptyView.MODE_ERROR);
                            ToastUtils.show(PendingOrderDetailActivity.this, "请求结果错误：" + result.getInfo());
                        }
                    }

                    @Override
                    public void handleExceptionResponse(String errMsg) {
                        productListFragment.initEmptyView(EmptyView.MODE_ERROR);
                    }
                });
            } else {
                actionDoneBtn.setEnabled(false);
            }
        }
    }

    @OnClick({R.id.action_back_tv, action_done_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_back_tv:
                finish();
                break;
            case action_done_btn:
                reqStartReceiveOrder(preBuyId, new RequestListener() {
                    @Override
                    public void handleRequestResponse(ApiResponse result) {
                        if (result.isSuccessful()) {
                            Intent intent = new Intent();
                            intent.putExtra("ResultCount", 1);
                            intent.putExtra("PreBuyID", preBuyId);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        } else {
                            ToastUtils.show(PendingOrderDetailActivity.this, result.getInfo());
                        }
                    }

                    @Override
                    public void handleExceptionResponse(String errMsg) {

                    }
                });
                break;

            default:
                break;
        }
    }
}
