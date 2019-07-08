package com.frxs.receipt.greendao.entity;

import android.text.TextUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.util.List;
import org.greenrobot.greendao.DaoException;
import com.frxs.receipt.greendao.gen.DaoSession;
import com.frxs.receipt.greendao.gen.ReceivedListEntityDao;
import com.frxs.receipt.greendao.gen.ProductEntityDao;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/06/08
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
@Entity
public class ProductEntity implements Serializable {
    private static final long serialVersionUID = -1;

    @Id
    private Long id;

    private String PreBuyID;

    private String VendorName;

    private String VendorCode;

    private String ProductId;

    private String ProductName;

    private String SKU;

    private String BarCode;

    private double BuyPackingQty;

    private String ShelfCode;

    private double BuyQty;

    private String BuyUnit; //大单位

    private String Unit; //小单位

    private double UnitQty;

    private String Remark;

    private String OrderRemark;//整单备注

    private String ImgUrl;

    private boolean isReceived; //客户端自定义字段标识是否已收货 true已收货 false未收货

    private double receivedQty = 0; //客户端自定义字段收货数量

    private String receivedUnit; //客户端自定义字段收货单位

    private String receivedRemark;// 客户端自定义字段收货备注

    private String BigUnitBarCode;//大单位条码

    private int SLType;//商品保质期类型 0:无保质期  1:有保质期天数（生产、到期日期不为空）  2:只有到期时期

    private int IsUserSL;//是否启用保质期 0:不启用  1:启用保质期

    private int GPDay;// 保质期天数

    private int GPMonth;// 保质期月数

    private int GPYear;// 保质期年数

    private String Spec;// 商品规格

    @ToMany(referencedJoinProperty = "pid")
    private List<ReceivedListEntity> receivedListEntities;

