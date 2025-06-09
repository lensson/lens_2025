package com.lens.platform.auth.model;

import lombok.Data;

/**
 * @Author zhenac
 * @Created 5/22/25 12:15 PM
 */

@Data
public class Product {
    private long id;
    private String name;
    private int unitsPerCarton;
    private int cartonPrice;
    private float processingSingleLaborCharge;
    private int discountCartonsUnitsLowerLimit;
    private float cartonDiscount;
}
