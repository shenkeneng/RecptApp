package com.frxs.receipt.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.frxs.core.utils.MathUtils;
import com.frxs.core.utils.ToastUtils;
import com.frxs.core.widget.EmptyView;
import com.frxs.receipt.R;
import com.frxs.receipt.comms.GlobelDefines;
import com.frxs.receipt.model.ReceivedOrderDetail;
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
 *     time   : 2017/06/08
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class OrderDetailActivity extends MyBaseActivity {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.order_id_tv)
    TextView orderIdTv;
    @BindView(R.id.supplier_tv)
    TextView supplierTv;
    @BindView(R.id.product_list_view)
    ListView productListView;
    @BindView(R.id.actual_received_num_tv)
    TextView actualReceivedNumTv;
    @BindView(R.id.order_num_tv)
    TextView orderNumTv;
    @BindView(R.id.content_layout)
    LinearLayout contentLayout;
    @BindView(R.id.empty_view)
    EmptyView emptyView;
    @BindView(R.id.iv_signature)
    ImageView signStateIv;
    @BindView(R.id.action_right_tv)
    TextView actionRightTv;
    @BindView(R.id.tv_amt)
    TextView amtTv;
    @BindView(R.id.order_state_tv)
    TextView orderStatusTv;
    private Adapter adapter;
    private String buyID;
    private String preBuyID;
    private String signID;
    private int isSign;
    private int status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        titleTv.setText(R.string.order_detail);
        actionRightTv.setVisibility(View.VISIBLE);

        adapter = new Adapter<ReceivedOrderDetail.ItemBean>(this, R.layout.item_received_order_detial) {
            @Override
            protected void convert(AdapterHelper helper, ReceivedOrderDetail.ItemBean item) {
                helper.setText(R.id.product_name_tv, item.getProductName());
                helper.setText(R.id.order_num_tv, String.format(getString(R.string.item_order_num), MathUtils.doubleTrans(item.getPrepareQty()), item.getPrepareUnit()));
                helper.setText(R.id.actual_received_num_tv, String.format(getString(R.string.item_actual_received_num), MathUtils.doubleTrans(item.getReceiveQty()), item.getReceiveUnit()));
            }
        };
        productListView.setAdapter(adapter);

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            buyID = bundle.getString("BuyID");
            preBuyID = bundle.getString("PreBuyID");
            signID = bundle.getString("sign_id");
            status = bundle.getInt("status");
            String statusStr = bundle.getString("status_str");
            if (!TextUtils.isEmpty(buyID)) {
                reqReceivedOrderDetail(buyID);
            }
            if (!TextUtils.isEmpty(statusStr)) {
                orderStatusTv.setText(statusStr);
            }
        }
    }

    private void reqReceivedOrderDetail(String buyId) {
        showProgressDialog();
        AjaxParams params = new AjaxParams();
        params.put("BuyID", buyId);
        params.put("WID", getWID());
        params.put("ReceiveUserID", getUserID());

        getService().GetReceivedCompletedDetails(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<ReceivedOrderDetail>>() {
            @Override
            public void onResponse(ApiResponse<ReceivedOrderDetail> result, int code, String msg) {
                if (result.isSuccessful()) {
                    ReceivedOrderDetail orderDetail = result.getData();
                    if (null != orderDetail) {
                        emptyView.setVisibility(View.GONE);
                        updateUI(orderDetail);
                    } else {
                        initEmptyView(EmptyView.MODE_NODATA);
                        ToastUtils.show(OrderDetailActivity.this, "返回数据为空");
                    }
                } else {
                    initEmptyView(EmptyView.MODE_ERROR);
                    ToastUtils.show(OrderDetailActivity.this, "请求结果错误：" + result.getInfo());
                }

                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<ApiResponse<ReceivedOrderDetail>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                initEmptyView(EmptyView.MODE_ERROR);
            }
        });
    }

    protected void initEmptyView(int mode) {
        emptyView.setVisibility(View.VISIBLE);
        emptyView.setMode(mode);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(buyID)) {
                    reqReceivedOrderDetail(buyID);
                }
            }
        });
    }

    private void updateUI(ReceivedOrderDetail orderDetail) {
        actionRightTv.setText(orderDetail.getAutoPrintNum() > -1 ? "再次打印" : "打印");
        orderIdTv.setText(TextUtils.isEmpty(orderDetail.getBuyID()) ? "" : orderDetail.getBuyID());
        supplierTv.setText(TextUtils.isEmpty(orderDetail.getVendorName()) ? "" : orderDetail.getVendorCode() + "-" + orderDetail.getVendorName());
        signID = orderDetail.getSignID();
        isSign = orderDetail.getIsSign();
        if (isSign == 0) {//签名状态标识 0：未签名  1：已签名
            signStateIv.setImageResource(R.mipmap.icon_un_sign);
        } else {
            signStateIv.setImageResource(R.mipmap.icon_sign);
        }
        switch (status) {
            case 0:
                amtTv.setText("过账");
                amtTv.setBackgroundResource(R.color.frxs_gray);
                amtTv.setClickable(false);
                break;

            case 1:
                amtTv.setText("过账");
                amtTv.setBackgroundResource(R.color.red);
                amtTv.setClickable(true);
                break;

            case 2:
                amtTv.setText("合计");
                amtTv.setBackgroundResource(R.color.red);
                amtTv.setClickable(false);
                break;

            case 3:
                amtTv.setText("合计");
                amtTv.setBackgroundResource(R.color.red);
                amtTv.setClickable(false);
                break;

            default:
                break;
        }

        double actualNum = 0;
        double orderNum = 0;
        int id = -1;
        List<ReceivedOrderDetail.ItemBean> itemBeanList = orderDetail.getItems();
        if (null != itemBeanList) {
            for (ReceivedOrderDetail.ItemBean item : itemBeanList) {
                actualNum = MathUtils.add(actualNum, item.getReceiveQty());
                if (id != item.getProductId()) {
                    id = item.getProductId();
                    orderNum = MathUtils.add(orderNum, item.getPrepareQty());
                }
            }
            adapter.replaceAll(itemBeanList);
        }
        actualReceivedNumTv.setText(String.format(getString(R.string.actual_received_num), MathUtils.doubleTrans(actualNum)));
        orderNumTv.setText(String.format(getString(R.string.order_num), MathUtils.doubleTrans(orderNum)));
    }

    @OnClick({R.id.action_back_tv, R.id.tv_amt, R.id.action_right_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_back_tv:
                finish();
                break;

            case R.id.tv_amt:
                PostingOrderCompleted();
                break;

            case R.id.action_right_tv:
                printOrder();
                break;

            default:
                break;
        }
    }

    /**
     * 检测GPS是否打开
     *
     * @return
     */
    private boolean checkGPSIsOpen() {
        boolean isOpen;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isOpen = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        return isOpen;
    }

    /**
     * 跳转GPS设置
     */
    private void openGPSSettings() {
        if (checkGPSIsOpen()) {
            if (hasCameraPermissions() && hasPhoneStatePermissions()) {// && hasGPSPermissions()
                gotoActivity(SignatureActivity.class);
            }
        } else {
            //没有打开则弹出对话框
            AlertDialog dialog = new AlertDialog.Builder(OrderDetailActivity.this)
                    .setTitle("提示")
                    .setMessage("需进入下一步必须打开GPS功能。")
                    .setPositiveButton("去开启",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //跳转GPS设置界面
                                    //mActivity.gotoActivityForResult();
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivityForResult(intent, GlobelDefines.REQ_GPS_CODE);
                                }
                            })
                    // 拒绝, 退出应用
                    .setNegativeButton("取消", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    private void PostingOrderCompleted(){
        showProgressDialog();
        AjaxParams params = new AjaxParams();
        params.put("BuyID", buyID);
        params.put("WID", getWID());
        params.put("UserId", getUserID());
        params.put("UserName", getUserName());

        getService().PostingOrderCompleted(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<String>>() {
            @Override
            public void onResponse(ApiResponse<String> result, int code, String msg) {
                dismissProgressDialog();
                if (result.isSuccessful()) {
                    ToastUtils.show(OrderDetailActivity.this, "过账成功");
                    orderStatusTv.setText("过账");
                    amtTv.setText("合计");
                    amtTv.setBackgroundResource(R.color.red);
                    amtTv.setClickable(false);
                } else {
                    ToastUtils.show(OrderDetailActivity.this, result.getInfo());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                ToastUtils.show(OrderDetailActivity.this, t.getMessage());
            }
        });
    }

    private void printOrder() {
        showProgressDialog();
        AjaxParams params = new AjaxParams();
        params.put("BuyID", buyID);
        params.put("PreBuyID", preBuyID);
        params.put("WID", getWID());
        getService().PrintOrder(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<ArrayList<ApiResponse<String>>>>() {
            @Override
            public void onResponse(ApiResponse<ArrayList<ApiResponse<String>>> result, int code, String msg) {
                dismissProgressDialog();
                if (result.isSuccessful()) {
                    actionRightTv.setText("再次打印");
                    ToastUtils.show(OrderDetailActivity.this, "已添加到打印队列");
                } else {
                    ToastUtils.show(OrderDetailActivity.this, result.getInfo());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ArrayList<ApiResponse<String>>>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                ToastUtils.show(OrderDetailActivity.this, t.getMessage());
            }
        });
    }
}
