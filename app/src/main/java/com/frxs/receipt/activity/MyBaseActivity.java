package com.frxs.receipt.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.frxs.core.base.BaseActivity;
import com.frxs.core.utils.EasyPermissionsEx;
import com.frxs.receipt.MyApplication;
import com.frxs.receipt.comms.Config;
import com.frxs.receipt.model.PendingOrder;
import com.frxs.receipt.model.PostCompletedOrder;
import com.frxs.receipt.model.ProductInfo;
import com.frxs.receipt.model.UserInfo;
import com.frxs.receipt.rest.model.AjaxParams;
import com.frxs.receipt.rest.model.ApiResponse;
import com.frxs.receipt.rest.service.ApiService;
import com.frxs.receipt.rest.service.RequestListener;
import com.frxs.receipt.rest.service.SimpleCallback;
import com.google.gson.JsonObject;

import java.util.List;
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
public abstract class MyBaseActivity extends BaseActivity {
    protected ApiService mService;
    private static final int MY_PERMISSIONS_REQUEST_WES = 2;// 请求文件存储权限的标识码
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 3;// 请求相机权限的标识码
    private static final int MY_PERMISSIONS_REQUEST_GPS = 4;// GPS权限标识
    private static final int MY_PERMISSIONS_REQUEST_PHONE_STATE = 5;// 手机权限
    private Bundle bundleThis;
    private boolean isScan = false;// 是否扫码

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!(this instanceof SplashActivity)) {
            // 判断当前用户是否允许存储权限
            if (EasyPermissionsEx.hasPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE})) {
                // ,Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
                // 允许 - 执行更新方法
                if (MyApplication.getInstance().isNeedCheckUpgrade()) {
                    MyApplication.getInstance().prepare4Update(this, false);
                }
            } else {
                // 不允许 - 弹窗提示用户是否允许放开权限
                EasyPermissionsEx.executePermissionsRequest(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_WES);
                // ,Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            }
        }
    }

    public ApiService getService() {
        return MyApplication.getRestClient(Config.TYPE_BASE).getApiService();
    }

    public String getUserID() {
        UserInfo userInfo = MyApplication.getInstance().getUserInfo();
        if (null != userInfo) {
            return userInfo.getEmpID();
        } else {
            return "";
        }
    }

    public String getUserName() {
        UserInfo userInfo = MyApplication.getInstance().getUserInfo();
        if (null != userInfo) {
            return userInfo.getEmpName();
        } else {
            return "";
        }
    }

    public String getWID() {
        UserInfo userInfo = MyApplication.getInstance().getUserInfo();

        if (null != userInfo) {
            return String.valueOf(userInfo.getWareHouseWID());
        } else {
            return "";
        }
    }

    public String getSubWID() {
        UserInfo userInfo = MyApplication.getInstance().getUserInfo();

        if (null != userInfo) {
            return String.valueOf(userInfo.getSubWID());
        } else {
            return "";
        }
    }

    public void reqOrderDetails(String orderId, int status, final RequestListener listener) {
        showProgressDialog();
        AjaxParams params = new AjaxParams();
        params.put("WID", getWID());
        if (!TextUtils.isEmpty(orderId)) {
            params.put("PreBuyID", orderId);
        }
        if (status != 6) {
            params.put("ReceiveUserID", getUserID());
        }
        params.put("Status", status); //6：等待收货  8： 正在收货
        getService().GetReceiveOrderStartedDetails(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<ProductInfo>>() {
            @Override
            public void onResponse(ApiResponse<ProductInfo> result, int code, String msg) {
                if (null != listener) {
                    listener.handleRequestResponse(result);
                }

                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<ApiResponse<ProductInfo>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
            }
        });
    }

    public void reqStartReceiveOrder(final String orderId, final RequestListener listener) {
        showProgressDialog();
        AjaxParams params = new AjaxParams();
        params.put("WID", getWID());
        params.put("ReceiveUserID", getUserID());
        params.put("ReceiveUserName", getUserName());
        params.put("PreBuyID", orderId);
        getService().ReceiveOrderStart(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<Void>>() {
            @Override
            public void onResponse(ApiResponse<Void> result, int code, String msg) {
                if (null != listener) {
                    listener.handleRequestResponse(result);
                }

                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
            }
        });
    }
    public void reqOrderList(int status, final boolean needProgressDialog, final RequestListener listener) {
        reqOrderList(status, needProgressDialog, "", listener);
    }

    public void reqOrderList(int status, final boolean needProgressDialog, String preOrderId, final RequestListener listener) {
        if (needProgressDialog) {
            showProgressDialog();
        }
        AjaxParams params = new AjaxParams();
        params.put("WID", getWID());
        params.put("SubWID", getSubWID());
        if (6 != status){
            params.put("ReceiveUserID", getUserID());
        }
        params.put("PreBuyID", preOrderId);
        params.put("Status", status); //6：等待收货  8： 正在收货
        getService().GetReceiveOrders(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<PendingOrder>>() {
            @Override
            public void onResponse(ApiResponse<PendingOrder> result, int code, String msg) {
                if (null != listener) {
                    listener.handleRequestResponse(result);
                }

                if (needProgressDialog) {
                    dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PendingOrder>> call, Throwable t) {
                super.onFailure(call, t);
                if (needProgressDialog) {
                    dismissProgressDialog();
                }
            }
        });
    }

    public void reqReceiveOrderCompleted(List<PostCompletedOrder.DetailsBean> detailsBeen, final RequestListener listener) {
        showProgressDialog();
        PostCompletedOrder completedOrder = new PostCompletedOrder();
        completedOrder.setWID(getWID());
        completedOrder.setReceiveUserID(getUserID());
        completedOrder.setReceiveUserName(getUserName());
        completedOrder.setDetails(detailsBeen);
        getService().ReceiveOrderCompleted(completedOrder).enqueue(new SimpleCallback<ApiResponse<JsonObject>>() {
            @Override
            public void onResponse(ApiResponse<JsonObject> result, int code, String msg) {
                if (null != listener) {
                    listener.handleRequestResponse(result);
                }

                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<ApiResponse<JsonObject>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
            }
        });
    }

    public boolean hasCameraPermissions(){
        // 判断当前用户是否允许相机权限
        if (EasyPermissionsEx.hasPermissions(this, new String[]{Manifest.permission.CAMERA})) {
           return true;
        } else {
            // 不允许 - 弹窗提示用户是否允许放开权限
            EasyPermissionsEx.executePermissionsRequest(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            return false;
        }
    }

    public boolean hasGPSPermissions(){
        // 判断当前用户是否允许GPS权限
        if (EasyPermissionsEx.hasPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})) {
            return true;
        } else {
            // 不允许 - 弹窗提示用户是否允许放开权限
            EasyPermissionsEx.executePermissionsRequest(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_GPS);
            return false;
        }
    }

    public boolean hasPhoneStatePermissions(){
        // 判断当前用户是否允许相机权限
        if (EasyPermissionsEx.hasPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE})) {
            return true;
        } else {
            // 不允许 - 弹窗提示用户是否允许放开权限
            EasyPermissionsEx.executePermissionsRequest(this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_PHONE_STATE);
            return false;
        }
    }

    /**
     * 请求用户是否放开权限的回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WES: {// 版本更新存储权限
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // 已获取权限 继续运行应用
                        if (MyApplication.getInstance().isNeedCheckUpgrade()) {
                            MyApplication.getInstance().prepare4Update(this, false);
                        }
                    } else {
                        // 不允许放开权限后，提示用户可在去设置中跳转应用设置页面放开权限。
                        if (!EasyPermissionsEx.somePermissionPermanentlyDenied(this, permissions)) {
                            EasyPermissionsEx.goSettings2PermissionsDialog(this, "需要文件存储权限来下载更新的内容,但是该权限被禁止,你可以到设置中更改");
                        }
                    }
                break;
            }
        }
    }
}
