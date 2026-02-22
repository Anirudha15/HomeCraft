package com.homecraft.api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "workshop_cust")
public class WorkshopCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "workshop_id", nullable = false)
    private Workshop workshop;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @Column(name = "cust_list", nullable = false)
    private String custList;


    public int getId() {
        return id;
    }

    public Workshop getWorkshop() {
        return workshop;
    }

    public void setWorkshop(Workshop workshop) {
        this.workshop = workshop;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public String getCustList() {
        return custList;
    }

    public void setCustList(String custList) {
        this.custList = custList;
    }
}
