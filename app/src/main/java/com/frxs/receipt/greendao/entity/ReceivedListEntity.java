package com.frxs.receipt.greendao.entity;

import com.frxs.receipt.greendao.gen.DaoSession;
import com.frxs.receipt.greendao.gen.ProductEntityDao;
import com.frxs.receipt.greendao.gen.ReceivedListEntityDao;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;

import java.io.Serializable;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/12/06
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
@Entity
public class ReceivedListEntity implements Serializable {
    private static final long serialVersionUID = -1;

    @Id
    private Long id;

    private Long pid; ///这个就是外键 就是ProductEntity的id

    @ToOne(joinProperty = "pid")
    private ProductEntity productEntity;

    private String batchNumber; //批次号

    private String productionDate; //生产日期

    private String dueDate; //到期日期

    private double receivedQty = 0;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1193992317)
    private transient ReceivedListEntityDao myDao;

    @Generated(hash = 1755884584)
    public ReceivedListEntity(Long id, Long pid, String batchNumber, String productionDate,
            String dueDate, double receivedQty) {
        this.id = id;
        this.pid = pid;
        this.batchNumber = batchNumber;
        this.productionDate = productionDate;
        this.dueDate = dueDate;
        this.receivedQty = receivedQty;
    }

    @Generated(hash = 362042439)
    public ReceivedListEntity() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPid() {
        return this.pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public double getReceivedQty() {
        return this.receivedQty;
    }

    public void setReceivedQty(double receivedQty) {
        this.receivedQty = receivedQty;
    }

    @Generated(hash = 563827515)
    private transient Long productEntity__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 349217397)
    public ProductEntity getProductEntity() {
        Long __key = this.pid;
        if (productEntity__resolvedKey == null || !productEntity__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ProductEntityDao targetDao = daoSession.getProductEntityDao();
            ProductEntity productEntityNew = targetDao.load(__key);
            synchronized (this) {
                productEntity = productEntityNew;
                productEntity__resolvedKey = __key;
            }
        }
        return productEntity;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1563166838)
    public void setProductEntity(ProductEntity productEntity) {
        synchronized (this) {
            this.productEntity = productEntity;
            pid = productEntity == null ? null : productEntity.getId();
            productEntity__resolvedKey = pid;
        }
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
    @Generated(hash = 811644374)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getReceivedListEntityDao() : null;
    }

    public String getBatchNumber() {
        return this.batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getProductionDate() {
        return this.productionDate;
    }

    public void setProductionDate(String productionDate) {
        this.productionDate = productionDate;
    }

    public String getDueDate() {
        return this.dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}
