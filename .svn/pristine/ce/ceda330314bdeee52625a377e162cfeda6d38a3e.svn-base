package com.frxs.receipt.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.frxs.core.utils.ToastUtils;
import com.frxs.receipt.utils.ZXingUtils;
import com.frxs.receipt.R;
import com.frxs.receipt.rest.model.AjaxParams;
import com.frxs.receipt.rest.model.ApiResponse;
import com.frxs.receipt.rest.service.SimpleCallback;
import com.google.gson.JsonObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Created by Chentie on 2017/10/30.
 */

public class LookQrCodeActivity extends MyBaseActivity {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.iv_qr_code)
    ImageView qrCodeIv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitity_qr_code);
        ButterKnife.bind(this);
        titleTv.setText("查看二维码");

        initData();
    }

    private void initData() {
        showProgressDialog();
        Bundle bundle = getIntent().getExtras();
        String buyID = "";
        if (bundle!= null) {
            buyID = bundle.getString("BuyID");
        }
        AjaxParams params = new AjaxParams();
        params.put("BuyID", buyID);
        params.put("WID", getWID());
        getService().GetBuyOrderQRCodeContent(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<JsonObject>>() {
            @Override
            public void onResponse(ApiResponse<JsonObject> result, int code, String msg) {
                dismissProgressDialog();
                if (result.isSuccessful()) {
                    JsonObject data = result.getData();
                    if (data != null) {
                        String qrCodeContent = data.get("QRCodeContent").getAsString();
                        if (!TextUtils.isEmpty(qrCodeContent)) {
                            Bitmap bitmap = ZXingUtils.createQRImage(qrCodeContent, qrCodeIv.getWidth(), qrCodeIv.getHeight());
                            qrCodeIv.setImageBitmap(bitmap);
                        }
                    }
                } else {
                    ToastUtils.show(LookQrCodeActivity.this, result.getInfo());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<JsonObject>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                ToastUtils.show(LookQrCodeActivity.this, t.getMessage());
            }
        });
    }

    @OnClick({R.id.tv_confirm, R.id.action_back_tv})
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_confirm:
                gotoActivity(BottomTabActivity.class, true);
                break;

            case R.id.action_back_tv:
                gotoActivity(BottomTabActivity.class, true);
                break;

            default:
                break;
        }
    }
}
