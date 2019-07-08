package com.frxs.receipt.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.frxs.core.utils.MathUtils;
import com.frxs.core.utils.SortListUtil;
import com.frxs.core.utils.ToastUtils;
import com.frxs.core.widget.MaterialDialog;
import com.frxs.receipt.R;
import com.frxs.receipt.activity.MyBaseActivity;
import com.frxs.receipt.greendao.DBHelper;
import com.frxs.receipt.greendao.entity.ProductEntity;
import com.frxs.receipt.greendao.entity.ReceivedListEntity;
import com.frxs.receipt.listener.ParentUIListener;
import com.frxs.receipt.model.PostCompletedOrder;
import com.frxs.receipt.rest.model.ApiResponse;
import com.frxs.receipt.rest.service.RequestListener;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/06/07
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class ReceivingProductFragment extends MyBaseFragment implements ParentUIListener {
    @BindView(R.id.total_row_tv)
    TextView totalRowTv;
    @BindView(R.id.total_product_tv)
    TextView totalProductTv;
    private ProductListFragment productListFragment;
    private List<ProductEntity> productEntityList = new ArrayList<>();
    private List<PostCompletedOrder.DetailsBean> postProductList = new ArrayList<>();  //等待提交的商品列表

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_receiving_product;
    }

    @Override
    protected void initViews(View view) {
        totalRowTv.setText(String.format(getString(R.string.total_product_row), 0));
        totalProductTv.setText(String.format(getString(R.string.total_product_num), "0"));
        productListFragment = (ProductListFragment) getChildFragmentManager().findFragmentById(R.id.product_list_fragment);
        if (null != productListFragment) {
            productListFragment.setListener(this);
        }
    }

    @Override
    protected void initData() {

    }

    public void setData(List<ProductEntity> productList) {
        if (null != productList) {
            productEntityList = productList;
        }
    }

    public void refreshData(List<ProductEntity> productList) {
        productEntityList = productList;
        productListFragment.refreshData(productEntityList);

        updateUI();
    }

    private void updateUI() {
        int totalRow = productEntityList.size();
        double totalProductNum = 0;
        for (ProductEntity item : productEntityList) {
            double productQty = item.getReceivedQty() >= 0 ? item.getReceivedQty() : item.getBuyQty();
            totalProductNum = MathUtils.add(totalProductNum, productQty);
        }
        totalRowTv.setText(String.format(getString(R.string.total_product_row), totalRow));
        totalProductTv.setText(String.format(getString(R.string.total_product_num), MathUtils.doubleTrans(totalProductNum)));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    private void reqReceiveOrderCompleted(List<PostCompletedOrder.DetailsBean> productList) {
        ((MyBaseActivity)mActivity).reqReceiveOrderCompleted(productList, new RequestListener() {
            @Override
            public void handleRequestResponse(ApiResponse result) {
                if (result.isSuccessful()) {
                    ToastUtils.show(mActivity, "提交成功");
                    DBHelper.getProductEntityService().deleteAll();
                    ReceivingFragment parentFragment = (ReceivingFragment) getParentFragment();
                    if (null != parentFragment) {
                        parentFragment.refreshData();
                    }
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
        final MaterialDialog errorDialog = new MaterialDialog(mActivity);
        errorDialog.setMessage(errorMsg);
        errorDialog.setPositiveButton(R.string.confirm, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorDialog.dismiss();
            }
        });

        errorDialog.show();
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
        bean.setEXP(startDate);
        bean.setPD(startDate);
        bean.setLotNO(startDate);
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

    private void showConfirmDialog(String tipsStr) {
        final Dialog confirmDlg = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        View contentView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_confirm, null);
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
        final Dialog confirmDlg = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        View contentView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_cancel, null);
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

    @OnClick(R.id.action_done_btn)
    public void onClick() {
        if (packagePostProductList()) {
            showConfirmDialog(getString(R.string.finish_receive_confirm2));
        } else {
            showCancelDialog(getString(R.string.finish_receive_cancel));
        }
    }

    @Override
    public void onParentResult(Intent data) {
        updateUI();
    }
}
