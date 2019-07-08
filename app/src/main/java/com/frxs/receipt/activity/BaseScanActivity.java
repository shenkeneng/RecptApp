package com.frxs.receipt.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.frxs.core.utils.ToastUtils;
import com.frxs.receipt.MyApplication;
import com.frxs.receipt.comms.GlobelDefines;
import com.frxs.receipt.listener.ScanListener;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.ScannerUnavailableException;
import com.obm.mylibrary.ScanEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/09/28
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public abstract class BaseScanActivity extends MyBaseActivity implements BarcodeReader.BarcodeListener, ScanListener {

    protected BarcodeReader barcodeReader;
    private ScanEvent scanEvent;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String code = intent.getStringExtra(GlobelDefines.DATA);
            if (code != null && !code.isEmpty()) {
                onSuccessEventStr(code);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String brand = Build.BRAND;
        if (!TextUtils.isEmpty(brand) && brand.equals("Honeywell")) {
            initBarcodeReader();
        }
    }

    private void initBarcodeReader() {
        barcodeReader = MyApplication.getInstance().getBarcodeReader();
        if (needHideQrcodeScanView()) {
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(BarcodeReader.PROPERTY_EAN_13_CHECK_DIGIT_TRANSMIT_ENABLED, true);
            barcodeReader.setProperties(properties);

        }
    }


    @Override
    public void onBarcodeEvent(final BarcodeReadEvent barcodeReadEvent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BaseScanActivity.this.onSuccessEvent(barcodeReadEvent);
            }
        });
    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent barcodeFailureEvent) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        String brand = Build.BRAND;
        if (!TextUtils.isEmpty(brand) && brand.equals("Honeywell")) {
            if (null != barcodeReader) {
                barcodeReader.addBarcodeListener(this);
                try {
                    barcodeReader.claim();
                } catch (ScannerUnavailableException e) {
                    e.printStackTrace();
                    ToastUtils.show(this, "Scanner unavailable");
                }
            }
        } else if (!TextUtils.isEmpty(brand) && brand.equals("A8")) {
            scanEvent = new ScanEvent(this, new ScanEvent.OnScanListener() {
                @Override
                public void onScan(String barcode) {
                    BaseScanActivity.this.onSuccessEventStr(barcode);
                }
            });
        } else if (!TextUtils.isEmpty(brand) && brand.equals("SUNMI")){
            registerReceiver(mBroadcastReceiver, new IntentFilter(GlobelDefines.ACTION_DATA_CODE_RECEIVED));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        String brand = Build.BRAND;
        if (!TextUtils.isEmpty(brand) && brand.equals("Honeywell")) {
            if (null != barcodeReader) {
                barcodeReader.release();
                barcodeReader.removeBarcodeListener(this);
            }
        } else if (!TextUtils.isEmpty(brand) && brand.equals("A8")) {
            scanEvent.scan_stop();
            scanEvent = null;
        }else if (!TextUtils.isEmpty(brand) && brand.equals("SUNMI")){
            unregisterReceiver(mBroadcastReceiver);
        }
    }

    public boolean needHideQrcodeScanView() {
        //brand=Honeywell model=Glory50
        String brand = Build.BRAND;
        // String model = Build.MODEL;
        if (!TextUtils.isEmpty(brand) && brand.equals("Honeywell")) {
            return true;
        }else if (!TextUtils.isEmpty(brand) && brand.equals("A8")) {
            return true;
        }else if (!TextUtils.isEmpty(brand) && brand.equals("SUNMI")){
            return true;
        }else {
            return false;
        }
    }
}
