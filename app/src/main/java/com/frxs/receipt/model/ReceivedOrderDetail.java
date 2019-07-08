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
public class ReceivedOrderDetail implements Serializable {


    /**
     * BuyID : sample string 1
     * VendorID : 2
     * VendorCode : sample string 3
     * VendorName : sample string 4
     * Item : [{"ProductId":1,"SKU":"sample string 2","ProductName":"sample string 3","BarCode":"sample string 4","PrepareUnit":"sample string 5","PrepareQty":6,"ReceiveUnit":"sample string 7","ReceiveQty":8},{"ProductId":1,"SKU":"sample string 2","ProductName":"sample string 3","BarCode":"sample string 4","PrepareUnit":"sample string 5","PrepareQty":6,"ReceiveUnit":"sample string 7","ReceiveQty":8},{"ProductId":1,"SKU":"sample string 2","ProductName":"sample string 3","BarCode":"sample string 4","PrepareUnit":"sample string 5","PrepareQty":6,"ReceiveUnit":"sample string 7","ReceiveQty":8}]
     */

    private String BuyID;
    private int VendorID;
    private String VendorCode;
    private String VendorName;
    private String SignID;// 签名ID
    private int IsSign; //签名状态标识 0：未签名  1：已签名
    private int AutoPrintNum = -1;//打印结果 >0:显示再次打印
    /**
     * ProductId : 1
     * SKU : sample string 2
     * ProductName : sample string 3
     * BarCode : sample string 4
     * PrepareUnit : sample string 5
     * PrepareQty : 6
     * ReceiveUnit : sample string 7
     * ReceiveQty : 8
     */

    private List<ItemBean> Items;

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

    public List<ItemBean> getItems() {
        return Items;
    }

    public void setItems(List<ItemBean> Items) {
        this.Items = Items;
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

    public int getAutoPrintNum() {
        return AutoPrintNum;
    }

    public void setAutoPrintNum(int autoPrintNum) {
        AutoPrintNum = autoPrintNum;
    }

    public static class ItemBean {
        private int ProductId;
        private String SKU;
        private String ProductName;
        private String BarCode;
        private String PrepareUnit;
        private double PrepareQty;
        private String ReceiveUnit;
        private double ReceiveQty;

        public int getProductId() {
            return ProductId;
        }

        public void setProductId(int ProductId) {
            this.ProductId = ProductId;
        }

        public String getSKU() {
            return SKU;
        }

        public void setSKU(String SKU) {
            this.SKU = SKU;
        }

        public String getProductName() {
            return ProductName;
        }

        public void setProductName(String ProductName) {
            this.ProductName = ProductName;
        }

        public String getBarCode() {
            return BarCode;
        }

        public void setBarCode(String BarCode) {
            this.BarCode = BarCode;
        }

        public String getPrepareUnit() {
            return PrepareUnit;
        }

        public void setPrepareUnit(String PrepareUnit) {
            this.PrepareUnit = PrepareUnit;
        }

        public double getPrepareQty() {
            return PrepareQty;
        }

        public void setPrepareQty(double PrepareQty) {
            this.PrepareQty = PrepareQty;
        }

        public String getReceiveUnit() {
            return ReceiveUnit;
        }

        public void setReceiveUnit(String ReceiveUnit) {
            this.ReceiveUnit = ReceiveUnit;
        }

        public double getReceiveQty() {
            return ReceiveQty;
        }

        public void setReceiveQty(double ReceiveQty) {
            this.ReceiveQty = ReceiveQty;
        }
    }
}
