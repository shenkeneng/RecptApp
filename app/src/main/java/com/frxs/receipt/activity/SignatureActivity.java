package com.frxs.receipt.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.frxs.core.model.TException;
import com.frxs.core.utils.BitmapUtil;
import com.frxs.core.utils.CommonUtils;
import com.frxs.core.utils.CoordinateUtils;
import com.frxs.core.utils.EasyPermissionsEx;
import com.frxs.core.utils.LogUtils;
import com.frxs.core.utils.SharedPreferencesHelper;
import com.frxs.core.utils.SystemUtils;
import com.frxs.core.utils.TUriParse;
import com.frxs.core.utils.ToastUtils;
import com.frxs.receipt.MyApplication;
import com.frxs.receipt.R;
import com.frxs.receipt.comms.Config;
import com.frxs.receipt.comms.GlobelDefines;
import com.frxs.receipt.model.OrderSigns;
import com.frxs.receipt.model.PostSubOrderSigns;
import com.frxs.receipt.model.UserInfo;
import com.frxs.receipt.model.VendorImagePath;
import com.frxs.receipt.rest.RestClient;
import com.frxs.receipt.rest.model.ApiResponse;
import com.frxs.receipt.rest.service.SimpleCallback;
import com.frxs.receipt.widget.LinePathView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * Created by Chentie on 2017/8/3.
 */

public class SignatureActivity extends MyBaseActivity {
    public final static int RC_PICK_PICTURE_FROM_CAPTURE = 1003;
    @BindView(R.id.line_path_view)
    LinePathView linePathView;
    @BindView(R.id.title_tv)
    TextView titleTv;
    public static SignatureActivity signatureActivity;
    private String orderId;// 订单ID
    private Bitmap signImg;
    private LocationManager locationManager;
    private byte[] picByteArray; //存储拍照压缩之后的图片
    private String latLongString;// 经纬度
    private HashMap<String, String> ImgUrlMap = new HashMap<String, String>();// 存储签名、现场url的map
    private Uri outPutUri;
    private String tempImgPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        ButterKnife.bind(this);

