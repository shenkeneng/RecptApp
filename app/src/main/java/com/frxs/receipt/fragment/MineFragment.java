package com.frxs.receipt.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.frxs.core.utils.SystemUtils;
import com.frxs.core.widget.MaterialDialog;
import com.frxs.receipt.MyApplication;
import com.frxs.receipt.R;
import com.frxs.receipt.activity.LoginActivity;
import com.frxs.receipt.activity.ReceivedOrderListActivity;
import com.frxs.receipt.activity.UpdatePswActivity;
import com.frxs.receipt.model.UserInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/06/06
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class MineFragment extends MyBaseFragment {

    public static final String TAG = MineFragment.class.getSimpleName();
    @BindView(R.id.action_back_tv)
    TextView actionBackTv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.user_info_tv)
    TextView userInfoTv;
    @BindView(R.id.user_name_tv)
    TextView userNameTv;
    @BindView(R.id.TEL_tv)
    TextView TELTv;
//    @BindView(R.id.receipt_details_tv)
//    TextView receiptDetailsTv;
    @BindView(R.id.update_password_tv)
    TextView updatePasswordTv;
    @BindView(R.id.version_id_tv)
    TextView versionIdTv;
    @BindView(R.id.logout_btn)
    TextView logoutBtn;
    @BindView(R.id.tv_version_number)
    TextView tvVersionNumber;

    public static MineFragment newInstance(String argument) {
        Bundle bundle = new Bundle();
        bundle.putString(TAG, argument);
        MineFragment contentFragment = new MineFragment();
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initViews(View view) {
        titleTv.setText(R.string.title_mine);
        actionBackTv.setVisibility(View.INVISIBLE);
        tvVersionNumber.setText(String.format(getString(R.string.version), SystemUtils.getAppVersion(mActivity)));
        UserInfo userInfo = MyApplication.getInstance().getUserInfo();
        if (null != userInfo) {
            userNameTv.setText(userInfo.getEmpName());
            TELTv.setText(userInfo.getUserMobile());
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

    @OnClick({R.id.update_psw_view, R.id.check_version_view, R.id.logout_btn})
    public void onClick(View view) {
        switch (view.getId()) {
           /* case R.id.receipt_details_view:
                mActivity.gotoActivity(ReceivedOrderListActivity.class);
                break;*/
            case R.id.update_psw_view:
                mActivity.gotoActivity(UpdatePswActivity.class);
                break;
            case R.id.check_version_view:
                MyApplication.getInstance().prepare4Update(mActivity, true);
                break;
            case R.id.logout_btn:
                loginOut();
                break;
        }
    }

    private void loginOut() {
        final MaterialDialog loginOutDialog = new MaterialDialog(mActivity);
        loginOutDialog.setMessage(getString(R.string.exit_query));
        loginOutDialog.setPositiveButton(R.string.confirm, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.getInstance().logout();
                mActivity.finish();

                Intent intent = new Intent(mActivity, LoginActivity.class);
                startActivity(intent);
                loginOutDialog.dismiss();
            }
        });

        loginOutDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginOutDialog.dismiss();
            }
        });
        loginOutDialog.show();
    }
}
