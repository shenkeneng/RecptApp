package com.frxs.receipt.model;

/**
 * Created by Chentie on 2017/8/29.
 */

public class OrderSigns {
    String ID;
    int CusType; // 客户类型(0:配送APP门店；1收货APP供应商；）
    String CusID; //  (0:配送APP门店id；1收货APP供应商id；）
    String SigerUserID;// 供应商ID
    String SigerUserName; // 供应商名
    String SignUrl; // 签名图片地址；
    String PhotoUrl1; // 现场照片地址；
    String MacID; //设备ID
    String Lng; // 经度
    String Lat; // 纬度
    String WID;
    String BillNo;// 订单ID
    int BillType;//  (0:配送APP门店id；1收货APP供应商id；）
    String ModifyUserID; // 用户ID
    String ModifyUserName;// 用户名
    String ModifyTime;// 签名时间
    String SignSmallUrl;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getCusType() {
        return CusType;
    }

    public void setCusType(int cusType) {
        CusType = cusType;
    }

    public String getCusID() {
        return CusID;
    }

    public void setCusID(String cusID) {
        CusID = cusID;
    }

    public String getSigerUserID() {
        return SigerUserID;
    }

    public void setSigerUserID(String sigerUserID) {
        SigerUserID = sigerUserID;
    }

    public String getSigerUserName() {
        return SigerUserName;
    }

    public void setSigerUserName(String sigerUserName) {
        SigerUserName = sigerUserName;
    }

    public String getSignUrl() {
        return SignUrl;
    }

    public void setSignUrl(String signUrl) {
        SignUrl = signUrl;
    }

    public String getPhotoUrl1() {
        return PhotoUrl1;
    }

    public void setPhotoUrl1(String photoUrl1) {
        PhotoUrl1 = photoUrl1;
    }

    public String getMacID() {
        return MacID;
    }

    public void setMacID(String macID) {
        MacID = macID;
    }

    public String getLng() {
        return Lng;
    }

    public void setLng(String lng) {
        Lng = lng;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getWID() {
        return WID;
    }

    public void setWID(String WID) {
        this.WID = WID;
    }

    public String getBillNo() {
        return BillNo;
    }

    public void setBillNo(String billNo) {
        BillNo = billNo;
    }

    public int getBillType() {
        return BillType;
    }

    public void setBillType(int billType) {
        BillType = billType;
    }

    public String getModifyUserID() {
        return ModifyUserID;
    }

    public void setModifyUserID(String modifyUserID) {
        ModifyUserID = modifyUserID;
    }

    public String getModifyUserName() {
        return ModifyUserName;
    }

    public void setModifyUserName(String modifyUserName) {
        ModifyUserName = modifyUserName;
    }

    public String getModifyTime() {
        return ModifyTime;
    }

    public void setModifyTime(String modifyTime) {
        ModifyTime = modifyTime;
    }

    public String getSignSmallUrl() {
        return SignSmallUrl;
    }

    public void setSignSmallUrl(String signSmallUrl) {
        SignSmallUrl = signSmallUrl;
    }
}
