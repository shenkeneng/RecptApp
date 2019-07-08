package com.frxs.receipt.model;

import java.util.List;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/06/13
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class PostCompletedOrder {

    /**
     * WID : 1
     * ReceiveUserID : sample string 2
     * ReceiveUserName : sample string 3
     * Details : [{"PreBuyID":"sample string 1","ProductId":2,"PrepareUnit":"订单原始的采购单位","ReceiveUnit":"收货单位","ReceiveQty":5},{"PreBuyID":"sample string 1","ProductId":2,"PrepareUnit":"sample string 3","ReceiveUnit":"sample string 4","ReceiveQty":5},{"PreBuyID":"sample string 1","ProductId":2,"PrepareUnit":"sample string 3","ReceiveUnit":"sample string 4","ReceiveQty":5}]
     * UserId : 4
     * UserName : sample string 5
     */

    private String WID;
    private String ReceiveUserID;
    private String ReceiveUserName;
    /**
     * PreBuyID : sample string 1
     * ProductId : 2
     * PrepareUnit : 订单原始的采购单位
     * ReceiveUnit : 收货单位
     * ReceiveQty : 5.0
     */

    private List<DetailsBean> Details;

    public String getWID() {
        return WID;
    }

    public void setWID(String WID) {
        this.WID = WID;
    }

    public String getReceiveUserID() {
        return ReceiveUserID;
    }

    public void setReceiveUserID(String ReceiveUserID) {
        this.ReceiveUserID = ReceiveUserID;
    }

    public String getReceiveUserName() {
        return ReceiveUserName;
    }

    public void setReceiveUserName(String ReceiveUserName) {
        this.ReceiveUserName = ReceiveUserName;
    }

    public List<DetailsBean> getDetails() {
        return Details;
    }

    public void setDetails(List<DetailsBean> Details) {
        this.Details = Details;
    }

    public static class DetailsBean {
        private String PreBuyID;
        private String ProductId;
        private String PrepareUnit;
        private String ReceiveUnit;
        private double ReceiveQty;
        private String ReceiveRemark;
        private String LotNO; // 出厂批次
        private String EXP;// 到期日期
        private String PD;// 生产日期
        private int SLType;//商品保质期类型 0:无保质期  1:有保质期天数（生产、到期日期不为空）  2:只有到期时期
        private int IsUserSL;//是否启用保质期 0:不启用  1:启用保质期
        private int GPDay;// 保质期天数
        private int GPMonth;// 保质期月数
        private int GPYear;// 保质期年数

        public String getPreBuyID() {
            return PreBuyID;
        }

        public void setPreBuyID(String PreBuyID) {
            this.PreBuyID = PreBuyID;
        }

        public String getProductId() {
            return ProductId;
        }

        public void setProductId(String ProductId) {
            this.ProductId = ProductId;
        }

        public String getPrepareUnit() {
            return PrepareUnit;
        }

        public void setPrepareUnit(String PrepareUnit) {
            this.PrepareUnit = PrepareUnit;
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

        public String getReceiveRemark() {
            return ReceiveRemark;
        }

        public void setReceiveRemark(String receiveRemark) {
            ReceiveRemark = receiveRemark;
        }

        public String getLotNO() {
            return LotNO;
        }

        public void setLotNO(String lotNO) {
            LotNO = lotNO;
        }

        public String getEXP() {
            return EXP;
        }

        public void setEXP(String EXP) {
            this.EXP = EXP;
        }

        public String getPD() {
            return PD;
        }

        public void setPD(String PD) {
            this.PD = PD;
        }

        public int getSLType() {
            return SLType;
        }

        public void setSLType(int SLType) {
            this.SLType = SLType;
        }

        public int getIsUserSL() {
            return IsUserSL;
        }

        public void setIsUserSL(int isUserSL) {
            IsUserSL = isUserSL;
        }

        public int getGPDay() {
            return GPDay;
        }

        public void setGPDay(int GPDay) {
            this.GPDay = GPDay;
        }

        public int getGPMonth() {
            return GPMonth;
        }

        public void setGPMonth(int GPMonth) {
            this.GPMonth = GPMonth;
        }

        public int getGPYear() {
            return GPYear;
        }

        public void setGPYear(int GPYear) {
            this.GPYear = GPYear;
        }
    }
}