    @Transient
    private String tempUnit; //客户端自定义字段临时保留的字段做单位切换用

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 253932370)
    private transient ProductEntityDao myDao;

    @Generated(hash = 2036376678)
    public ProductEntity(Long id, String PreBuyID, String VendorName, String VendorCode,
            String ProductId, String ProductName, String SKU, String BarCode, double BuyPackingQty,
            String ShelfCode, double BuyQty, String BuyUnit, String Unit, double UnitQty, String Remark,
            String OrderRemark, String ImgUrl, boolean isReceived, double receivedQty,
            String receivedUnit, String receivedRemark, String BigUnitBarCode, int SLType, int IsUserSL,
            int GPDay, int GPMonth, int GPYear, String Spec) {
        this.id = id;
        this.PreBuyID = PreBuyID;
        this.VendorName = VendorName;
        this.VendorCode = VendorCode;
        this.ProductId = ProductId;
        this.ProductName = ProductName;
        this.SKU = SKU;
        this.BarCode = BarCode;
        this.BuyPackingQty = BuyPackingQty;
        this.ShelfCode = ShelfCode;
        this.BuyQty = BuyQty;
        this.BuyUnit = BuyUnit;
        this.Unit = Unit;
        this.UnitQty = UnitQty;
        this.Remark = Remark;
        this.OrderRemark = OrderRemark;
        this.ImgUrl = ImgUrl;
        this.isReceived = isReceived;
        this.receivedQty = receivedQty;
        this.receivedUnit = receivedUnit;
        this.receivedRemark = receivedRemark;
        this.BigUnitBarCode = BigUnitBarCode;
        this.SLType = SLType;
        this.IsUserSL = IsUserSL;
        this.GPDay = GPDay;
        this.GPMonth = GPMonth;
        this.GPYear = GPYear;
        this.Spec = Spec;
    }

    @Generated(hash = 27353230)
    public ProductEntity() {
    }

    public String getTempUnit() {
        if (TextUtils.isEmpty(tempUnit)) {
            tempUnit = TextUtils.isEmpty(receivedUnit) ? BuyUnit: receivedUnit;
        }
        return tempUnit;
    }

    public void setTempUnit(String tempUnit) {
        this.tempUnit = tempUnit;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPreBuyID() {
        return this.PreBuyID;
    }

    public void setPreBuyID(String PreBuyID) {
        this.PreBuyID = PreBuyID;
    }

    public String getVendorName() {
        return this.VendorName;
    }

    public void setVendorName(String VendorName) {
        this.VendorName = VendorName;
    }

    public String getVendorCode() {
        return this.VendorCode;
    }

    public void setVendorCode(String VendorCode) {
        this.VendorCode = VendorCode;
    }

    public String getProductId() {
        return this.ProductId;
    }

    public void setProductId(String ProductId) {
        this.ProductId = ProductId;
    }

    public String getProductName() {
        return this.ProductName;
    }

    public void setProductName(String ProductName) {
        this.ProductName = ProductName;
    }

    public String getSKU() {
        return this.SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }

    public String getBarCode() {
        return this.BarCode;
    }

    public void setBarCode(String BarCode) {
        this.BarCode = BarCode;
    }

    public double getBuyPackingQty() {
        return this.BuyPackingQty;
    }

    public void setBuyPackingQty(double BuyPackingQty) {
        this.BuyPackingQty = BuyPackingQty;
    }

    public String getShelfCode() {
        return this.ShelfCode;
    }

    public void setShelfCode(String ShelfCode) {
        this.ShelfCode = ShelfCode;
    }

    public double getBuyQty() {
        return this.BuyQty;
    }

    public void setBuyQty(double BuyQty) {
        this.BuyQty = BuyQty;
    }

    public String getBuyUnit() {
        return this.BuyUnit;
    }

    public void setBuyUnit(String BuyUnit) {
        this.BuyUnit = BuyUnit;
    }

    public String getUnit() {
        return this.Unit;
    }

    public void setUnit(String Unit) {
        this.Unit = Unit;
    }

    public double getUnitQty() {
        return this.UnitQty;
    }

    public void setUnitQty(double UnitQty) {
        this.UnitQty = UnitQty;
    }

    public String getRemark() {
        return this.Remark;
    }

    public void setRemark(String Remark) {
        this.Remark = Remark;
    }

    public String getImgUrl() {
        return this.ImgUrl;
    }

    public void setImgUrl(String ImgUrl) {
        this.ImgUrl = ImgUrl;
    }

    public boolean getIsReceived() {
        return this.isReceived;
    }

    public void setIsReceived(boolean isReceived) {
        this.isReceived = isReceived;
    }

    public double getReceivedQty() {
        return this.receivedQty;
    }

    public void setReceivedQty(double receivedQty) {
        this.receivedQty = receivedQty;
    }

    public String getReceivedUnit() {
        return this.receivedUnit;
    }

    public void setReceivedUnit(String receivedUnit) {
        this.receivedUnit = receivedUnit;
    }

    public String getReceivedRemark() {
        return this.receivedRemark;
    }

    public void setReceivedRemark(String receivedRemark) {
        this.receivedRemark = receivedRemark;
    }

    public String getBigUnitBarCode() {
        return this.BigUnitBarCode;
    }

    public void setBigUnitBarCode(String BigUnitBarCode) {
        this.BigUnitBarCode = BigUnitBarCode;
    }

    public String getOrderRemark() {
        return OrderRemark;
    }

    public void setOrderRemark(String orderRemark) {
        OrderRemark = orderRemark;
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

    public String getSpec() {
        return Spec;
    }

    public void setSpec(String spec) {
        Spec = spec;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1438930614)
    public List<ReceivedListEntity> getReceivedListEntities() {
        if (receivedListEntities == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ReceivedListEntityDao targetDao = daoSession.getReceivedListEntityDao();
            List<ReceivedListEntity> receivedListEntitiesNew = targetDao
                    ._queryProductEntity_ReceivedListEntities(id);
            synchronized (this) {
                if (receivedListEntities == null) {
                    receivedListEntities = receivedListEntitiesNew;
                }
            }
        }
        return receivedListEntities;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1102378549)
    public synchronized void resetReceivedListEntities() {
        receivedListEntities = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 317211899)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getProductEntityDao() : null;
    }
}
