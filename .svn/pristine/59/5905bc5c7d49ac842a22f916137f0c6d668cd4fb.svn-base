package com.frxs.receipt.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.frxs.core.utils.InputUtils;
import com.frxs.core.utils.ToastUtils;
import com.frxs.receipt.MyApplication;
import com.frxs.receipt.R;
import com.frxs.receipt.comms.Config;
import com.frxs.receipt.greendao.DBHelper;
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
public class LoginActivity extends MyBaseActivity {

    @BindView(R.id.user_name_et)
    EditText userNameEt;
    @BindView(R.id.user_name_layout)
    TextInputLayout userNameLayout;
    @BindView(R.id.password_et)
    EditText passwordEt;
    @BindView(R.id.password_layout)
    TextInputLayout passwordLayout;
    @BindView(R.id.login_btn)
    Button loginBtn;
    int keyDownNum = 0;
    private String[] environments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        iniData();
    }

    protected void iniData() {
        userNameLayout.setHint(getResources().getString(R.string.user_name));
        passwordLayout.setHint(getResources().getString(R.string.password));
        String account = MyApplication.getInstance().getUserAccount();
        if (!TextUtils.isEmpty(account)) {
            userNameEt.setText(account);
            userNameEt.setSelection(userNameEt.length());
        }

        initEnvironment();
    }

    private void initEnvironment() {
        environments = getResources().getStringArray(R.array.run_environments);
        for (int i = 0; i < environments.length; i++) {
            environments[i] = String.format(environments[i], Config.getBaseUrl(Config.TYPE_BASE,i));
        }
    }

    @OnClick({R.id.login_btn, R.id.select_environment})
    public void onClick(View view) {
//        gotoActivity(BottomTabActivity.class, true);
        if (view.getId() == R.id.login_btn) {
            String username = userNameLayout.getEditText().getText().toString();
            String password = passwordLayout.getEditText().getText().toString();
            if (TextUtils.isEmpty(username)) {
                userNameLayout.setError(getString(R.string.tips_null_account));
                userNameEt.requestFocus();
            } else if (TextUtils.isEmpty(password)) {
                passwordLayout.setError(getString(R.string.tips_null_password));
                passwordEt.requestFocus();
            } else {
                if (InputUtils.isNumericOrLetter(password)) {
                    userNameLayout.setErrorEnabled(false);
                    passwordLayout.setErrorEnabled(false);
                    requestLogin(username, password);
                } else {
                    passwordLayout.setError(getString(R.string.tips_input_limit));
                    passwordEt.requestFocus();
                }
            }
        } else if (view.getId() == R.id.select_environment/* && (Config.networkEnv != 0)*/) {
            keyDownNum++;
            if (keyDownNum == 9) {
                ToastUtils.showLongToast(LoginActivity.this, "再点击1次进入环境选择模式");
            }
            if (keyDownNum == 10) {
                ToastUtils.showLongToast(LoginActivity.this, "已进入环境选择模式");
                AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
                final int spEnv = MyApplication.getInstance().getEnvironment();
                String env = spEnv < environments.length ? environments[spEnv] : "";
                dialog.setTitle(getResources().getString(R.string.tips_environment, env));
                dialog.setCancelable(false);
                dialog.setItems(environments, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, final int which) {
                        if (spEnv == which) {
                            return;
                        }
                        if (which != 0) {
                            final AlertDialog verifyMasterDialog = new AlertDialog.Builder(LoginActivity.this).create();
                            View contentView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_evironments, null);
                            final EditText pswEt = (EditText) contentView.findViewById(R.id.password_et);
                            contentView.findViewById(R.id.confirm_btn).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (TextUtils.isEmpty(pswEt.getText().toString().trim())) {
                                        ToastUtils.show(LoginActivity.this, "密码不能为空！");
                                        return;
                                    }

                                    if (!pswEt.getText().toString().trim().equals(getString(R.string.str_psw))) {
                                        ToastUtils.show(LoginActivity.this, "密码错误！");
                                        return;
                                    }
                                    DBHelper.getProductEntityService().deleteAll();
                                    DBHelper.getReceivedListEntityService().deleteAll();
                                    MyApplication.getInstance().setEnvironment(which);//存储所选择环境
                                    MyApplication.getInstance().resetRestClient();
                                    verifyMasterDialog.dismiss();
                                }
                            });

                            contentView.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    verifyMasterDialog.dismiss();
                                }
                            });
                            verifyMasterDialog.setView(contentView);
                            verifyMasterDialog.show();

                        } else {
                            DBHelper.getProductEntityService().deleteAll();
                            DBHelper.getReceivedListEntityService().deleteAll();
                            MyApplication.getInstance().setEnvironment(which);//存储所选择环境
                            MyApplication.getInstance().resetRestClient();
                        }

                    }
                });
                dialog.setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialog.show();
                keyDownNum = 0;
            }
        }
    }

    private void requestLogin(final String strUserName, String strPassWord) {
        showProgressDialog();

        AjaxParams params = new AjaxParams();
        params.put("UserAccount", strUserName);
        params.put("UserPwd", strPassWord);
        params.put("UserType", "5");// 5 供货易
        getService().UserLogin(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<UserInfo>>() {
            @Override
            public void onResponse(ApiResponse<UserInfo> result, int code, String msg) {
                if (result.getFlag().equals("0")) {
                    ToastUtils.show(LoginActivity.this, "登录成功");
                    MyApplication application = MyApplication.getInstance();
                    application.setUserAccount(strUserName);

                    UserInfo userInfo = result.getData();
                    if (null != userInfo) {
                        application.setUserInfo(userInfo);

                        gotoActivity(BottomTabActivity.class, true);
                    }
                } else {
                    ToastUtils.show(LoginActivity.this, result.getInfo());
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<ApiResponse<UserInfo>> call, Throwable t) {
                dismissProgressDialog();
                ToastUtils.show(LoginActivity.this, R.string.network_request_failed);//网络请求失败，请重试
            }
        });
    }
}
