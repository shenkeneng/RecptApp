package com.frxs.receipt.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.frxs.core.utils.ImageLoader;
import com.frxs.core.utils.MathUtils;
import com.frxs.core.utils.ToastUtils;
import com.frxs.receipt.R;
import com.frxs.receipt.model.OrderSigns;
import com.frxs.receipt.rest.model.AjaxParams;
import com.frxs.receipt.rest.model.ApiResponse;
import com.frxs.receipt.rest.service.SimpleCallback;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Created by Chentie on 2017/8/3.
 */

public class LookSignatureActivity extends MyBaseActivity {

    @BindView(R.id.action_back_tv)
    TextView actionBackTv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.iv_sign)
    ImageView ivSign;
    @BindView(R.id.iv_photo)
    ImageView ivPhoto;
    @BindView(R.id.tv_sign_time)
    TextView signTimeTv;
    @BindView(R.id.tv_sign_id)
    TextView signIdTv;
    @BindView(R.id.tv_gps)
    TextView gpsTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_signature);
        ButterKnife.bind(this);
        titleTv.setText(R.string.look_sign);

        initDate();
    }

    private void initDate() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String signId = bundle.getString("sign_id");
            if (!TextUtils.isEmpty(signId)) {
                lookOrderSign(signId);
            } else {
                ToastUtils.show(this, "该订单暂无签名信息");
            }
        }
    }

    private void lookOrderSign(String signId) {
        showProgressDialog();
        AjaxParams params = new AjaxParams();
        params.put("SignID", signId);

        getService().GetOrderSign(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<OrderSigns>>() {
            @Override
            public void onResponse(ApiResponse<OrderSigns> result, int code, String msg) {
                dismissProgressDialog();
                if (result.getFlag().equals("0")) {
                    if (result.getData() != null) {
                        OrderSigns orderSigns = result.getData();
                        if (orderSigns.getSignUrl() != null) {
                            String signUrl = orderSigns.getSignUrl();
                            if (!orderSigns.getSignUrl().startsWith("http")) {
                                signUrl = "http://" + signUrl;
                            }
                            ImageLoader.loadImage(LookSignatureActivity.this, signUrl, ivSign, R.mipmap.showcase_product_default);
                        }

                        if (orderSigns.getPhotoUrl1() != null) {
                            String photoUrl = orderSigns.getPhotoUrl1();
                            if (!orderSigns.getPhotoUrl1().startsWith("http")) {
                                photoUrl = "http://" + photoUrl;
                            }
                            ImageLoader.loadImage(LookSignatureActivity.this, photoUrl, ivPhoto, R.mipmap.showcase_product_default);
                        }

                        if (!TextUtils.isEmpty(orderSigns.getLng()) && !TextUtils.isEmpty(orderSigns.getLat())) {
                            gpsTv.setText(String.format(getString(R.string.gps), MathUtils.round(Double.valueOf(orderSigns.getLng()), 6), MathUtils.round(Double.valueOf(orderSigns.getLat()), 6)));
                        } else {
                            gpsTv.setText("未查询到坐标...");
                        }

                        if (!TextUtils.isEmpty(orderSigns.getID())) {
                            signIdTv.setText(String.format(getString(R.string.sign_device_id), orderSigns.getMacID()));
                        } else {
                            signIdTv.setText(String.format(getString(R.string.sign_device_id), ""));
                        }

                        if (!TextUtils.isEmpty(orderSigns.getModifyTime())) {
                            signTimeTv.setText(String.format(getString(R.string.sign_time), orderSigns.getModifyTime()));
                        } else {
                            signTimeTv.setText(String.format(getString(R.string.sign_time), ""));
                        }
                    }
                } else {
                    ToastUtils.show(LookSignatureActivity.this, result.getInfo());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OrderSigns>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                ToastUtils.show(LookSignatureActivity.this, t.getMessage());
            }
        });
    }

    @OnClick({R.id.action_back_tv})
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.action_back_tv:
                finish();
                break;
        }
    }
}
