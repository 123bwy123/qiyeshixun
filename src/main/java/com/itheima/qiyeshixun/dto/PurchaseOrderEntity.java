package com.itheima.qiyeshixun.dto; // 注意：如果你建在 entity 文件夹，这里就写 .entity

public class PurchaseOrderEntity {
    public Long id;
    public String purchaseNo;
    public Long supplierId;
    public Long deliveryAdminId;
    public Double totalAmount;
}