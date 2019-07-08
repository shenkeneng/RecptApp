package com.frxs.receipt.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.frxs.receipt.R;
import com.frxs.receipt.activity.BaseScanActivity;
import com.frxs.receipt.activity.CaptureActivity;
import com.frxs.receipt.activity.MyBaseActivity;
import com.frxs.receipt.activity.PendingOrderListActivity;
import com.frxs.receipt.comms.GlobelDefines;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/10/31
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class PendingRcvOrderFragment extends MyBaseFragment {
    private static final String TAG = PendingRcvOrderFragment.class.getSimpleName();
    @BindView(R.id.action_back_tv)
    TextView actionBackTv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.action_right_tv)
    TextView actionRightTv;
    @BindView(R.id.search_tv)
    EditText searchTv;
    @BindView(R.id.action_scan_tv)
    TextView actionScanTv;

    /**
     * 传入需要的参数，设置给arguments
     *
     * @param argument
     * @return
     */
    public static PendingRcvOrderFragment newInstance(String argument) {
        Bundle bundle = new Bundle();
        bundle.putString(TAG, argument);
        PendingRcvOrderFragment contentFragment = new PendingRcvOrderFragment();
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pending_rcv_order_list;
    }

    @Override
    protected void initViews(View view) {
        actionBackTv.setVisibility(View.GONE);
        titleTv.setText(R.string.pending_receive);

        if (mActivity instanceof BaseScanActivity) {
            if (((BaseScanActivity) mActivity).needHideQrcodeScanView()) {
                actionScanTv.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @OnClick({R.id.action_scan_tv, R.id.search_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_scan_tv:
                if (((MyBaseActivity) mActivity).hasCameraPermissions()) {
                    mActivity.gotoActivityForResult(CaptureActivity.class, false, null, GlobelDefines.REQ_CODE_SCAN);
                }
                break;
            case R.id.search_btn:
                String searchContent = searchTv.getText().toString().trim();
                Bundle bundle = new Bundle();
                bundle.putString("search_content", searchContent);
                mActivity.gotoActivity(PendingOrderListActivity.class, false, bundle);
                break;
        }
    }

    public void onSuccessEvent(String barcodeData) {
        if (!TextUtils.isEmpty(barcodeData)) {
            Bundle bundle = new Bundle();
            bundle.putString("search_content", barcodeData);
            mActivity.gotoActivity(PendingOrderListActivity.class, false, bundle);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GlobelDefines.REQ_CODE_SCAN && null != data) {
                String result = data.getStringExtra("Result");
                searchTv.setText(result);
            }
        }
    }
}
