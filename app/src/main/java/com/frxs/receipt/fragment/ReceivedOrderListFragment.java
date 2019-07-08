package com.frxs.receipt.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.PaintDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.frxs.core.utils.MathUtils;
import com.frxs.core.utils.SortListUtil;
import com.frxs.core.utils.ToastUtils;
import com.frxs.core.widget.EmptyView;
import com.frxs.receipt.R;
import com.frxs.receipt.activity.LookQrCodeActivity;
import com.frxs.receipt.activity.LookSignatureActivity;
import com.frxs.receipt.activity.MyBaseActivity;
import com.frxs.receipt.activity.OrderDetailActivity;
import com.frxs.receipt.activity.SignatureActivity;
import com.frxs.receipt.comms.GlobelDefines;
import com.frxs.receipt.model.RcvOrderSectionListItem;
import com.frxs.receipt.model.ReceivedOrder;
import com.frxs.receipt.model.SectionListItem;
import com.frxs.receipt.rest.model.AjaxParams;
import com.frxs.receipt.rest.model.ApiResponse;
import com.frxs.receipt.rest.service.SimpleCallback;
import com.pacific.adapter.Adapter;
import com.pacific.adapter.AdapterHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Created by Chentie on 2017/8/3.
 */

public class ReceivedOrderListFragment extends MyBaseFragment {

