package com.frxs.receipt.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.frxs.core.utils.ToastUtils;
import com.frxs.receipt.R;
import com.google.zxing.Result;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/06/07
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class CaptureActivity extends MyBaseActivity implements ZXingScannerView.ResultHandler {
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.content_frame)
    FrameLayout contentFrame;
    private ZXingScannerView scanView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        ButterKnife.bind(this);

        titleTv.setText(R.string.scan);
        scanView = new ZXingScannerView(this);
        contentFrame.addView(scanView);
    }

    @Override
    public void onResume() {
        super.onResume();
        scanView.setResultHandler(this); // Register ourselves as a handler for scan results.
        scanView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        scanView.stopCamera();           // Stop camera on pause
    }


    @Override
    public void handleResult(Result result) {
        String resultTxt = result.getText().toString().trim();
        if (TextUtils.isEmpty(resultTxt)) {
            ToastUtils.show(CaptureActivity.this, "没有搜索到任何结果");
            finish();
        } else {
            Intent intent = new Intent();
            intent.putExtra("Result", resultTxt);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }


    @OnClick(R.id.action_back_tv)
    public void onClick() {
        finish();
    }
}
