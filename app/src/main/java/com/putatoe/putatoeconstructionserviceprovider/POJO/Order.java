package com.putatoe.putatoeconstructionserviceprovider.POJO;

public class Order {


    private String customerName,customerNumber,materialType, quantityType , description
            , updatedByName, updatedByNumber,orderId,transactionType,timestamp,orderStatus,completionDate;

    private float totalAmount;
    float quantity;


    public Order() {
    }


    public Order(String customerName, String customerNumber, String materialType, String quantityType, String description, String updatedByName, String updatedByNumber, String orderId, String transactionType, String timestamp, String orderStatus, String completionDate, float totalAmount, float quantity) {
        this.customerName = customerName;
        this.customerNumber = customerNumber;
        this.materialType = materialType;
        this.quantityType = quantityType;
        this.description = description;
        this.updatedByName = updatedByName;
        this.updatedByNumber = updatedByNumber;
        this.orderId = orderId;
        this.transactionType = transactionType;
        this.timestamp = timestamp;
        this.orderStatus = orderStatus;
        this.completionDate = completionDate;
        this.totalAmount = totalAmount;
        this.quantity = quantity;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getQuantityType() {
        return quantityType;
    }

    public void setQuantityType(String quantityType) {
        this.quantityType = quantityType;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }



    public String getUpdatedByName() {
        return updatedByName;
    }

    public void setUpdatedByName(String updatedByName) {
        this.updatedByName = updatedByName;
    }

    public String getUpdatedByNumber() {
        return updatedByNumber;
    }

    public void setUpdatedByNumber(String updatedByNumber) {
        this.updatedByNumber = updatedByNumber;
    }

    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
    }
}
