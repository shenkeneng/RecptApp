package com.frxs.receipt.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.frxs.receipt.R;
import com.frxs.receipt.comms.GlobelDefines;
import com.frxs.receipt.fragment.ProductListFragment;
import com.frxs.receipt.greendao.DBHelper;
import com.frxs.receipt.greendao.entity.ProductEntity;
import com.frxs.receipt.greendao.gen.ProductEntityDao;
import com.honeywell.aidc.BarcodeReadEvent;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/06/10
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class SearchResultRcvProductActivity extends BaseScanActivity {
    public static final int TYPE_GLOBAL_SEARCH = 1; //搜索所有正在收货的商品
    public static final int TYPE_ORDER_SEARCH = 2; //搜索单个订单的商品

    @BindView(R.id.search_tv)
    EditText searchTv;
    @BindView(R.id.action_scan_tv)
    TextView actionScanTv;
    private ProductListFragment productListFragment;
    private List<ProductEntity> productEntityList = new ArrayList<>();
    private List<ProductEntity> searchResultProductList = new ArrayList<>();
    private int searchType = TYPE_GLOBAL_SEARCH;
    private boolean scanForSearch = false; //是否是扫码搜索
    private String preBuyId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_product);
        ButterKnife.bind(this);

        productListFragment = (ProductListFragment) getSupportFragmentManager().findFragmentById(R.id.product_list_fragment);
        initData();
    }

    private void initData() {
        if (needHideQrcodeScanView()) {
            actionScanTv.setVisibility(View.GONE);
        }

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            productEntityList = (ArrayList<ProductEntity>) bundle.getSerializable("product_list");
            if (productEntityList != null) {
                DBHelper.getProductEntityService().saveOrUpdate(productEntityList);
                for (ProductEntity productEntity : productEntityList) {
                    DBHelper.getReceivedListEntityService().saveOrUpdate(productEntity.getReceivedListEntities());
                    productEntity.resetReceivedListEntities();
                }
            } else {
                preBuyId = bundle.getString("PreBuyID");
                productEntityList = DBHelper.getProductEntityService().queryBuilder().where(ProductEntityDao.Properties.PreBuyID.eq(preBuyId)).list();
            }
            searchType = bundle.getInt("search_type", TYPE_GLOBAL_SEARCH);
            productListFragment.refreshData(productEntityList);
            scanForSearch = bundle.getBoolean("scan_search", false);
            String searchResult = bundle.getString("Result", "");
            if (!TextUtils.isEmpty(searchResult)) {
                searchTv.setText(searchResult);
                searchTv.setSelection(searchResult.length());
                doSearch(searchResult);
            }
        }

        searchTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                doSearch(s.toString());
            }
        });
    }

    private void doSearch(String keyword) {
        if (!TextUtils.isEmpty(keyword)) {
            searchResultProductList.clear();
            if (null != productEntityList) {
                for (ProductEntity item : productEntityList) {
                    if (item.getProductName().contains(keyword)
                            || item.getBarCode().contains(keyword)
                            || item.getBigUnitBarCode().contains(keyword)
                            || item.getSKU().contains(keyword)) {
                        searchResultProductList.add(item);
                    }
                }
            }

            productListFragment.refreshData(searchResultProductList);

            if (scanForSearch && searchResultProductList.size() == 1) {
                productListFragment.setScanResult(searchResultProductList.get(0));
            }
        } else {
            searchResultProductList.clear();
            productListFragment.refreshData(productEntityList);
        }

        scanForSearch = false;
    }

    @OnClick({R.id.action_back_tv, R.id.action_scan_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_back_tv:
                finish();
                break;
            case R.id.action_scan_tv:
                if (hasCameraPermissions()) {
                    gotoActivityForResult(CaptureActivity.class, false, null, GlobelDefines.REQ_CODE_SCAN);
                }
                //hasCameraPermissions(null, true);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GlobelDefines.REQ_CODE_SCAN && null != data) {
                String result = data.getStringExtra("Result");
                if (!TextUtils.isEmpty(result)) {
                    searchTv.setText(result);
                    scanForSearch = true;
                    doSearch(result);
                }
            }
        }
    }

    @Override
    public void onSuccessEvent(BarcodeReadEvent event) {
        String barcodeData = event.getBarcodeData();
        if (!TextUtils.isEmpty(barcodeData)) {
            scanForSearch = true;
            searchTv.setText(barcodeData);
            searchTv.setSelection(barcodeData.length());
        }
    }

    @Override
    public void onSuccessEventStr(String event) {
        if (!TextUtils.isEmpty(event)) {
            scanForSearch = true;
            searchTv.setText(event);
            searchTv.setSelection(event.length());
        }
    }
}