        initView();
        initDate();
    }

    private void initView() {
        titleTv.setText("电子签名");
        signatureActivity = this;
        // 获取经纬度
        formListenerGetLocation();
    }

    private void initDate() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            orderId = bundle.getString("order_id");
        }
        // 获取电话服务
        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, Config.PREFS_NAME);
        String deviceId = helper.getString(Config.KEY_DEVICE_ID, "");
        if (TextUtils.isEmpty(deviceId)) {
            String id = SystemUtils.getPhoneIMEI(this);

            if (id.equals("000000000000000") || TextUtils.isEmpty(id)) {
                LogUtils.d("IMEI---" + id);
                getSerialNumber();
            } else {
                helper.putValue(Config.KEY_DEVICE_ID, id);
            }
        } else {
            helper.putValue(Config.KEY_DEVICE_ID, deviceId);
        }
    }

    @OnClick({R.id.action_back_tv, R.id.tv_clean, R.id.tv_save})
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.action_back_tv:
                finish();
                break;

            case R.id.tv_clean:// 清除签名
                linePathView.clear();
                linePathView.setDisableSizeChange(false);
                break;

            case R.id.tv_save:
                if (CommonUtils.isFastDoubleClick()) {
                    return;
                }
                if (checkGPSIsOpen()) {
                    hasSignPermission();
                } else {
                    openGPSSettings();
                }
                break;

            default:
                break;
        }
    }

    /**
     * 初始化定位监听
     */
    private void formListenerGetLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, mLocationListener01);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, mLocationListener01);
        Location gps = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (gps != null) {
            latLongString =  CoordinateUtils.transform2Mars(gps.getLatitude(), gps.getLongitude(), true);
        }
    }

    /**
     * 通过location获取当前设备的具体位置
     *
     * @param location
     * @return
     */
    private String updateToNewLocation(Location location) {
        if (location != null) {
            latLongString =  CoordinateUtils.transform2Mars(location.getLatitude(), location.getLongitude(), true);
        }

        return latLongString;
    }

    /**
     * 设置监听器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N米
     */
    public LocationListener mLocationListener01 = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateToNewLocation(location);
        }


        @Override
        public void onProviderDisabled(String provider) {
            updateToNewLocation(null);
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    /**
     * 销毁时解除定位监听器
     */
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(mLocationListener01);
            locationManager = null;
        }
        if (signImg != null) {
            signImg = null;
        }
        if (ImgUrlMap != null) {
            ImgUrlMap.clear();
        }
    }

    public void startCamera(){
        File file = new File(Environment.getExternalStorageDirectory(), "/temp/"+ System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        outPutUri = Uri.fromFile(file);
        if (Build.VERSION.SDK_INT >= 23) {
            this.outPutUri = TUriParse.convertFileUriToFileProviderUri(this, outPutUri);
        } else {
            this.outPutUri = outPutUri;
        }
        startActivityForResult(getCaptureIntent(outPutUri), RC_PICK_PICTURE_FROM_CAPTURE);
    }

    public static Intent getCaptureIntent(Uri outPutUri) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);//将拍取的照片保存到指定URI
        return intent;
    }

    /**
     * 提交签名、现场图片
     */
    public void subSignImg() {
        showProgressDialog();
        Map<String, RequestBody> multiParts = new HashMap<String, RequestBody>();
        if (signImg != null) {
            multiParts.put("photos\"; filename=\"" + "sign", RequestBody.create(MediaType.parse("image"), BitmapUtil.bitmap2Bytes(signImg)));
        }

        if (picByteArray != null) {
            multiParts.put("photos\"; filename=\"" + "pictureImg", RequestBody.create(MediaType.parse("image"), picByteArray));
        }

        new RestClient(Config.getSubimgUrl()).getApiService().SubmitSignImage(multiParts).enqueue(new SimpleCallback<String>() {

            @Override
            public void onResponse(String result, int code, String msg) {
                dismissProgressDialog();
                // 去除json转义符
                String data = result.replace("\\\"", "");
                Type type = new TypeToken<ArrayList<JsonObject>>(){}.getType();
                ArrayList<JsonObject> jsonObjects = new Gson().fromJson(data, type);
                ArrayList<VendorImagePath> tmpBackList = new ArrayList<>();
                for (JsonObject jsonObject : jsonObjects) {
                    tmpBackList.add(new Gson().fromJson(jsonObject, VendorImagePath.class));
                }
                // 图片是否上传成功
                for (int i = 0; i < tmpBackList.size(); i++) {
                    if (tmpBackList.get(i).getCode().equals("0")) {
                        VendorImagePath imageDate = tmpBackList.get(i);
                        if (imageDate.getData() != null) {
                            if (imageDate.getInfo().equals("sign")) {
                                ImgUrlMap.put("sign", imageDate.getData().getImgPath());
                                ImgUrlMap.put("sign120", imageDate.getData().getImgPath120());
                            } else if (imageDate.getInfo().equals("pictureImg")) {
                                ImgUrlMap.put("pictureImg", imageDate.getData().getImgPath());
                                ImgUrlMap.put("pictureImg120", imageDate.getData().getImgPath120());
                            }
                        }
                    } else {
                        ToastUtils.show(SignatureActivity.this, "完成签名失败，请重新签名!");
                        return;
                    }
                }

                if (!TextUtils.isEmpty(ImgUrlMap.get("sign"))&& !TextUtils.isEmpty(ImgUrlMap.get("sign120")) && !TextUtils.isEmpty(ImgUrlMap.get("pictureImg"))) {
                    subSignInfo(); // 上传签名信息
                } else {
                    ToastUtils.show(SignatureActivity.this, "完成签名失败，请重新提交!");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                ToastUtils.show(SignatureActivity.this, t.getMessage());
            }
        });
    }


    /**
     * 提交签名信息
     */
    private void subSignInfo() {
        showProgressDialog();
        UserInfo userInfo = MyApplication.getInstance().getUserInfo();
        OrderSigns orderSigns = new OrderSigns();
        orderSigns.setWID(getWID());// 仓库id
        orderSigns.setBillNo(orderId.split(",")[0]);// 订单id
        orderSigns.setBillType(1);// 订单类型 0:门店  1：供应商
        orderSigns.setCusID("1");// 客户id 0:门店  1：供应商
        orderSigns.setCusType(1);// 客户类型 0:门店  1：供应商
        orderSigns.setID(SystemUtils.newRandomUUID());
        orderSigns.setModifyUserID(userInfo.getEmpID()); // 用户id
        orderSigns.setModifyUserName(userInfo.getEmpName()); // 用户名
        if (!TextUtils.isEmpty(latLongString)) {
            String[] location = latLongString.split(",");
            if (location.length > 1) {
                orderSigns.setLat(location[0]);
                orderSigns.setLng(location[1]);
            }
        }
        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, Config.PREFS_NAME);
        String deviceId = helper.getString(Config.KEY_DEVICE_ID, "");
        orderSigns.setMacID(deviceId);
        orderSigns.setSignUrl(ImgUrlMap.get("sign"));
        orderSigns.setPhotoUrl1(ImgUrlMap.get("pictureImg"));
        orderSigns.setSignSmallUrl(ImgUrlMap.get("sign120"));

        PostSubOrderSigns postSubOrderSigns = new PostSubOrderSigns();
        postSubOrderSigns.setOrderId(orderId.split(",")[0]);// 订单id
        postSubOrderSigns.setWID(getWID());// 仓库id
        postSubOrderSigns.setUserId(userInfo.getEmpID()); // 用户id
        postSubOrderSigns.setUserName(userInfo.getEmpName()); // 用户名
        postSubOrderSigns.setSigns(orderSigns);

        getService().SubOrderSigns(postSubOrderSigns).enqueue(new SimpleCallback<ApiResponse<String>>() {
            @Override
            public void onResponse(ApiResponse<String> result, int code, String msg) {
                dismissProgressDialog();
                if (result.getFlag().equals("0")) {
                    deletImage();
                    SignatureActivity.signatureActivity.finish();
                    finish();
                } else {
                    ToastUtils.show(SignatureActivity.this, result.getInfo());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                ToastUtils.show(SignatureActivity.this, t.getMessage());
            }
        });
    }

    /**
     * 删除图片
     */
    private void deletImage() {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = MediaStore.Images.Media.query(resolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=?",
                new String[]{tempImgPath}, null);
        boolean res = false;
        if (cursor.moveToFirst()) {
            long id = cursor.getLong(0);
            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uri = ContentUris.withAppendedId(contentUri, id);
            int count = getContentResolver().delete(uri, null, null);
            res = count == 1;
        } else {
            File file = new File(tempImgPath);
            res = file.delete();
        }
        //如果要删除的图片是收藏的图片，则要从收藏的表里删除
        if (!res) {
            Toast.makeText(SignatureActivity.this, "删除失败，可以在系统图库中删除。", Toast.LENGTH_LONG).show();
        }
    }

    private void getSerialNumber() {
        String serialnum = "";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            serialnum = (String) (get.invoke(c, "ro.serialno", "unknown"));
            LogUtils.d("SERIAL_NUM---" + serialnum);
        } catch (Exception ignored) {
        }

        if (!TextUtils.isEmpty(serialnum)) {
            SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, Config.PREFS_NAME);
            helper.putValue(Config.KEY_DEVICE_ID, serialnum);
        } else {
            String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            LogUtils.d("ANDROID_ID---" + androidId);
            if (!TextUtils.isEmpty(androidId)) {
                SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, Config.PREFS_NAME);
                helper.putValue(Config.KEY_DEVICE_ID, androidId);
            } else {
                String uuid = SystemUtils.newRandomUUID();
                SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, Config.PREFS_NAME);
                helper.putValue(Config.KEY_DEVICE_ID, uuid);
                LogUtils.d("UUID---" + uuid);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    /**
     * 检测GPS是否打开
     *
     * @return
     */
    public boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps && network) {
            return true;
        }

        return false;
    }

    /**
     * 跳转GPS设置
     */
    private void openGPSSettings() {
        if (checkGPSIsOpen()) {
            hasSignPermission();
        } else {
            //没有打开则弹出对话框
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("需进入下一步必须打开GPS功能。")
                    .setPositiveButton("去开启",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //跳转GPS设置界面
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivityForResult(intent, GlobelDefines.REQ_GPS_CODE);
                                }
                            })
                    // 拒绝, 退出应用
                    .setNegativeButton("取消", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    private void hasSignPermission() {
        if (EasyPermissionsEx.hasPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA})) {
            if (!linePathView.getTouched()) {
                Toast.makeText(SignatureActivity.this, "您没有签名~", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                signImg = linePathView.save();
                linePathView.setDisableSizeChange(true);
                startCamera();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ToastUtils.show(this, "请开启位置、相机、手机权限才能继续完成收货");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RC_PICK_PICTURE_FROM_CAPTURE == requestCode) {
            if ( resultCode == 0) {
                return;
            }
            try {
                String originalPath = TUriParse.getFilePathWithUri(outPutUri, this);
                compress(originalPath);
            } catch (TException e) {
                e.printStackTrace();
            }
        }
    }

    private void compress(String originalPath) {
        if (TextUtils.isEmpty(originalPath)){
            return;
        }

        compressImageByPixel(originalPath, 800, 300*1024);
    }

    /**
     * 按比例缩小图片的像素以达到压缩的目的
     * @author JPH
     * @param imgPath
     * @return
     * @date 2014-12-5下午11:30:59
     */
    private Bitmap compressImageByPixel(String imgPath, int maxPixel, int maxSize) {
        if(imgPath==null){
            ToastUtils.show(this,"要压缩的文件不存在");
            return null;
        }
        tempImgPath = imgPath;
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;//只读边,不读内容
        BitmapFactory.decodeFile(imgPath, newOpts);
        newOpts.inJustDecodeBounds = false;
        int width = newOpts.outWidth;
        int height = newOpts.outHeight;
        int be = 1;
        if (width >= height && width > maxPixel) {//缩放比,用高或者宽其中较大的一个数据进行计算
            be = (int) (newOpts.outWidth / maxPixel);
            be++;
        } else if (width < height && height > maxPixel) {
            be = (int) (newOpts.outHeight / maxPixel);
            be++;
        }

        newOpts.inSampleSize = be;//设置采样率
        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;//该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;//当系统内存不够时候图片自动被回收
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        compressImageByQuality(bitmap,imgPath, maxSize);//压缩好比例大小后再进行质量压缩
        return bitmap;
    }

    /**
     * 多线程压缩图片的质量
     * @author JPH
     * @param bitmap 内存中的图片
     * @param imgPath 图片的保存路径
     * @date 2014-12-5下午11:30:43
     */
    private void compressImageByQuality(final Bitmap bitmap, final String imgPath, final int maxSize){
        if(bitmap==null){
            ToastUtils.show(this,"像素压缩失败,bitmap is null");
            return;
        }

        Observable.create(new ObservableOnSubscribe<byte[]>() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int options = 100;
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//质量压缩方法，把压缩后的数据存放到baos中 (100表示不压缩，0表示压缩到最小)
                while (baos.toByteArray().length > maxSize) {//循环判断如果压缩后图片是否大于指定大小,大于继续压缩
                    baos.reset();//重置baos即让下一次的写入覆盖之前的内容
                    options -= 5;//图片质量每次减少5
                    if(options<=5)options=5;//如果图片质量小于5，为保证压缩后的图片质量，图片最底压缩质量为5
                    bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//将压缩后的图片保存到baos中
                    if(options==5)break;//如果图片的质量已降到最低则，不再进行压缩
                }
                e.onNext(baos.toByteArray());
            }
        }).subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        SignatureActivity.this.showProgressDialog();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<byte[]>() {
                    @Override
                    public void accept(byte[] bytes) throws Exception {
                        //TODO 提交压缩后的图片
                        SignatureActivity.this.dismissProgressDialog();
                        picByteArray = bytes;
                        subSignImg();
//                        Bitmap pictureImg = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                        ivPicture.setImageBitmap(pictureImg);
                    }
                });
    }


}
