package com.frxs.receipt.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.frxs.core.utils.DateUtil;
import com.frxs.core.utils.DisplayUtil;
import com.frxs.core.utils.ImageLoader;
import com.frxs.core.utils.ImeUtils;
import com.frxs.core.utils.MathUtils;
import com.frxs.core.utils.SortListUtil;
import com.frxs.core.utils.ToastUtils;
import com.frxs.core.widget.EmptyView;
import com.frxs.core.widget.MaterialDialog;
import com.frxs.receipt.MyApplication;
import com.frxs.receipt.R;
import com.frxs.receipt.greendao.DBHelper;
import com.frxs.receipt.greendao.entity.ProductEntity;
import com.frxs.receipt.greendao.entity.ReceivedListEntity;
import com.frxs.receipt.greendao.gen.ProductEntityDao;
import com.frxs.receipt.listener.ParentUIListener;
import com.frxs.receipt.model.UserInfo;
import com.frxs.receipt.rest.model.AjaxParams;
import com.frxs.receipt.rest.model.ApiResponse;
import com.frxs.receipt.rest.service.SimpleCallback;
import com.frxs.receipt.widget.CountEditText;
import com.kyleduo.switchbutton.SwitchButton;
import com.pacific.adapter.Adapter;
import com.pacific.adapter.AdapterHelper;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/06/10
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class ProductListFragment extends MyBaseFragment {

    @BindView(R.id.product_list_view)
    ListView productListView;
    @BindView(R.id.empty_view)
    EmptyView emptyView;
    private Adapter adapter;
    private List<ProductEntity> productEntityList = new ArrayList<>();
    private ParentUIListener listener;
    private int status = 8;
    private int type = -1;
    private AlertDialog modifyDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_product_list;
    }

    @Override
    protected void initViews(View view) {

    }

    @Override
    protected void initData() {
        adapter = new Adapter<ProductEntity>(mActivity, R.layout.item_product) {
            @Override
            protected void convert(AdapterHelper helper, final ProductEntity item) {
                if (helper.getPosition() == 0) {
                    String supplierInfo = item.getVendorCode() + "-" + item.getVendorName();
                    helper.setVisible(R.id.supplier_tv, View.VISIBLE);
                    helper.setVisible(R.id.order_id_tv, View.VISIBLE);
                    helper.setText(R.id.supplier_tv, supplierInfo);
                    helper.setText(R.id.order_id_tv,item.getPreBuyID());
                    helper.setText(R.id.order_remark_tv, getString(R.string.remark) + "：" + item.getOrderRemark());
                    helper.setVisible(R.id.order_remark_tv,TextUtils.isEmpty(item.getOrderRemark()) ? View.GONE : View.VISIBLE);
                } else {
                    helper.setVisible(R.id.supplier_tv, View.GONE);
                    helper.setVisible(R.id.order_id_tv, View.GONE);

                    helper.setVisible(R.id.order_remark_tv, View.GONE);
                }
                helper.setText(R.id.product_name_tv, item.getProductName());
                helper.setText(R.id.product_code_tv, String.format(getString(R.string.product_code), item.getSKU()));
                helper.setText(R.id.product_barcode_tv, String.format(getString(R.string.product_barcode), item.getBarCode().split(",")[0]));
                helper.setText(R.id.product_location_tv, String.format(getString(R.string.product_location), item.getShelfCode()));
                helper.setText(R.id.product_package_tv, String.format(getString(R.string.product_package_num), item.getBuyPackingQty()));
                helper.setVisible(R.id.tv_good_remark, TextUtils.isEmpty(item.getRemark()) ? View.GONE : View.VISIBLE);
                helper.setText(R.id.tv_good_remark, getResources().getString(R.string.remark) + "：" + item.getRemark());
                double rcQty = !TextUtils.isEmpty(item.getReceivedUnit()) ? item.getReceivedQty() : item.getBuyQty();
                String receivedUnit = TextUtils.isEmpty(item.getReceivedUnit()) ? item.getBuyUnit() : item.getReceivedUnit();
                helper.setText(R.id.product_num_tv, String.format("%1$s%2$s", MathUtils.doubleTrans(rcQty), receivedUnit));
                helper.setOnClickListener(R.id.product_name_tv, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProductDetailDialog(item);
                    }
                });
                if (item.getReceivedListEntities() == null || item.getReceivedListEntities().size() <= 0) {
                    if (TextUtils.isEmpty(item.getReceivedRemark())) {
                        item.setReceivedRemark(item.getSpec());
                    }
                }
                helper.setVisible(R.id.tv_received_remark, TextUtils.isEmpty(item.getReceivedRemark()) ? View.GONE : View.VISIBLE);
                helper.setText(R.id.tv_received_remark, getResources().getString(R.string.actual_received_remark) + item.getReceivedRemark());

                if (6 != status) {
                    helper.setOnClickListener(R.id.product_num_tv, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!item.getIsReceived()) {
                                showChangeCountDialog2(item);
                            } else {
                                ToastUtils.show(mActivity, getString(R.string.modify_receiving_status));
                            }
                        }
                    });
                    final SwitchButton switchButton = helper.getView(R.id.received_switch_btn);
                    switchButton.setCheckedImmediatelyNoEvent(!item.getIsReceived());
                    switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (item.getSLType() != 0 && item.getIsUserSL() == 1) {//此商品开启保质期 并且是保质期商品
                                if (item.getReceivedListEntities() != null && item.getReceivedListEntities().size() > 0) {
                                    ReceivedListEntity recEntity = item.getReceivedListEntities().get(0);
                                    if (item.getSLType() == 1 && !TextUtils.isEmpty(recEntity.getProductionDate()) && !TextUtils.isEmpty(recEntity.getDueDate())) {
                                        productReceived(isChecked, recEntity.getProductionDate(), recEntity.getDueDate(), recEntity.getBatchNumber());
                                    } else if (item.getSLType() == 2 && !TextUtils.isEmpty(recEntity.getDueDate())) {
                                        productReceived(isChecked, recEntity.getProductionDate(), recEntity.getDueDate(), recEntity.getBatchNumber());
                                    } else {
                                        switchButton.setCheckedImmediatelyNoEvent(true);
                                        showChangeCountDialog2(item);
                                        ToastUtils.show(mActivity, "该商品生产日期或者到期日期不能为空");
                                    }
                                } else {
                                    switchButton.setCheckedImmediatelyNoEvent(true);
                                    showChangeCountDialog2(item);
                                    ToastUtils.show(mActivity, "该商品生产日期或者到期日期不能为空");
                                }
                            } else {
                                productReceived(isChecked, "", "", "");
                            }
                        }

                        private void productReceived(boolean isChecked, String startDate, String endDate, String batchNumber) {
                            item.setIsReceived(!isChecked);

                            if (TextUtils.isEmpty(item.getReceivedUnit())) {
                                item.setReceivedQty(item.getBuyQty());
                                item.setReceivedUnit(item.getBuyUnit());
                                recordReceivedList(item, item.getBuyQty(), startDate, endDate, batchNumber);
                            }

                            DBHelper.getProductEntityService().update(item);
                            //排序
                            SortListUtil.sort(productEntityList, "getIsReceived", SortListUtil.ORDER_BY_ASC);
                            adapter.replaceAll(productEntityList);
                        }
                    });

                    boolean isModified = !TextUtils.isEmpty(item.getReceivedUnit()) && ((!item.getReceivedUnit().equals(item.getBuyUnit()) || item.getReceivedQty() != item.getBuyQty()));
                    helper.setBackgroundColor(R.id.product_num_tv, isModified ? ContextCompat.getColor(mActivity, R.color.yellow_light) : ContextCompat.getColor(mActivity, R.color.transparent));
                } else {
                    helper.setVisible(R.id.received_switch_btn, View.GONE);
                }
            }
        };

        productListView.setAdapter(adapter);
        if (null != productEntityList && productEntityList.size() > 0) {
            emptyView.setVisibility(View.GONE);
            SortListUtil.sort(productEntityList, "getIsReceived", SortListUtil.ORDER_BY_ASC);
            adapter.replaceAll(productEntityList);
        }
    }

    public void setListener(ParentUIListener listener) {
        this.listener = listener;
    }

    public void refreshData(List<ProductEntity> productEntityList, int status) {
        this.status = status;
        refreshData(productEntityList);
    }

    private void recordReceivedList(ProductEntity productEntity, double count, String startDate, String endDate, String batchNumber) {
        ReceivedListEntity receivedListEntity = new ReceivedListEntity();
        receivedListEntity.setPid(productEntity.getId());
        receivedListEntity.setReceivedQty(count);
        if (!TextUtils.isEmpty(startDate)) {
            receivedListEntity.setProductionDate(startDate);
        }
        if (!TextUtils.isEmpty(endDate)) {
            receivedListEntity.setDueDate(endDate);
        }
        if (!TextUtils.isEmpty(batchNumber)) {
            receivedListEntity.setBatchNumber(batchNumber);
        }
        DBHelper.getReceivedListEntityService().save(receivedListEntity);
        productEntity.resetReceivedListEntities();
    }

    public void refreshData(List<ProductEntity> productEntityList) {
        this.productEntityList = productEntityList;
        if (productEntityList != null && adapter != null && productEntityList.size() > 0) {
            SortListUtil.sort(productEntityList, "getIsReceived", SortListUtil.ORDER_BY_ASC);
            adapter.replaceAll(productEntityList);
            emptyView.setVisibility(View.GONE);
        } else {
            initEmptyView(EmptyView.MODE_NODATA);
        }
    }

    public void setScanResult(ProductEntity item) {
        if (!item.getIsReceived()) {
            showChangeCountDialog2(item);
        } else {
            ToastUtils.show(mActivity, getString(R.string.modify_receiving_status));
        }
    }

    private double getMaxCount(ProductEntity item) {
        double multiple = MyApplication.getInstance().getUserInfo().getMultiple();
        double buyQty = item.getBuyQty();
        if (!TextUtils.isEmpty(item.getTempUnit()) && !item.getTempUnit().equals(item.getBuyUnit())) { //表示切换了小单位, 需要乘以包装数
            buyQty = MathUtils.mul(buyQty, item.getBuyPackingQty());
        }
        double goodCount = MathUtils.roundUp(MathUtils.mul(buyQty, multiple), 2);
        return goodCount; //最大值是购买的数量 * 登陆时获取的收货倍数值
    }

    private double getBuyCount(ProductEntity item){
        double buyQty = item.getBuyQty();
        if (!TextUtils.isEmpty(item.getTempUnit()) && !item.getTempUnit().equals(item.getBuyUnit())) { //表示切换了小单位, 需要乘以包装数
            buyQty = MathUtils.mul(buyQty, item.getBuyPackingQty());
        }
        return MathUtils.roundUp(buyQty, 2);
    }

    private void showProductDetailDialog(ProductEntity item) {
        final Dialog productDlg = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        View contentView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_product_detail, null);
        ((TextView)contentView.findViewById(R.id.product_id_tv)).setText("单号：" + item.getPreBuyID());
        ((TextView)contentView.findViewById(R.id.product_name_tv)).setText("商品名称：" + item.getProductName());
        String placeholder = (!TextUtils.isEmpty(item.getBigUnitBarCode()) && !TextUtils.isEmpty(item.getBarCode())) ? "," : "";
        ((TextView)contentView.findViewById(R.id.product_barcode_tv)).setText("条码：\n" + item.getBarCode() + placeholder + item.getBigUnitBarCode());
        ImageLoader.loadImage(mActivity, item.getImgUrl(), (ImageView) contentView.findViewById(R.id.product_pic_iv),R.mipmap.showcase_product_default,R.mipmap.showcase_product_default);
        contentView.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productDlg.dismiss();
            }
        });
        productDlg.setContentView(contentView);
        productDlg.show();
    }

    private void showChangeCountDialog2(final ProductEntity item) {
        if (modifyDialog != null && modifyDialog.isShowing()) {
            ToastUtils.show(mActivity, "完成或取消当前商品收货");
            return;
        }
        modifyDialog = new AlertDialog.Builder(mActivity).create();
        View contentView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_modify_count, null);
        final CountEditText quantityEt = (CountEditText) contentView.findViewById(R.id.edit_count_et);
        quantityEt.setMaxCount(getMaxCount(item));
        String receivedUnit = TextUtils.isEmpty(item.getReceivedUnit()) ? item.getBuyUnit() : item.getReceivedUnit();
        final TextView receiveUnitTv = (TextView) contentView.findViewById(R.id.receive_unit_tv);
        receiveUnitTv.setText(receivedUnit);
        double receivableQty;
        if (item.getReceivedQty() >= 0) {
            double buyQty = item.getBuyQty();
            if (!receivedUnit.equals(item.getBuyUnit())) {  //表示切换了小单位, 需要乘以包装数
                buyQty = MathUtils.mul(buyQty, item.getBuyPackingQty());
            }
            receivableQty = buyQty > item.getReceivedQty() ? buyQty - item.getReceivedQty() : 0;
        } else {
            receivableQty = item.getBuyQty();
        }
        quantityEt.setCount(receivableQty);
        TextView supplierTv = (TextView) contentView.findViewById(R.id.supplier_tv);
        supplierTv.setText(item.getProductName());
        TextView buyQtyTv = (TextView) contentView.findViewById(R.id.order_num_tv);
        buyQtyTv.setText(String.format(getString(R.string.order_num_1), MathUtils.doubleTrans(item.getBuyQty()), item.getBuyUnit()));
        TextView packageTv = (TextView) contentView.findViewById(R.id.product_package_tv);
        packageTv.setText(String.format(getString(R.string.package_num), MathUtils.doubleTrans(item.getBuyPackingQty())));
        final TextView receivedQtyTv= (TextView) contentView.findViewById(R.id.received_num_tv);
        receivedQtyTv.setText(String.format(getString(R.string.received_num), MathUtils.doubleTrans(item.getReceivedQty()), receivedUnit));
        final EditText recpRemarkEt = (EditText) contentView.findViewById(R.id.et_received_remark);
        recpRemarkEt.setText(item.getReceivedRemark());
        // 初始化保质期
        final TextView productionDate = (TextView) contentView.findViewById(R.id.production_date);
        final TextView batchNumber = (TextView) contentView.findViewById(R.id.et_batch_number);
        final TextView dueDate = (TextView) contentView.findViewById(R.id.due_date);
        List<ReceivedListEntity> receivedListEntities = item.getReceivedListEntities();
        initProductionDate(item, contentView, productionDate, batchNumber, dueDate, receivedListEntities);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean setReceived;
                if (v.getId() == R.id.confirm_recv_btn) {
                    setReceived = true;
                } else {
                    setReceived = false;
                }

                double editCount = MathUtils.add(quantityEt.getCount(), item.getReceivedQty());
                double maxCount = getMaxCount(item);
                if (editCount > maxCount) {
                    ToastUtils.show(mActivity, getResources().getString(R.string.max_upper_limit_prompt) + maxCount);
                    return;
                }

                if (quantityEt.getCount() - (int)quantityEt.getCount() > 0) {
                    final MaterialDialog materialDialog = new MaterialDialog(mActivity);
                    materialDialog.setMessage("确认输入小数？");
                    materialDialog.setPositiveButton("确认", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    materialDialog.dismiss();
                                    doConfirm(setReceived);
                                }
                            }
                    );
                    materialDialog.setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            materialDialog.dismiss();
                        }
                    });
                    materialDialog.show();
                    return;
                } else {
                    doConfirm(setReceived);
                }
            }

            private void doConfirm(boolean setReceived) {
                if (item.getSLType() != 0 && item.getIsUserSL() == 1) {// 开启保质期并且该商品是保质期商品
                    if (item.getSLType() == 1) {
                        if (TextUtils.isEmpty(productionDate.getText())) {
                            ToastUtils.show(mActivity, "该商品生产日期不能为空");
                            return;
                        }

                        if (TextUtils.isEmpty(dueDate.getText())) {
                            ToastUtils.show(mActivity, "该商品到期日期不能为空");
                            return;
                        }
                    }
                    if (item.getSLType() == 2 && TextUtils.isEmpty(dueDate.getText())) {
                        ToastUtils.show(mActivity, "该商品到期日期不能为空");
                        return;
                    }

                    int isMaster = MyApplication.getInstance().getUserInfo().getIsMaster();
                    if (quantityEt.getCount() > 0) {
                        if (MathUtils.add(quantityEt.getCount(), item.getReceivedQty()) > getBuyCount(item) && isMaster != 1) {
                            showMasterDialog(item, quantityEt.getCount(), recpRemarkEt.getText().toString().trim(), setReceived, modifyDialog,
                                    productionDate.getText().toString(), dueDate.getText().toString(), batchNumber.getText().toString());
                        } else {
                            modifyProductInfo(setReceived, true, productionDate.getText().toString(), dueDate.getText().toString(), batchNumber.getText().toString());
                        }
                    } else {
                        modifyProductInfo(setReceived, false, productionDate.getText().toString(), dueDate.getText().toString(), batchNumber.getText().toString());
                    }
                } else {
                    int isMaster = MyApplication.getInstance().getUserInfo().getIsMaster();
                    if (quantityEt.getCount() > 0) {
                        if (MathUtils.add(quantityEt.getCount(), item.getReceivedQty()) > getBuyCount(item) && isMaster != 1) {
                            showMasterDialog(item, quantityEt.getCount(), recpRemarkEt.getText().toString().trim(), setReceived, modifyDialog,
                                    "", "", "");
                        } else {
                            modifyProductInfo(setReceived, true, "", "", "");
                        }
                    } else {
                        modifyProductInfo(setReceived, false, "", "", "");
                    }
                }
            }

            private void modifyProductInfo(boolean setReceived, boolean setReceivedDeail,String startDate, String endDate, String batchNumber) {
                item.setReceivedQty(MathUtils.add(item.getReceivedQty(),quantityEt.getCount()));
                item.setReceivedUnit(item.getTempUnit());
                item.setIsReceived(setReceived);
                item.setReceivedRemark(recpRemarkEt.getText().toString().trim());
                if (setReceivedDeail) {
                    recordReceivedList(item, quantityEt.getCount(), startDate, endDate, batchNumber);
                }
                DBHelper.getProductEntityService().update(item);

                //排序
                SortListUtil.sort(productEntityList, "getIsReceived", SortListUtil.ORDER_BY_ASC);
                adapter.replaceAll(productEntityList);
                if (null != listener) {
                    listener.onParentResult(null);
                }
                modifyDialog.dismiss();
            }
        };

        contentView.findViewById(R.id.confirm_recv_btn).setOnClickListener(onClickListener);
        contentView.findViewById(R.id.confirm_btn).setOnClickListener(onClickListener);
        contentView.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setTempUnit(null);
                adapter.replaceAll(productEntityList);
                if (null != listener) {
                    listener.onParentResult(null);
                }
                modifyDialog.dismiss();
            }
        });
        contentView.findViewById(R.id.switch_unit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!item.getBuyUnit().equals(item.getUnit())) {
                    if (item.getReceivedQty() == 0) {
                        showUnitPop(item, v, quantityEt, receiveUnitTv);
                    } else {
                        ToastUtils.show(mActivity, getString(R.string.forbid_switch_unit_prompt));
                    }
                } else {
                    ToastUtils.show(mActivity, getString(R.string.no_need_switch_unit_prompt));
                }
            }
        });
        contentView.findViewById(R.id.received_detail_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ReceivedListEntity> receivedList = item.getReceivedListEntities();
                if (null != receivedList && receivedList.size() > 0) {
                    showReceivedDetailDialog(item, receivedQtyTv);
                } else {
                    ToastUtils.show(mActivity, getString(R.string.no_received_record));
                }
            }
        });
        modifyDialog.setView(contentView, 0, 0, 0, 0);
        modifyDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialog) {
                if (quantityEt.getmEdit() != null) {
                    EditText editText = quantityEt.getmEdit();
                    ImeUtils.popSoftKeyboard(mActivity, editText, true);
                }
            }
        });
        modifyDialog.show();
    }

    private void initProductionDate(final ProductEntity item, View contentView, final TextView startDate, TextView batchNumber, final TextView dueDate, List<ReceivedListEntity> receivedListEntities) {
        if (item.getSLType() != 0 && item.getIsUserSL() == 1) {//
            contentView.findViewById(R.id.ll_production_date).setVisibility(View.VISIBLE);
            contentView.findViewById(R.id.ll_due_date).setVisibility(View.VISIBLE);
            contentView.findViewById(R.id.ll_batch_number).setVisibility(View.VISIBLE);
            final TextView delTv = (TextView) contentView.findViewById(R.id.ic_start_del);
            if ((receivedListEntities != null && receivedListEntities.size() > 0)) {
                startDate.setText(receivedListEntities.get(receivedListEntities.size() - 1).getProductionDate());
                dueDate.setText(receivedListEntities.get(receivedListEntities.size() - 1).getDueDate());
                batchNumber.setText(receivedListEntities.get(receivedListEntities.size() - 1).getBatchNumber());
            } else {
                startDate.setText("");
                dueDate.setText("");
                batchNumber.setText("");
            }

            contentView.findViewById(R.id.ll_production_date).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    type = 0;
                    showYearMonthDayDialog(item, startDate, dueDate, delTv);
                }
            });
            contentView.findViewById(R.id.ll_due_date).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    type = 1;
                    showYearMonthDayDialog(item, startDate, dueDate, delTv);
                }
            });

            if (TextUtils.isEmpty(startDate.getText().toString().trim())) {
                delTv.setVisibility(View.GONE);
            } else {
                delTv.setVisibility(View.VISIBLE);
            }

            delTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startDate.setText("");
                    v.setVisibility(View.GONE);
                }
            });

        } else {
            contentView.findViewById(R.id.ll_production_date).setVisibility(View.GONE);
            contentView.findViewById(R.id.ll_due_date).setVisibility(View.GONE);
            contentView.findViewById(R.id.ll_batch_number).setVisibility(View.GONE);
        }
    }

    private void showYearMonthDayDialog(final ProductEntity item, final TextView startDate, final TextView dueDate, final TextView delTv) {
        int year = -1;
        int monthOfYear = -1;
        int dayOfMonth = -1;
        String date1s = "";
        if (type == 0) {
            date1s = startDate.getText().toString();
        } else if (type == 1) {
            date1s = dueDate.getText().toString();
        }
        if (!TextUtils.isEmpty(date1s)) {
            year = Integer.valueOf(date1s.split("-")[0]);
            monthOfYear = Integer.valueOf(date1s.split("-")[1]) - 1;
            dayOfMonth = Integer.valueOf(date1s.split("-")[2]);
        }
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Date dateStr = new Date(DateUtil.getTimeMillis(DateUtil.getTimeString(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth, "0", type)));
                        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd");
                        switch (type) {
                            case 0:
                                String date1s = dueDate.getText().toString();
                                if (item.getSLType() == 1) {
                                    setProductDate(dateStr, startDate, dueDate);
                                } else {
                                    compareDate(sdfTime.format(dateStr), date1s, startDate);
                                }
                                break;
                            case 1:
                                String date2s = startDate.getText().toString();
                                if (item.getSLType() == 1) {
                                    setProductDate(dateStr, dueDate, startDate);
                                } else {
                                    compareDate(date2s, sdfTime.format(dateStr), dueDate);
                                }
                                break;
                            default:
                                break;
                        }

                        if (TextUtils.isEmpty(startDate.getText().toString().trim())) {
                            delTv.setVisibility(View.GONE);
                        } else {
                            delTv.setVisibility(View.VISIBLE);
                        }
                    }

                    private void setProductDate(Date dateStr, TextView startDate, TextView dueDate) {
                        String timeString1 = "";
                        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd");
                        if (item.getGPYear() > 0) {
                            Date date = DateUtil.addDateYears(dateStr, (type == 0) ? item.getGPYear() : -item.getGPYear());
                            timeString1 = sdfTime.format(date);
                        } else if (item.getGPMonth() > 0) {
                            Date date = DateUtil.addDateMonths(dateStr, (type == 0) ? item.getGPMonth() : -item.getGPMonth());
                            timeString1 = sdfTime.format(date);
                        } else {
                            Date date = DateUtil.addDateDays(dateStr, (type == 0) ? item.getGPDay() : -item.getGPDay());
                            timeString1 = sdfTime.format(date);
                        }
                        dueDate.setText(timeString1);
                        startDate.setText(sdfTime.format(dateStr));
                    }

                    private void compareDate(String dateStr, String date1s, TextView startDate) {
                        if (!TextUtils.isEmpty(date1s)) {
                            long dateStart = DateUtil.getTimeMillis(dateStr);
                            long dateEnd = DateUtil.getTimeMillis(date1s);
                            if (dateStart > dateEnd) {
                                ToastUtils.show(mActivity, "生产日期不应大于过期日期，请重新选择!");
                            } else {
                                startDate.setText(type == 0 ? dateStr : date1s);
                            }
                        } else {
                            startDate.setText(dateStr);
                        }
                    }
                },
                (year != -1 ? year : now.get(Calendar.YEAR)),
                (monthOfYear != -1 ? monthOfYear : now.get(Calendar.MONTH)),
                (dayOfMonth != -1 ? dayOfMonth : now.get(Calendar.DAY_OF_MONTH))
        );
        dpd.setYearRange(2000, 2050);
        dpd.setAccentColor(getResources().getColor(R.color.frxs_red));
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.show(mActivity.getFragmentManager(), "Datepickerdialog");
    }


    private void showReceivedDetailDialog(final ProductEntity productEntity, final TextView receivedQtyTv) {
        final MaterialDialog materialDialog = new MaterialDialog(mActivity);
        final View contentView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_received_detail, null);
        contentView.findViewById(R.id.close_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        final ListView receivedLv = (ListView) contentView.findViewById(R.id.received_list_lv);
        final Adapter adapter = new Adapter<ReceivedListEntity>(mActivity, productEntity.getReceivedListEntities(), R.layout.item_received) {
            @Override
            protected void convert(AdapterHelper helper, final ReceivedListEntity item) {
                helper.setText(R.id.record_item_tv, String.valueOf(item.getReceivedQty()));
                final int position = helper.getPosition();
                helper.setOnClickListener(R.id.delete_action_tv, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        productEntity.setReceivedQty(MathUtils.sub(productEntity.getReceivedQty(), item.getReceivedQty()));
                        ReceivedListEntity receivedListEntity = productEntity.getReceivedListEntities().get(position);
                        if (null != receivedListEntity) {
                            DBHelper.getReceivedListEntityService().delete(receivedListEntity);
                            productEntity.resetReceivedListEntities();
                        }
                        DBHelper.getProductEntityService().update(productEntity);
                        receivedQtyTv.setText(String.format(getString(R.string.received_num), MathUtils.doubleTrans(productEntity.getReceivedQty()), productEntity.getReceivedUnit()));
                        materialDialog.dismiss();
                    }
                });
            }
        };
        receivedLv.setAdapter(adapter);
        materialDialog.setListViewHeightBasedOnChildren(receivedLv);
        materialDialog.setContentView(contentView);
        materialDialog.setCanceledOnTouchOutside(true);
        materialDialog.show();
    }

    private void showMasterDialog(final ProductEntity item, final double quantity, final String recpRemark, final boolean setReceived, final Dialog changCountdialog,
                                  final String startDate, final String endDate, final String batchNumber) {
        final AlertDialog verifyMasterDialog = new AlertDialog.Builder(mActivity).create();
        View contentView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_master, null);
        final EditText masterAccountEt = (EditText) contentView.findViewById(R.id.et_master_account);
        final EditText masterPasswordEt = (EditText) contentView.findViewById(R.id.et_master_psw);
        contentView.findViewById(R.id.confirm_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = masterAccountEt.getText().toString().trim();
                String password = masterPasswordEt.getText().toString().trim();
                if (TextUtils.isEmpty(account)){
                    ToastUtils.show(mActivity, "组长账号不能为空！");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    ToastUtils.show(mActivity, "组长密码不能为空！");
                    return;
                }

                reqIsMaster(account, password, item, quantity, recpRemark, setReceived, changCountdialog, verifyMasterDialog
                        ,startDate, endDate, batchNumber);
            }
        });
        contentView.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyMasterDialog.dismiss();
            }
        });

        verifyMasterDialog.setView(contentView);
        verifyMasterDialog.show();
    }

    private void reqIsMaster(String account, String password, final ProductEntity item, final double quantity,
                             final String recpRemark, final boolean setReceived, final Dialog changCountdialog, final Dialog verifyMasterDialog,
                             final String startDate, final String endDate, final String batchNumber) {
        mActivity.showProgressDialog();

        AjaxParams params = new AjaxParams();
        params.put("UserAccount", account);
        params.put("UserPwd", password);
        params.put("UserType", "5");// 5 供货易
        getService().UserLogin(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<UserInfo>>() {
            @Override
            public void onResponse(ApiResponse<UserInfo> result, int code, String msg) {
                if (result.getFlag().equals("0")) {
                    UserInfo userInfo = result.getData();
                    if (null != userInfo) {
                        if (userInfo.getIsMaster() == 1){
                            item.setReceivedQty(MathUtils.add(quantity,item.getReceivedQty()));
                            item.setReceivedUnit(item.getTempUnit());
                            item.setIsReceived(setReceived);
                            item.setReceivedRemark(recpRemark);

                            DBHelper.getProductEntityService().update(item);
                            recordReceivedList(item, quantity, startDate, endDate, batchNumber);
                            //排序
                            SortListUtil.sort(productEntityList, "getIsReceived", SortListUtil.ORDER_BY_ASC);
                            adapter.replaceAll(productEntityList);
                            if (null != listener) {
                                listener.onParentResult(null);
                            }
                            changCountdialog.dismiss();
                            verifyMasterDialog.dismiss();
                        } else {
                            ToastUtils.show(mActivity, "该用户不是组长，无法修改商品数量。");//网络请求失败，请重试
                        }
                    } else {
                        ToastUtils.show(mActivity, R.string.network_request_failed);//网络请求失败，请重试
                    }
                } else {
                    ToastUtils.show(mActivity, result.getInfo());
                }
                mActivity.dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<ApiResponse<UserInfo>> call, Throwable t) {
                mActivity.dismissProgressDialog();
                ToastUtils.show(mActivity, R.string.network_request_failed);//网络请求失败，请重试
            }
        });
    }

    private void showUnitPop(final ProductEntity item, View anchor, final CountEditText quantityEt, final TextView receiveUnitTv) {
        List<String> unitList = new ArrayList<String>();
        unitList.add(item.getBuyUnit());
        unitList.add(item.getUnit());
        View contentView = LayoutInflater.from(mActivity).inflate(R.layout.view_select_unit, null);
        final PopupWindow unitPop = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        unitPop.setBackgroundDrawable(new ColorDrawable(0x00000000));
        unitPop.setFocusable(true);
        DisplayUtil.setBackgroundAlpha(mActivity, 0.5f);
        ListView unitLv = (ListView) contentView.findViewById(R.id.lv_unit);
        unitLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String)parent.getAdapter().getItem(position);
                if (item.getTempUnit().equals(selectedItem)) { //表示选中了上次一样的单位直接退出
                    unitPop.dismiss();
                    return;
                }
                double count = quantityEt.getCount();
                double maxCount = quantityEt.getMaxCount();
                if (!selectedItem.equals(item.getUnit())) { //表示切换了大单位
                    count = MathUtils.div(count, item.getBuyPackingQty());
                    maxCount = MathUtils.div(maxCount, item.getBuyPackingQty());
                } else {
                    count = MathUtils.mul(item.getBuyPackingQty(), count);
                    maxCount = MathUtils.mul(item.getBuyPackingQty(), maxCount);
                }
                quantityEt.setMaxCount(maxCount);//切换单位后的最大数量
                quantityEt.setCount(count);
                item.setTempUnit(selectedItem);
                receiveUnitTv.setText(selectedItem);
                unitPop.dismiss();
            }
        });
        Adapter adapter = new Adapter<String>(mActivity, unitList, R.layout.item_select_unit) {
            @Override
            protected void convert(AdapterHelper helper, String listItem) {
                helper.setText(R.id.unit_name_tv, listItem);
                if (item.getTempUnit().equals(listItem)) {
                    helper.setTextColor(R.id.unit_name_tv, ContextCompat.getColor(context, R.color.themeColor));
                } else {
                    helper.setTextColor(R.id.unit_name_tv, ContextCompat.getColor(context, R.color.frxs_black));
                }
            }
        };
        unitLv.setAdapter(adapter);
        unitPop.showAsDropDown(anchor);
        unitPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                DisplayUtil.setBackgroundAlpha(mActivity, 1f);
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

    public void initEmptyView(int mode) {
        emptyView.setVisibility(View.VISIBLE);
        emptyView.setMode(mode);
    }
}