    public static final String TAG = ReceivedOrderListFragment.class.getSimpleName();
    @BindView(R.id.action_back_tv)
    TextView actionBackTv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.received_order_lv)
    ListView receivedOrderLv;
    @BindView(R.id.action_right_tv)
    TextView actionRightTv;
    @BindView(R.id.empty_view)
    EmptyView emptyView;
    private Adapter adapter;
    private List<RcvOrderSectionListItem> sectionList = new ArrayList<RcvOrderSectionListItem>();
    private List<String> stateList;
    private int orderType = -1;// 查询条件 默认为-1：查看全部   0:未签   1：已签

    @Override
    protected int getLayoutId() {
        return R.layout.activity_receiving_list;
    }

    @Override
    protected void initViews(View view) {
        titleTv.setText(R.string.receipt_details);
        actionBackTv.setVisibility(View.INVISIBLE);
        actionRightTv.setVisibility(View.VISIBLE);
        actionRightTv.setText("查看全部");
        actionRightTv.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(mActivity, R.mipmap.navi_fold), null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initData() {
        stateList = new ArrayList<String>(Arrays.asList(getString(R.string.all), getString(R.string.unsigned), getString(R.string.signed)));
        adapter = new Adapter<RcvOrderSectionListItem>(mActivity, R.layout.item_received_order_list) {
            @Override
            protected void convert(final AdapterHelper helper, RcvOrderSectionListItem item) {
                final ReceivedOrder.ItemsBean orderItem = (ReceivedOrder.ItemsBean) item.getItem();
                if (item.getType() == SectionListItem.SECTION) {
                    helper.setVisible(R.id.head_layout, View.VISIBLE);
                    helper.setText(R.id.tv_order_date, item.getSection());
                    helper.setText(R.id.tv_order_count, String.format(getString(R.string.order_num_0), String.valueOf(item.getTotalOrderSize())));
                    helper.setText(R.id.tv_actual_number, String.format(getString(R.string.actual_received_num), MathUtils.doubleTrans(item.getTotalReceiveQty())));
                } else {
                    helper.setVisible(R.id.head_layout, View.GONE);
                }
                helper.setText(R.id.tv_print, orderItem.getAutoPrintNum() > -1 ? "再次打印" : "打印");
                helper.setText(R.id.order_id_tv, orderItem.getBuyID());
                String supplierInfo = orderItem.getVendorCode() + "-" + orderItem.getVendorName();
                helper.setText(R.id.supplier_tv, supplierInfo);
                helper.setText(R.id.actual_received_num_tv, String.format(getString(R.string.item_actual_received_num), MathUtils.doubleTrans(orderItem.getReceiveQty()), ""));
                helper.setTextColor(R.id.order_state, orderItem.getStatus() == 2 ? getResources().getColor(R.color.frxs_gray) : getResources().getColor(R.color.frxs_red));
                helper.setText(R.id.order_state, orderItem.getStatusStr());
                final TextView tvSignature = helper.getView(R.id.tv_signature);
                tvSignature.setVisibility(View.VISIBLE);
                if (orderItem.getIsSign() == 0) {
                    tvSignature.setTextColor(getResources().getColor(R.color.frxs_white));
                    tvSignature.setBackgroundResource(R.drawable.shape_red_btn);// 未签
                    tvSignature.setText(R.string.sign);
                    if (orderItem.getStatus() == 0 || orderItem.getStatus() == 2) {
                        tvSignature.setVisibility(View.GONE);
                    }
                } else {
                    tvSignature.setTextColor(getResources().getColor(R.color.frxs_gray));
                    tvSignature.setBackgroundResource(R.drawable.shape_gray_storke_btn);// 未签
                    tvSignature.setText(R.string.look_sign);
                }

                /**
                 * 订单签名
                 */
                helper.setOnClickListener(R.id.tv_signature, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (orderItem.getIsSign() == 0) {
                            if (checkGPSIsOpen()) {
                                if (((MyBaseActivity) mActivity).hasCameraPermissions() && ((MyBaseActivity) mActivity).hasPhoneStatePermissions()) {// && ((MyBaseActivity) mActivity).hasGPSPermissions()
                                    Bundle bundle = new Bundle();
                                    bundle.putString("order_id", orderItem.getBuyID());
                                    mActivity.gotoActivity(SignatureActivity.class, false, bundle);
                                }
                            } else {
                                openGPSSettings();
                            }
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putString("sign_id", orderItem.getSignID());
                            mActivity.gotoActivity(LookSignatureActivity.class, false, bundle);
                        }
                    }
                });

                helper.setOnClickListener(R.id.tv_scan, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("BuyID", orderItem.getBuyID());
                        mActivity.gotoActivity(LookQrCodeActivity.class, false, bundle);
                    }
                });

                helper.setOnClickListener(R.id.tv_print, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        printOrder(orderItem, (TextView)helper.getView(R.id.tv_print));
                    }
                });
            }
        };
        receivedOrderLv.setAdapter(adapter);
        receivedOrderLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReceivedOrder.ItemsBean item = (ReceivedOrder.ItemsBean) ((RcvOrderSectionListItem) parent.getAdapter().getItem(position)).getItem();
                Bundle bundle = new Bundle();
                bundle.putString("BuyID", item.getBuyID());
                bundle.putString("PreBuyID", item.getPreBuyID());
                bundle.putString("sign_id", item.getSignID());
                bundle.putInt("sign_state", item.getIsSign());
                bundle.putInt("status", item.getStatus());
                bundle.putString("status_str", item.getStatusStr());
                mActivity.gotoActivity(OrderDetailActivity.class, false, bundle);
            }
        });
    }

    private void printOrder(ReceivedOrder.ItemsBean itemsBean, final TextView printTv) {
        mActivity.showProgressDialog();
        AjaxParams params = new AjaxParams();
        params.put("BuyID", itemsBean.getBuyID());
        params.put("PreBuyID", itemsBean.getPreBuyID());
        params.put("WID", getWID());
        getService().PrintOrder(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<ArrayList<ApiResponse<String>>>>() {
            @Override
            public void onResponse(ApiResponse<ArrayList<ApiResponse<String>>> result, int code, String msg) {
                mActivity.dismissProgressDialog();
                if (result.isSuccessful()) {
                    printTv.setText("再次打印");
                    ToastUtils.show(mActivity, "已添加到打印队列");
                } else {
                    ToastUtils.show(mActivity, result.getInfo());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ArrayList<ApiResponse<String>>>> call, Throwable t) {
                super.onFailure(call, t);
                mActivity.dismissProgressDialog();
                ToastUtils.show(mActivity, t.getMessage());
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            reqReceivedOrderList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!this.isHidden()) {
            reqReceivedOrderList();
        }
    }

    public static ReceivedOrderListFragment newInstance(String argument) {
        Bundle bundle = new Bundle();
        bundle.putString(TAG, argument);
        ReceivedOrderListFragment contentFragment = new ReceivedOrderListFragment();
        contentFragment.setArguments(bundle);
        return contentFragment;
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
                sectionItem.setTotalOrderSize(1);
                sectionList.add(sectionItem);
                sectionPosition = i;
            } else {
                RcvOrderSectionListItem listItem = new RcvOrderSectionListItem(itemList.get(i), SectionListItem.ITEM, itemList.get(i).getOrderDate());
                if (sectionPosition >= 0) {
                    RcvOrderSectionListItem sectionItem = sectionList.get(sectionPosition);
                    double totalPrepareQty = MathUtils.add(sectionItem.getTotalPrepareQty(), itemList.get(i).getPrepareQty());
                    double totalReceiveQty = MathUtils.add(sectionItem.getTotalReceiveQty(), itemList.get(i).getReceiveQty());
                    sectionItem.setTotalPrepareQty(totalPrepareQty);
                    sectionItem.setTotalReceiveQty(totalReceiveQty);
                    sectionItem.setTotalOrderSize(sectionItem.getTotalOrderSize() + 1);
                    sectionList.add(listItem);
                }
            }
        }
    }

    private void reqReceivedOrderList() {
        mActivity.showProgressDialog();
        AjaxParams params = new AjaxParams();
        params.put("WID", getWID());
        params.put("ReceiveUserID", getUserID());
        if (orderType >= 0) {
            params.put("IsSign", orderType);
        }
        getService().GetReceivedCompletedOrders(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<ReceivedOrder>>() {
            @Override
            public void onResponse(ApiResponse<ReceivedOrder> result, int code, String msg) {
                if (result.isSuccessful()) {
                    ReceivedOrder orderDetail = result.getData();
                    if (null != orderDetail) {
                        List<ReceivedOrder.ItemsBean> orderList = orderDetail.getItems();
                        if (orderList != null && orderList.size() > 0) {
                            emptyView.setVisibility(View.GONE);
//                            SortListUtil.sort(orderList, "getOrderDate", SortListUtil.ORDER_BY_DESC);
                            packageSectionList(orderList);
                            adapter.replaceAll(sectionList);
                        } else {
                            initEmptyView(EmptyView.MODE_NODATA);
                        }
                    } else {
                        initEmptyView(EmptyView.MODE_NODATA);
                        ToastUtils.show(mActivity, "返回数据为空");
                    }
                } else {
                    initEmptyView(EmptyView.MODE_ERROR);
                    ToastUtils.show(mActivity, "请求结果错误：" + result.getInfo());
                }
                mActivity.dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<ApiResponse<ReceivedOrder>> call, Throwable t) {
                super.onFailure(call, t);
                mActivity.dismissProgressDialog();
                initEmptyView(EmptyView.MODE_ERROR);
            }
        });
    }

    @OnClick({R.id.action_back_tv, R.id.action_right_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_back_tv:
                mActivity.finish();
                break;

            case R.id.action_right_tv:
                showDropDownPopWindow();
                break;
            default:
                break;
        }
    }

    private void showDropDownPopWindow() {
        View contentView = LayoutInflater.from(mActivity).inflate(R.layout.drop_down_view, null);
        final PopupWindow filterPointsPw = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ListView filterConditionLv = (ListView)contentView.findViewById(R.id.list_view);
        filterPointsPw.setBackgroundDrawable(new PaintDrawable());
        filterPointsPw.setFocusable(true);
        filterPointsPw.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                actionRightTv.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(mActivity, R.mipmap.navi_fold), null);
            }
        });

        filterConditionLv.setAdapter(new Adapter<String>(mActivity, stateList, R.layout.pop_menu_item) {
            @Override
            protected void convert(AdapterHelper helper, String item) {
                helper.setText(R.id.menu_item_tv, item);
            }
        });
        filterConditionLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getAdapter().getItem(position);
                if (!TextUtils.isEmpty(item)) {
                    actionRightTv.setText(item);
                    switch (position) {
                        case 0: {
                            orderType = -1;
                            break;
                        }
                        case 1: {
                            orderType = 0;
                            break;
                        }
                        case 2: {
                            orderType = 1;
                            break;
                        }
                        default:
                            break;
                    }
                    reqReceivedOrderList();
                }
                filterPointsPw.dismiss();
            }
        });

        if (filterPointsPw.isShowing()) {
            filterPointsPw.dismiss();
        } else {
            actionRightTv.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(mActivity, R.mipmap.navi_unfold), null);
            filterConditionLv.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            filterPointsPw.setWidth(filterConditionLv.getMeasuredWidth());
            filterPointsPw.showAsDropDown(actionRightTv, 0, 10);
        }
    }

    /**
     * 检测GPS是否打开
     *
     * @return
     */
    private boolean checkGPSIsOpen() {
        boolean isOpen;
        LocationManager locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        isOpen = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        return isOpen;
    }

    /**
     * 跳转GPS设置
     */
    private void openGPSSettings() {

        if (checkGPSIsOpen()) {

            if (((MyBaseActivity) mActivity).hasCameraPermissions() && ((MyBaseActivity) mActivity).hasPhoneStatePermissions()) {// && ((MyBaseActivity)mActivity).hasGPSPermissions()
                mActivity.gotoActivity(SignatureActivity.class);
            }
        } else {
            //没有打开则弹出对话框
            AlertDialog dialog = new AlertDialog.Builder(mActivity)
                    .setTitle("提示")
                    .setMessage("需进入下一步必须打开GPS功能。")
                    .setPositiveButton("去开启",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //跳转GPS设置界面
                                    //mActivity.gotoActivityForResult();
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    mActivity.startActivityForResult(intent, GlobelDefines.REQ_GPS_CODE);
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

    protected void initEmptyView(int mode) {
        emptyView.setVisibility(View.VISIBLE);
        emptyView.setMode(mode);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqReceivedOrderList();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == GlobelDefines.REQ_GPS_CODE) {
//                openGPSSettings();
//            }
//        }
    }
}
