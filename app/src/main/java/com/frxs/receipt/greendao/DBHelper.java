package com.frxs.receipt.greendao;

import com.frxs.receipt.greendao.service.ProductEntityService;
import com.frxs.receipt.greendao.service.ReceivedListEntityService;
import com.frxs.receipt.greendao.utils.DbCore;

/**
 * Created by ewu on 2017/3/1.
 */

public class DBHelper {

    private static ProductEntityService productEntityService;

    private static ReceivedListEntityService receivedListEntityService;

    public static ProductEntityService getProductEntityService() {
        if (productEntityService == null) {
            productEntityService = new ProductEntityService(DbCore.getDaoSession().getProductEntityDao());
        }
        return productEntityService;
    }

    public static ReceivedListEntityService getReceivedListEntityService() {
        if (receivedListEntityService == null) {
            receivedListEntityService = new ReceivedListEntityService(DbCore.getDaoSession().getReceivedListEntityDao());
        }
        return receivedListEntityService;
    }
}
