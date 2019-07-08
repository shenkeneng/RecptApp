package com.frxs.receipt.model;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/06/08
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class RcvOrderSectionListItem extends SectionListItem {

    private double totalPrepareQty;

    public double getTotalPrepareQty() {
        return totalPrepareQty;
    }

    public void setTotalPrepareQty(double totalPrepareQty) {
        this.totalPrepareQty = totalPrepareQty;
    }

    public double getTotalReceiveQty() {
        return totalReceiveQty;
    }

    public void setTotalReceiveQty(double totalReceiveQty) {
        this.totalReceiveQty = totalReceiveQty;
    }

    private double totalReceiveQty;

    public RcvOrderSectionListItem(Object item, int type, String section) {
        super(item, type, section);
    }

    private int totalOrderSize;

    public int getTotalOrderSize() {
        return totalOrderSize;
    }

    public void setTotalOrderSize(int totalOrderSize) {
        this.totalOrderSize = totalOrderSize;
    }
}
