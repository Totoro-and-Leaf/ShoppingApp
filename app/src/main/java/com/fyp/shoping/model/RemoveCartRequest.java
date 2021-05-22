package com.fyp.shoping.model;

public class RemoveCartRequest {

    private String barCode;

    private String quantity;

    private String orderCode;

    public RemoveCartRequest(String barCode, String orderCode) {
        this.barCode = barCode;
        this.orderCode = orderCode;
        this.quantity = "-1";
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }
}
