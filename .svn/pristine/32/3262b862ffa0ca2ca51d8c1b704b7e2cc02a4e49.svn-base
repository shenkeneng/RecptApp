package com.frxs.receipt;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;

import com.frxs.core.utils.SerializableUtil;
import com.frxs.core.utils.SharedPreferencesHelper;
import com.frxs.core.utils.SystemUtils;
import com.frxs.core.utils.ToastUtils;
import com.frxs.core.widget.MaterialDialog;
import com.frxs.receipt.comms.Config;
import com.frxs.receipt.comms.GlobelDefines;
import com.frxs.receipt.greendao.utils.DbCore;
import com.frxs.receipt.model.AppVersionInfo;
import com.frxs.receipt.model.UserInfo;
import com.frxs.receipt.rest.RestClient;
import com.frxs.receipt.rest.model.AjaxParams;
import com.frxs.receipt.rest.model.ApiResponse;
import com.frxs.receipt.rest.service.SimpleCallback;
import com.frxs.receipt.rest.service.apkUpdate.DownloadService;
import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeReader;

import java.io.IOException;

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
public class MyApplication extends Application {

    private static MyApplication mInstance;
    private static SparseArray<RestClient> restClientSparseArray = new SparseArray<RestClient>();
    private UserInfo mUserInfo;// 用户信息
    private boolean needCheckUpgrade = true; // 是否需要检测更新
    private BarcodeReader barcodeReader;

    public static MyApplication getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException("Not yet initialized");
        }

        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (mInstance != null) {
            throw new IllegalStateException("Not a singleton");
        }

        mInstance = this;

        DbCore.init(this);

        initData();

        initRestClient();

        AidcManager.create(this, new AidcManager.CreatedCallback() {
            @Override
            public void onCreated(AidcManager aidcManager) {
                barcodeReader = aidcManager.createBarcodeReader();
            }
        });
    }

    public static RestClient getRestClient(int clientType) {
        return restClientSparseArray.get(clientType);
    }

    private void initRestClient() {
        int env = getEnvironment();
        restClientSparseArray.put(Config.TYPE_BASE, new RestClient(Config.getBaseUrl(Config.TYPE_BASE, env)));
        restClientSparseArray.put(Config.TYPE_UPDATE, new RestClient(Config.getBaseUrl(Config.TYPE_UPDATE, env)));
    }

    public void resetRestClient() {
        restClientSparseArray.clear();
        initRestClient();
    }

    private void initData() {
        // Get the user Info
        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, Config.PREFS_NAME);
        String userStr = helper.getString(Config.KEY_USER, "");
        if (!TextUtils.isEmpty(userStr)) {
            Object object = null;
            try {
                object = SerializableUtil.str2Obj(userStr);
                if (null != object) {
                    mUserInfo = (UserInfo) object;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setEnvironment(int environmentId) {
        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, GlobelDefines.PREFS_NAME);
        helper.putValue(GlobelDefines.KEY_ENVIRONMENT, environmentId);
    }

    public int getEnvironment() {
        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, GlobelDefines.PREFS_NAME);
        return helper.getInt(GlobelDefines.KEY_ENVIRONMENT, Config.networkEnv);
    }

    public void setUserInfo(UserInfo userInfo) {
        this.mUserInfo = userInfo;

        String userStr = "";
        try {
            userStr = SerializableUtil.obj2Str(userInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, Config.PREFS_NAME);
        helper.putValue(Config.KEY_USER, userStr);
    }

    public UserInfo getUserInfo() {
        if (null == mUserInfo) {
            initData();
        }

        return mUserInfo;
    }

    public String getUserAccount() {
        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, GlobelDefines.PREFS_NAME);
        return helper.getString(GlobelDefines.KEY_USER_ACCOUNT, "");
    }

    public void setUserAccount(String userAccount) {
        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, GlobelDefines.PREFS_NAME);
        helper.putValue(GlobelDefines.KEY_USER_ACCOUNT, userAccount);
    }

    public void logout() {
        // 清空用户信息
        setUserInfo(null);
    }

    public void exitApp(int code) {
        System.exit(code);
    }

    public boolean isNeedCheckUpgrade() {
        return needCheckUpgrade;
    }

    public BarcodeReader getBarcodeReader() {
        return barcodeReader;
    }

    /**
     * 更新版本的网路请求
     *
     * @param activity
     */
    public void prepare4Update(final Activity activity, final boolean isShow) {
        //开始检测了升级之后，设置标志位为不再检测升级
        if (needCheckUpgrade) {
            needCheckUpgrade = false;
        } else {
            return;
        }

        AjaxParams params = new AjaxParams();
        params.put("SysType", "0"); // 0:android;1:ios
        params.put("AppType", "4"); // 软件类型(0:兴盛店订货平台, 1:拣货APP. 2:兴盛店配送APP,3:装箱APP, 4:采购APP, 5:网络店订货平台,6：网络店配送APP)
        RestClient updateClient = getRestClient(Config.TYPE_UPDATE);
        updateClient.getApiService().AppVersionUpdateGet(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<AppVersionInfo>>() {
            @Override
            public void onResponse(ApiResponse<AppVersionInfo> result, int code, String msg) {
                if (result.getFlag().equals("0")) {
                    AppVersionInfo respData = result.getData();
                    if (null != respData) {
                        int versionCode = Integer.valueOf(SystemUtils.getVersionCode(getApplicationContext()));

                        String curVersion = respData.getCurVersion();
                        int curCode = respData.getCurCode();

                        if (versionCode < curCode) {
                            int updateFlag = respData.getUpdateFlag();
                            String updateRemark = respData.getUpdateRemark();
                            String downloadUrl = respData.getDownUrl();
                            switch (updateFlag) {
                                case 0: // 0:不需要
                                    break;
                                case 1: // 1:建议升级
                                    showUpdateDialog(activity, false, downloadUrl, curVersion, updateRemark);
                                    break;
                                case 2: // 2：强制升级
                                    showUpdateDialog(activity, true, downloadUrl, curVersion, updateRemark);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                } else {
                    ToastUtils.show(activity, result.getInfo());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AppVersionInfo>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    /**
     * 弹出更新的dialog
     *
     * @param activity
     * @param isForceUpdate 是否强制更新 true是，false否
     * @param downloadUrl   下载链接
     * @param curVersion    最新版本
     * @param updateRemark  更新内容
     * @description
     */
    private void showUpdateDialog(final Activity activity, final boolean isForceUpdate, final String downloadUrl, String curVersion, String updateRemark) {
        final MaterialDialog updateDialog = new MaterialDialog(activity);
        updateDialog.setTitle(R.string.update_title);
        updateDialog.setMessage(String.format(activity.getResources().getString(R.string.updade_content), curVersion,
                updateRemark));
        updateDialog.setCanceledOnTouchOutside(false);// 对话框外点击无效

        // 立即更新
        updateDialog.setPositiveButton("立即更新", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDialog.dismiss();
                DownloadService downLoadService = new DownloadService(activity, downloadUrl, isForceUpdate);
                downLoadService.execute();
                if (!isForceUpdate) {
                    ToastUtils.showShortToast(activity, "程序在后台下载，请稍等...");
                }
            }
        });

        // 稍后更新（强制更新）:点击稍后更新也要更新
        updateDialog.setNegativeButton("稍后更新", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDialog.dismiss();
                if (isForceUpdate) {
                    activity.finish();
                    System.exit(0);
                }
            }
        });
        updateDialog.show();
    }
}
