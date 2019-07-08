package com.frxs.receipt.model;

/**
 * Created by Chentie on 2017/8/29.
 */

public class PostSubOrderSigns {

    private String OrderId;// 订单ID
    private String WID;
    private OrderSigns Signs;
    private String UserId;
    private String UserName;

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getWID() {
        return WID;
    }

    public void setWID(String WID) {
        this.WID = WID;
    }

    public OrderSigns getSigns() {
        return Signs;
    }

    public void setSigns(OrderSigns signs) {
        Signs = signs;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }
}
