package com.homecraft.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cust_request")
public class CustRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "orders_id", nullable = false)
    private Integer ordersId;

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(name = "customer_id", nullable = false)
    private Integer customerId;

    @Column(name = "seller_id", nullable = false)
    private Integer sellerId;

    @Lob
    @Column(nullable = false)
    private String info;

}