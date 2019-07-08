package com.frxs.receipt.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.frxs.receipt.greendao.entity.ProductEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PRODUCT_ENTITY".
*/
public class ProductEntityDao extends AbstractDao<ProductEntity, Long> {

    public static final String TABLENAME = "PRODUCT_ENTITY";

    /**
     * Properties of entity ProductEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property PreBuyID = new Property(1, String.class, "PreBuyID", false, "PRE_BUY_ID");
        public final static Property VendorName = new Property(2, String.class, "VendorName", false, "VENDOR_NAME");
        public final static Property VendorCode = new Property(3, String.class, "VendorCode", false, "VENDOR_CODE");
        public final static Property ProductId = new Property(4, String.class, "ProductId", false, "PRODUCT_ID");
        public final static Property ProductName = new Property(5, String.class, "ProductName", false, "PRODUCT_NAME");
        public final static Property SKU = new Property(6, String.class, "SKU", false, "SKU");
        public final static Property BarCode = new Property(7, String.class, "BarCode", false, "BAR_CODE");
        public final static Property BuyPackingQty = new Property(8, double.class, "BuyPackingQty", false, "BUY_PACKING_QTY");
        public final static Property ShelfCode = new Property(9, String.class, "ShelfCode", false, "SHELF_CODE");
        public final static Property BuyQty = new Property(10, double.class, "BuyQty", false, "BUY_QTY");
        public final static Property BuyUnit = new Property(11, String.class, "BuyUnit", false, "BUY_UNIT");
        public final static Property Unit = new Property(12, String.class, "Unit", false, "UNIT");
        public final static Property UnitQty = new Property(13, double.class, "UnitQty", false, "UNIT_QTY");
        public final static Property Remark = new Property(14, String.class, "Remark", false, "REMARK");
        public final static Property OrderRemark = new Property(15, String.class, "OrderRemark", false, "ORDER_REMARK");
        public final static Property ImgUrl = new Property(16, String.class, "ImgUrl", false, "IMG_URL");
        public final static Property IsReceived = new Property(17, boolean.class, "isReceived", false, "IS_RECEIVED");
        public final static Property ReceivedQty = new Property(18, double.class, "receivedQty", false, "RECEIVED_QTY");
        public final static Property ReceivedUnit = new Property(19, String.class, "receivedUnit", false, "RECEIVED_UNIT");
        public final static Property ReceivedRemark = new Property(20, String.class, "receivedRemark", false, "RECEIVED_REMARK");
        public final static Property BigUnitBarCode = new Property(21, String.class, "BigUnitBarCode", false, "BIG_UNIT_BAR_CODE");
        public final static Property SLType = new Property(22, int.class, "SLType", false, "SLTYPE");
        public final static Property IsUserSL = new Property(23, int.class, "IsUserSL", false, "IS_USER_SL");
        public final static Property GPDay = new Property(24, int.class, "GPDay", false, "GPDAY");
        public final static Property GPMonth = new Property(25, int.class, "GPMonth", false, "GPMONTH");
        public final static Property GPYear = new Property(26, int.class, "GPYear", false, "GPYEAR");
        public final static Property Spec = new Property(27, String.class, "Spec", false, "SPEC");
    }

    private DaoSession daoSession;


    public ProductEntityDao(DaoConfig config) {
        super(config);
    }
    
    public ProductEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PRODUCT_ENTITY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"PRE_BUY_ID\" TEXT," + // 1: PreBuyID
                "\"VENDOR_NAME\" TEXT," + // 2: VendorName
                "\"VENDOR_CODE\" TEXT," + // 3: VendorCode
                "\"PRODUCT_ID\" TEXT," + // 4: ProductId
                "\"PRODUCT_NAME\" TEXT," + // 5: ProductName
                "\"SKU\" TEXT," + // 6: SKU
                "\"BAR_CODE\" TEXT," + // 7: BarCode
                "\"BUY_PACKING_QTY\" REAL NOT NULL ," + // 8: BuyPackingQty
                "\"SHELF_CODE\" TEXT," + // 9: ShelfCode
                "\"BUY_QTY\" REAL NOT NULL ," + // 10: BuyQty
                "\"BUY_UNIT\" TEXT," + // 11: BuyUnit
                "\"UNIT\" TEXT," + // 12: Unit
                "\"UNIT_QTY\" REAL NOT NULL ," + // 13: UnitQty
                "\"REMARK\" TEXT," + // 14: Remark
                "\"ORDER_REMARK\" TEXT," + // 15: OrderRemark
                "\"IMG_URL\" TEXT," + // 16: ImgUrl
                "\"IS_RECEIVED\" INTEGER NOT NULL ," + // 17: isReceived
                "\"RECEIVED_QTY\" REAL NOT NULL ," + // 18: receivedQty
                "\"RECEIVED_UNIT\" TEXT," + // 19: receivedUnit
                "\"RECEIVED_REMARK\" TEXT," + // 20: receivedRemark
                "\"BIG_UNIT_BAR_CODE\" TEXT," + // 21: BigUnitBarCode
                "\"SLTYPE\" INTEGER NOT NULL ," + // 22: SLType
                "\"IS_USER_SL\" INTEGER NOT NULL ," + // 23: IsUserSL
                "\"GPDAY\" INTEGER NOT NULL ," + // 24: GPDay
                "\"GPMONTH\" INTEGER NOT NULL ," + // 25: GPMonth
                "\"GPYEAR\" INTEGER NOT NULL ," + // 26: GPYear
                "\"SPEC\" TEXT);"); // 27: Spec
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PRODUCT_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ProductEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String PreBuyID = entity.getPreBuyID();
        if (PreBuyID != null) {
            stmt.bindString(2, PreBuyID);
        }
 
        String VendorName = entity.getVendorName();
        if (VendorName != null) {
            stmt.bindString(3, VendorName);
        }
 
        String VendorCode = entity.getVendorCode();
        if (VendorCode != null) {
            stmt.bindString(4, VendorCode);
        }
 
        String ProductId = entity.getProductId();
        if (ProductId != null) {
            stmt.bindString(5, ProductId);
        }
 
        String ProductName = entity.getProductName();
        if (ProductName != null) {
            stmt.bindString(6, ProductName);
        }
 
        String SKU = entity.getSKU();
        if (SKU != null) {
            stmt.bindString(7, SKU);
        }
 
        String BarCode = entity.getBarCode();
        if (BarCode != null) {
            stmt.bindString(8, BarCode);
        }
        stmt.bindDouble(9, entity.getBuyPackingQty());
 
        String ShelfCode = entity.getShelfCode();
        if (ShelfCode != null) {
            stmt.bindString(10, ShelfCode);
        }
        stmt.bindDouble(11, entity.getBuyQty());
 
        String BuyUnit = entity.getBuyUnit();
        if (BuyUnit != null) {
            stmt.bindString(12, BuyUnit);
        }
 
        String Unit = entity.getUnit();
        if (Unit != null) {
            stmt.bindString(13, Unit);
        }
        stmt.bindDouble(14, entity.getUnitQty());
 
        String Remark = entity.getRemark();
        if (Remark != null) {
            stmt.bindString(15, Remark);
        }
 
        String OrderRemark = entity.getOrderRemark();
        if (OrderRemark != null) {
            stmt.bindString(16, OrderRemark);
        }
 
        String ImgUrl = entity.getImgUrl();
        if (ImgUrl != null) {
            stmt.bindString(17, ImgUrl);
        }
        stmt.bindLong(18, entity.getIsReceived() ? 1L: 0L);
        stmt.bindDouble(19, entity.getReceivedQty());
 
        String receivedUnit = entity.getReceivedUnit();
        if (receivedUnit != null) {
            stmt.bindString(20, receivedUnit);
        }
 
        String receivedRemark = entity.getReceivedRemark();
        if (receivedRemark != null) {
            stmt.bindString(21, receivedRemark);
        }
 
        String BigUnitBarCode = entity.getBigUnitBarCode();
        if (BigUnitBarCode != null) {
            stmt.bindString(22, BigUnitBarCode);
        }
        stmt.bindLong(23, entity.getSLType());
        stmt.bindLong(24, entity.getIsUserSL());
        stmt.bindLong(25, entity.getGPDay());
        stmt.bindLong(26, entity.getGPMonth());
        stmt.bindLong(27, entity.getGPYear());
 
        String Spec = entity.getSpec();
        if (Spec != null) {
            stmt.bindString(28, Spec);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ProductEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String PreBuyID = entity.getPreBuyID();
        if (PreBuyID != null) {
            stmt.bindString(2, PreBuyID);
        }
 
        String VendorName = entity.getVendorName();
        if (VendorName != null) {
            stmt.bindString(3, VendorName);
        }
 
        String VendorCode = entity.getVendorCode();
        if (VendorCode != null) {
            stmt.bindString(4, VendorCode);
        }
 
        String ProductId = entity.getProductId();
        if (ProductId != null) {
            stmt.bindString(5, ProductId);
        }
 
        String ProductName = entity.getProductName();
        if (ProductName != null) {
            stmt.bindString(6, ProductName);
        }
 
        String SKU = entity.getSKU();
        if (SKU != null) {
            stmt.bindString(7, SKU);
        }
 
        String BarCode = entity.getBarCode();
        if (BarCode != null) {
            stmt.bindString(8, BarCode);
        }
        stmt.bindDouble(9, entity.getBuyPackingQty());
 
        String ShelfCode = entity.getShelfCode();
        if (ShelfCode != null) {
            stmt.bindString(10, ShelfCode);
        }
        stmt.bindDouble(11, entity.getBuyQty());
 
        String BuyUnit = entity.getBuyUnit();
        if (BuyUnit != null) {
            stmt.bindString(12, BuyUnit);
        }
 
        String Unit = entity.getUnit();
        if (Unit != null) {
            stmt.bindString(13, Unit);
        }
        stmt.bindDouble(14, entity.getUnitQty());
 
        String Remark = entity.getRemark();
        if (Remark != null) {
            stmt.bindString(15, Remark);
        }
 
        String OrderRemark = entity.getOrderRemark();
        if (OrderRemark != null) {
            stmt.bindString(16, OrderRemark);
        }
 
        String ImgUrl = entity.getImgUrl();
        if (ImgUrl != null) {
            stmt.bindString(17, ImgUrl);
        }
        stmt.bindLong(18, entity.getIsReceived() ? 1L: 0L);
        stmt.bindDouble(19, entity.getReceivedQty());
 
        String receivedUnit = entity.getReceivedUnit();
        if (receivedUnit != null) {
            stmt.bindString(20, receivedUnit);
        }
 
        String receivedRemark = entity.getReceivedRemark();
        if (receivedRemark != null) {
            stmt.bindString(21, receivedRemark);
        }
 
        String BigUnitBarCode = entity.getBigUnitBarCode();
        if (BigUnitBarCode != null) {
            stmt.bindString(22, BigUnitBarCode);
        }
        stmt.bindLong(23, entity.getSLType());
        stmt.bindLong(24, entity.getIsUserSL());
        stmt.bindLong(25, entity.getGPDay());
        stmt.bindLong(26, entity.getGPMonth());
        stmt.bindLong(27, entity.getGPYear());
 
        String Spec = entity.getSpec();
        if (Spec != null) {
            stmt.bindString(28, Spec);
        }
    }

    @Override
    protected final void attachEntity(ProductEntity entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public ProductEntity readEntity(Cursor cursor, int offset) {
        ProductEntity entity = new ProductEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // PreBuyID
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // VendorName
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // VendorCode
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // ProductId
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // ProductName
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // SKU
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // BarCode
            cursor.getDouble(offset + 8), // BuyPackingQty
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // ShelfCode
            cursor.getDouble(offset + 10), // BuyQty
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // BuyUnit
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // Unit
            cursor.getDouble(offset + 13), // UnitQty
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // Remark
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // OrderRemark
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), // ImgUrl
            cursor.getShort(offset + 17) != 0, // isReceived
            cursor.getDouble(offset + 18), // receivedQty
            cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19), // receivedUnit
            cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20), // receivedRemark
            cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21), // BigUnitBarCode
            cursor.getInt(offset + 22), // SLType
            cursor.getInt(offset + 23), // IsUserSL
            cursor.getInt(offset + 24), // GPDay
            cursor.getInt(offset + 25), // GPMonth
            cursor.getInt(offset + 26), // GPYear
            cursor.isNull(offset + 27) ? null : cursor.getString(offset + 27) // Spec
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ProductEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPreBuyID(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setVendorName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setVendorCode(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setProductId(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setProductName(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setSKU(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setBarCode(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setBuyPackingQty(cursor.getDouble(offset + 8));
        entity.setShelfCode(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setBuyQty(cursor.getDouble(offset + 10));
        entity.setBuyUnit(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setUnit(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setUnitQty(cursor.getDouble(offset + 13));
        entity.setRemark(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setOrderRemark(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setImgUrl(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setIsReceived(cursor.getShort(offset + 17) != 0);
        entity.setReceivedQty(cursor.getDouble(offset + 18));
        entity.setReceivedUnit(cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19));
        entity.setReceivedRemark(cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20));
        entity.setBigUnitBarCode(cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21));
        entity.setSLType(cursor.getInt(offset + 22));
        entity.setIsUserSL(cursor.getInt(offset + 23));
        entity.setGPDay(cursor.getInt(offset + 24));
        entity.setGPMonth(cursor.getInt(offset + 25));
        entity.setGPYear(cursor.getInt(offset + 26));
        entity.setSpec(cursor.isNull(offset + 27) ? null : cursor.getString(offset + 27));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(ProductEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(ProductEntity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(ProductEntity entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}