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
public class PendingOrder implements Serializable {

    /**
     * PreBuyID : sample string 1
     * VendorID : 2
     * VendorCode : sample string 3
     * VendorName : sample string 4
     * DetailsCount : 5
     * ProductTotalQty : 6
     * OrderDate : 2017-06-08 14:52:48
     */
    private String Date; //客户端添加，方便适配ExpandableListView

    private List<ItemsBean> Items;

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public List<ItemsBean> getItems() {
        return Items;
    }

    public void setItems(List<ItemsBean> Items) {
        this.Items = Items;
    }

    public static class ItemsBean implements Serializable{
        private String PreBuyID;
        private int VendorID;
        private String VendorCode;
        private String VendorName;
        private int DetailsCount;
        private double ProductTotalQty;
        private String OrderDate;
        private String Remark;
        private int status;// 6等待收货，8正在收货
        private String PostingTime;// 过账时间

        public String getPreBuyID() {
            return PreBuyID;
        }

        public void setPreBuyID(String PreBuyID) {
            this.PreBuyID = PreBuyID;
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

        public int getDetailsCount() {
            return DetailsCount;
        }

        public void setDetailsCount(int DetailsCount) {
            this.DetailsCount = DetailsCount;
        }

        public double getProductTotalQty() {
            return ProductTotalQty;
        }

        public void setProductTotalQty(double ProductTotalQty) {
            this.ProductTotalQty = ProductTotalQty;
        }

        public String getOrderDate() {
            return OrderDate;
        }

        public void setOrderDate(String Date) {
            this.OrderDate = Date;
        }

        public String getRemark() {
            return Remark;
        }

        public void setRemark(String remark) {
            Remark = remark;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getPostingTime() {
            return PostingTime;
        }

        public void setPostingTime(String postingTime) {
            PostingTime = postingTime;
        }
    }
}
