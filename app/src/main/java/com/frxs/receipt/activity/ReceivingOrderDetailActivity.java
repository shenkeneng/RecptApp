package com.frxs.receipt.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.frxs.core.utils.MathUtils;
import com.frxs.core.utils.SortListUtil;
import com.frxs.core.utils.ToastUtils;
import com.frxs.core.widget.EmptyView;
import com.frxs.core.widget.MaterialDialog;
import com.frxs.receipt.R;
import com.frxs.receipt.comms.GlobelDefines;
import com.frxs.receipt.fragment.ProductListFragment;
import com.frxs.receipt.greendao.DBHelper;
import com.frxs.receipt.greendao.entity.ProductEntity;
import com.frxs.receipt.greendao.entity.ReceivedListEntity;
import com.frxs.receipt.greendao.gen.ProductEntityDao;
import com.frxs.receipt.listener.ParentUIListener;
import com.frxs.receipt.model.PostCompletedOrder;
import com.frxs.receipt.model.ProductInfo;
import com.frxs.receipt.rest.model.ApiResponse;
import com.frxs.receipt.rest.service.RequestListener;
import com.google.gson.JsonObject;
import com.honeywell.aidc.BarcodeReadEvent;
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
 *     time   : 2017/06/08
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class ReceivingOrderDetailActivity extends BaseScanActivity implements ParentUIListener {

    @BindView(R.id.total_row_tv)
    TextView totalRowTv;
    @BindView(R.id.total_product_tv)
    TextView totalProductTv;
    @BindView(R.id.action_scan_tv)
    TextView actionScanTv;
    private List<ProductEntity> productEntityList = new ArrayList<>();
    private String preBuyId;
    private ProductListFragment productListFragment;
    List<PostCompletedOrder.DetailsBean> postProductList = new ArrayList<>();  //等待提交的商品列表

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiving_detail);
        ButterKnife.bind(this);

        productListFragment = (ProductListFragment) getSupportFragmentManager().findFragmentById(R.id.product_list_fragment);
        if (null != productListFragment) {
            productListFragment.setListener(this);
        }
        initData();
    }

    private void initData() {
        totalRowTv.setText(String.format(getString(R.string.total_product_row), 0));
        totalProductTv.setText(String.format(getString(R.string.total_product_num), "0"));
        if (needHideQrcodeScanView()) {
            actionScanTv.setVisibility(View.GONE);
        }

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            preBuyId = bundle.getString("PreBuyID");
            productEntityList = (ArrayList<ProductEntity>) bundle.getSerializable("product_list");
            if (!TextUtils.isEmpty(preBuyId)) {
                //如果没有传值过来则首先查询数据库数据
                if (productEntityList == null || productEntityList.size() == 0) {
                    productEntityList = DBHelper.getProductEntityService().queryBuilder().where(ProductEntityDao.Properties.PreBuyID.eq(preBuyId)).list();
                }
                if (productEntityList != null && productEntityList.size() > 0) {
                    updateUI();
                } else {
                    reqOrderDetails(preBuyId, 8, new RequestListener() {
                        @Override
                        public void handleRequestResponse(ApiResponse result) {
                            if (result.isSuccessful()) {
                                ProductInfo productInfo = (ProductInfo) result.getData();
                                if (null != productInfo && productInfo.getItems().size() > 0) {
                                    productEntityList = productInfo.getItems();
                                    syncProductList();
                                    updateUI();
                                } else {
                                    productListFragment.initEmptyView(EmptyView.MODE_NODATA);
                                    ToastUtils.show(ReceivingOrderDetailActivity.this, "返回数据为空");
                                }
                            } else {
                                productListFragment.initEmptyView(EmptyView.MODE_ERROR);
                                ToastUtils.show(ReceivingOrderDetailActivity.this, "请求结果错误：" + result.getInfo());
                            }
                        }

                        @Override
                        public void handleExceptionResponse(String errMsg) {

                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncProductList();
        updateUI();
    }

    private void syncProductList() {
        List<ProductEntity> localProductList = DBHelper.getProductEntityService().queryBuilder().where(ProductEntityDao.Properties.PreBuyID.eq(preBuyId)).list();
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
                DBHelper.getProductEntityService().saveOrUpdate(item);
            }
        } else {
            DBHelper.getProductEntityService().saveOrUpdate(productEntityList);
        }
    }

    public void updateUI() {
        productListFragment.refreshData(productEntityList);

        int totalRow = productEntityList.size();
        double totalProductNum = 0;
        for (ProductEntity item : productEntityList) {
            double productQty = TextUtils.isEmpty(item.getReceivedUnit()) ? item.getBuyQty() : item.getReceivedQty();
            totalProductNum = MathUtils.add(totalProductNum, productQty);
        }
        totalRowTv.setText(Html.fromHtml(String.format(getString(R.string.total_product_row_red), totalRow)));
        totalProductTv.setText(Html.fromHtml(String.format(getString(R.string.total_product_num_red), MathUtils.doubleTrans(totalProductNum))));
    }

    private boolean packagePostProductList() {
        boolean bFinishRcv = true; //是否全部完成收货
        postProductList.clear();
        for (ProductEntity entity: productEntityList) {
            if (!entity.getIsReceived()) {
                bFinishRcv = false;
            }
            if (entity.getIsUserSL() == 1 && entity.getSLType() != 0 && entity.getReceivedListEntities() != null && entity.getReceivedListEntities().size() > 0) {
                List<ReceivedListEntity> receivedEntity = entity.getReceivedListEntities();
                SortListUtil.sort(receivedEntity, "getDueDate", SortListUtil.ORDER_BY_ASC);
                double recNumber = 0.0;
                for (int i = 0; i < receivedEntity.size(); i++) {
                    recNumber = (recNumber == 0.0) ? receivedEntity.get(i).getReceivedQty() : recNumber;
                    if (i + 1 < receivedEntity.size() && receivedEntity.get(i).getDueDate().equals(receivedEntity.get(i + 1).getDueDate())) {
                        recNumber = MathUtils.add(recNumber, receivedEntity.get(i + 1).getReceivedQty());
                    } else {
                        addPostList(entity, receivedEntity.get(i).getProductionDate(), receivedEntity.get(i).getDueDate(),
                                receivedEntity.get(i).getBatchNumber(), recNumber);
                        recNumber = 0.0;
                    }
                }
            } else {
                addPostList(entity, "", "", "", entity.getReceivedQty());
            }
        }

        return bFinishRcv;
    }

    private void addPostList(ProductEntity entity, String startDate, String dueDate, String batchNumber, double recNumber) {
        PostCompletedOrder.DetailsBean bean = new PostCompletedOrder.DetailsBean();
        bean.setPreBuyID(entity.getPreBuyID());
        bean.setProductId(entity.getProductId());
        bean.setPrepareUnit(entity.getBuyUnit());
        bean.setReceiveUnit(TextUtils.isEmpty(entity.getReceivedUnit()) ? entity.getBuyUnit() : entity.getReceivedUnit());
        bean.setReceiveQty(recNumber);
        List<ReceivedListEntity> receivedListEntities = entity.getReceivedListEntities();
        bean.setEXP(dueDate);
        bean.setPD(startDate);
        bean.setLotNO(batchNumber);
        bean.setIsUserSL(entity.getIsUserSL());
        bean.setSLType(entity.getSLType());
        bean.setGPDay(entity.getGPDay());
        bean.setGPMonth(entity.getGPMonth());
        bean.setGPYear(entity.getGPYear());
        if (!TextUtils.isEmpty(entity.getReceivedRemark())) {
            bean.setReceiveRemark(entity.getReceivedRemark());
        }

        postProductList.add(bean);
    }

    private void reqReceiveOrderCompleted(List<PostCompletedOrder.DetailsBean> productList) {
        reqReceiveOrderCompleted(productList, new RequestListener() {
            @Override
            public void handleRequestResponse(ApiResponse result) {
                if (result.isSuccessful()) {
                    List<ProductEntity> localProductList = DBHelper.getProductEntityService().queryBuilder().where(ProductEntityDao.Properties.PreBuyID.eq(preBuyId)).list();
                    for (ProductEntity item: localProductList) {
                        DBHelper.getReceivedListEntityService().delete(item.getReceivedListEntities()); //删除关联的已收货列表
                        item.resetReceivedListEntities();
                    }
                    DBHelper.getProductEntityService().delete(localProductList);
                    setResult(Activity.RESULT_OK);
                    JsonObject jsonData = (JsonObject)result.getData();
                    if (null != jsonData) {
                        String buyID = jsonData.get("BuyID").getAsString();
                        if (!TextUtils.isEmpty(buyID)) {
                            Bundle bundle = new Bundle();
                            bundle.putString("BuyID", buyID);
                            gotoActivity(LookQrCodeActivity.class, false, bundle);
                        } else {
                            showSubmitFailedResultDialog("BuyID为空，无法查看二维码");
                        }
                    } else {
                        showSubmitFailedResultDialog("BuyID为空，无法查看二维码");
                    }

                    finish();
                } else {
                    showSubmitFailedResultDialog(result.getInfo());
                }
            }

            @Override
            public void handleExceptionResponse(String errMsg) {

            }
        });
    }

    private void showSubmitFailedResultDialog(String errorMsg) {
        final MaterialDialog errorDialog = new MaterialDialog(this);
        errorDialog.setMessage(errorMsg);
        errorDialog.setPositiveButton(R.string.confirm, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorDialog.dismiss();
            }
        });

        errorDialog.show();
    }

    private void showConfirmDialog(String tipsStr) {
        final Dialog confirmDlg = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_confirm, null);
        TextView contentTv = (TextView) contentView.findViewById(R.id.content_tv);
        contentTv.setText(tipsStr);
        contentView.findViewById(R.id.confirm_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDlg.dismiss();
                reqReceiveOrderCompleted(postProductList);
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

    private void showCancelDialog(String tipsStr) {
        final Dialog confirmDlg = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_cancel, null);
        TextView contentTv = (TextView) contentView.findViewById(R.id.content_tv);
        contentTv.setText(tipsStr);
        contentView.findViewById(R.id.confirm_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDlg.dismiss();
            }
        });
        confirmDlg.setContentView(contentView);
        confirmDlg.show();
    }

    @OnClick({R.id.action_back_tv, R.id.action_done_btn, R.id.search_tv, R.id.action_scan_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_back_tv:
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
            case R.id.action_done_btn:
                if (packagePostProductList()) {
                    showConfirmDialog(getString(R.string.finish_receive_confirm2));
                } else {
                    showCancelDialog(getString(R.string.finish_receive_cancel));
                }
                break;
            case R.id.search_tv: {
                Bundle bundle = new Bundle();
                bundle.putInt("search_type", SearchResultRcvProductActivity.TYPE_ORDER_SEARCH);
                bundle.putString("PreBuyID", preBuyId);
                gotoActivityForResult(SearchResultRcvProductActivity.class, false, bundle, GlobelDefines.REQ_CODE_SEARCH_PRODUCT);
                break;
            }
            case R.id.action_scan_tv: {
                Bundle bundle = new Bundle();
                bundle.putBoolean("to_result_page", false);
                bundle.putString("prebuyid", preBuyId);
                bundle.putSerializable("product_list", (Serializable) productEntityList);
                //hasCameraPermissions(bundle, true);
                if (hasCameraPermissions()) {
                    gotoActivityForResult(CaptureActivity.class, false, bundle, GlobelDefines.REQ_CODE_SCAN);
                }
                break;
            }
        }
    }

    @Override
    public void onParentResult(Intent data) {
        updateUI();
    }

    @Override
    public void onSuccessEvent(BarcodeReadEvent event) {
        String barcodeData = event.getBarcodeData();
        dealScanResult(barcodeData);
    }

    @Override
    public void onSuccessEventStr(String event) {
        dealScanResult(event);
    }

    private void dealScanResult(String barcodeData) {
        if (!TextUtils.isEmpty(barcodeData)) {
            if (null != productEntityList) {
                List<ProductEntity> scanResultList = new ArrayList<>();
                for (ProductEntity item : productEntityList) {
                    if (item.getBarCode().contains(barcodeData) || item.getBigUnitBarCode().contains(barcodeData)) {
                        scanResultList.add(item);
                    }
                }

                if (scanResultList.size() > 1) {
                    gotoSearchResultRcvProductActivity(barcodeData, (Serializable) scanResultList);
                } else if (scanResultList.size() == 1) {
                    if (!scanResultList.get(0).getIsReceived()) {
                        productListFragment.setScanResult(scanResultList.get(0));
                    } else {
                        gotoSearchResultRcvProductActivity(barcodeData, (Serializable) scanResultList);
                    }
                } else {
                    ToastUtils.show(ReceivingOrderDetailActivity.this, "没有找到" + barcodeData);
                }
            }
        }
    }

    private void gotoSearchResultRcvProductActivity(String barcodeData, Serializable scanResultList) {
        Bundle bundle = new Bundle();
        bundle.putInt("search_type", SearchResultRcvProductActivity.TYPE_ORDER_SEARCH);
        bundle.putString("Result", barcodeData);
        bundle.putString("PreBuyID", preBuyId);
        bundle.putSerializable("product_list", scanResultList);
        gotoActivityForResult(SearchResultRcvProductActivity.class, false, bundle, GlobelDefines.REQ_CODE_SEARCH_PRODUCT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    String barcodeData = data.getStringExtra("Result");
                    if (!TextUtils.isEmpty(barcodeData)) {
                        dealScanResult(barcodeData);
                    }
                }
            }
    }
}
