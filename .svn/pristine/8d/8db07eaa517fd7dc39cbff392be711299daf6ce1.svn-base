package com.frxs.receipt.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.frxs.core.utils.InputUtils;
import com.frxs.core.utils.ToastUtils;
import com.frxs.receipt.MyApplication;
import com.frxs.receipt.R;
import com.frxs.receipt.model.UserInfo;
import com.frxs.receipt.rest.model.AjaxParams;
import com.frxs.receipt.rest.model.ApiResponse;
import com.frxs.receipt.rest.service.SimpleCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/03/29
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class UpdatePswActivity extends MyBaseActivity {


    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.old_password_et)
    EditText oldPasswordEt;
    @BindView(R.id.old_password_layout)
    TextInputLayout oldPasswordLayout;
    @BindView(R.id.new_pw_et)
    EditText newPwEt;
    @BindView(R.id.new_pw_layout)
    TextInputLayout newPwLayout;
    @BindView(R.id.repeat_new_pw_et)
    EditText repeatNewPwEt;
    @BindView(R.id.repeat_new_pw_layout)
    TextInputLayout repeatNewPwLayout;
    @BindView(R.id.confirm_btn)
    Button confirmBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_psw);
        ButterKnife.bind(this);

        iniData();
    }

    protected void iniData() {
        titleTv.setText(R.string.title_update_pwd);
    }

    private void reqUpdatePws(String oldPassword, String newPassword) {
        showProgressDialog();
        AjaxParams params = new AjaxParams();
        UserInfo userInfo = MyApplication.getInstance().getUserInfo();
        params.put("UserId", getUserID());
        params.put("OldUserPwd", oldPassword);
        params.put("NewUserPwd", newPassword);
        params.put("UserAccount", userInfo.getUserAccount());
        params.put("UserType", "5");// 5 供货易
        getService().UpdatePwd(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse>() {
            @Override
            public void onResponse(ApiResponse result, int code, String msg) {
                dismissProgressDialog();
                if (result.isSuccessful()) {
                    ToastUtils.show(UpdatePswActivity.this, "修改密码成功");
                    finish();
                } else {
                    ToastUtils.show(UpdatePswActivity.this, result.getInfo());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
            }
        });
    }

    private void actionSubmitNewPsw() {
        String oldPassword = oldPasswordEt.getText().toString();
        String newPassword = newPwEt.getText().toString();
        String repeatNewPassword = repeatNewPwEt.getText().toString();
        if (TextUtils.isEmpty(oldPassword)) {
            oldPasswordLayout.setError(getString(R.string.tips_null_password));
            oldPasswordEt.requestFocus();
        } else if (TextUtils.isEmpty(newPassword)) {
            newPwLayout.setError(getString(R.string.tips_null_password));
            newPwEt.requestFocus();
        } else {
            if (newPassword.equals(repeatNewPassword)) {
                if (InputUtils.isNumericOrLetter(newPassword)) {
                    oldPasswordLayout.setErrorEnabled(false);
                    newPwLayout.setErrorEnabled(false);
                    reqUpdatePws(oldPassword, newPassword);
                } else {
                    newPwLayout.setError(getString(R.string.tips_input_limit));
                    newPwEt.requestFocus();
                }
            } else {
                ToastUtils.show(this, getString(R.string.tips_new_password_error));
                newPwEt.requestFocus();
            }
        }
    }

    @OnClick({R.id.action_back_tv, R.id.confirm_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_back_tv:
                finish();
                break;
            case R.id.confirm_btn:
                actionSubmitNewPsw();
                break;
        }
    }
}
