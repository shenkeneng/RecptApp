package com.frxs.receipt.model;

import com.frxs.receipt.greendao.entity.ProductEntity;

import java.io.Serializable;
import java.util.List;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/06/09
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class ProductInfo implements Serializable {

    private List<ProductEntity> Items;

    public List<ProductEntity> getItems() {
        return Items;
    }

    public void setItems(List<ProductEntity> items) {
        Items = items;
    }
}
