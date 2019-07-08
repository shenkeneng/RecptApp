package com.frxs.receipt.fragment;

import android.view.View;

import com.frxs.core.utils.ToastUtils;
import com.frxs.receipt.MyApplication;
import com.frxs.receipt.listener.ScanListener;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.ScannerUnavailableException;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/09/28
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public abstract class BaseScanFragment extends MyBaseFragment implements BarcodeReader.BarcodeListener, ScanListener {

    protected BarcodeReader barcodeReader;

    @Override
    protected void initViews(View view) {
        barcodeReader = MyApplication.getInstance().getBarcodeReader();
        barcodeReader.addBarcodeListener(this);
        try {
            barcodeReader.claim();
        } catch (ScannerUnavailableException e) {
            e.printStackTrace();
            ToastUtils.show(mActivity, "Scanner unavailable");
        }
    }

    @Override
    public void onBarcodeEvent(final BarcodeReadEvent barcodeReadEvent) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BaseScanFragment.this.onSuccessEvent(barcodeReadEvent);
            }
        });
    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent barcodeFailureEvent) {

    }

    private BarcodeReader getBarcodeReader() {
        if (null == barcodeReader) {
            barcodeReader = MyApplication.getInstance().getBarcodeReader();
        }

        return barcodeReader;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            barcodeReader.release();
            barcodeReader.removeBarcodeListener(this);
        } else {
            barcodeReader.addBarcodeListener(this);
            try {
                barcodeReader.claim();
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
                ToastUtils.show(mActivity, "Scanner unavailable");
            }
        }
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        if (null != barcodeReader) {
//            barcodeReader.addBarcodeListener(this);
//            try {
//                barcodeReader.claim();
//            } catch (ScannerUnavailableException e) {
//                e.printStackTrace();
//                ToastUtils.show(mActivity, "Scanner unavailable");
//            }
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (null != barcodeReader) {
//            barcodeReader.release();
//            barcodeReader.removeBarcodeListener(this);
//        }
//    }

}
