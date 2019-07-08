package com.frxs.receipt.model;

import java.io.Serializable;
import java.util.List;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/06/08
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class ReceivedOrder implements Serializable{


    /**
     * TotalOrders : 1
     * Items : [{"BuyID":"sample string 1","VendorID":2,"VendorCode":"sample string 3","VendorName":"sample string 4","PrepareQty":5,"ReceiveQty":6},{"BuyID":"sample string 1","VendorID":2,"VendorCode":"sample string 3","VendorName":"sample string 4","PrepareQty":5,"ReceiveQty":6},{"BuyID":"sample string 1","VendorID":2,"VendorCode":"sample string 3","VendorName":"sample string 4","PrepareQty":5,"ReceiveQty":6}]
     */

    private int TotalOrders;
    /**
     * BuyID : sample string 1
     * VendorID : 2
     * VendorCode : sample string 3
     * VendorName : sample string 4
     * PrepareQty : 5
     * ReceiveQty : 6
     */

    private List<ItemsBean> Items;

    public int getTotalOrders() {
        return TotalOrders;
    }

    public void setTotalOrders(int TotalOrders) {
        this.TotalOrders = TotalOrders;
    }

    public List<ItemsBean> getItems() {
        return Items;
    }

    public void setItems(List<ItemsBean> Items) {
        this.Items = Items;
    }

    public static class ItemsBean {
        private String BuyID;
        private String PreBuyID;
        private int VendorID;
        private String VendorCode;
        private String VendorName;
        private double PrepareQty;
        private double ReceiveQty;
        private String OrderDate;
        private String SignID; //查询签名信息
        private int IsSign; //判断签名状态 0：未签 1：已签
        private int Status;//代购单状态
        private String StatusStr;//代购单状态名称
        private int AutoPrintNum = -1;//打印结果 >0:显示再次打印


        public String getBuyID() {
            return BuyID;
        }

        public void setBuyID(String BuyID) {
            this.BuyID = BuyID;
        }

        public int getVendorID() {
            return VendorID;
        }

        public void setVendorID(int VendorID) {
            this.VendorID = VendorID;
        }

        public String getVendorCode() {
            return VendorCode;
        }

        public void setVendorCode(String VendorCode) {
            this.VendorCode = VendorCode;
        }

        public String getVendorName() {
            return VendorName;
        }

        public void setVendorName(String VendorName) {
            this.VendorName = VendorName;
        }

        public double getPrepareQty() {
            return PrepareQty;
        }

        public void setPrepareQty(double PrepareQty) {
            this.PrepareQty = PrepareQty;
        }

        public double getReceiveQty() {
            return ReceiveQty;
        }

        public void setReceiveQty(double ReceiveQty) {
            this.ReceiveQty = ReceiveQty;
        }

        public String getOrderDate() {
            return OrderDate;
        }

        public void setOrderDate(String orderDate) {
            OrderDate = orderDate;
        }

        public String getSignID() {
            return SignID;
        }

        public void setSignID(String signID) {
            SignID = signID;
        }

        public int getIsSign() {
            return IsSign;
        }

        public void setIsSign(int isSign) {
            IsSign = isSign;
        }

        public int getStatus() {
            return Status;
        }

        public void setStatus(int status) {
            Status = status;
        }

        public String getStatusStr() {
            return StatusStr;
        }

        public void setStatusStr(String statusStr) {
            StatusStr = statusStr;
        }

        public int getAutoPrintNum() {
            return AutoPrintNum;
        }

        public void setAutoPrintNum(int autoPrintNum) {
            AutoPrintNum = autoPrintNum;
        }

        public String getPreBuyID() {
            return PreBuyID;
        }

        public void setPreBuyID(String preBuyID) {
            PreBuyID = preBuyID;
        }
    }
}
