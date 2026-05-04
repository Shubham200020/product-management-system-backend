package com.example.products.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Table(name = "shops")
public class Shop {
    
    public Shop() {}
    
    public Shop(Long id, String name, String city, String address, String gstNumber, User owner, List<Category> categories, List<Product> products, List<SalesInvoice> salesInvoices) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.address = address;
        this.gstNumber = gstNumber;
        this.owner = owner;
        this.categories = categories;
        this.products = products;
        this.salesInvoices = salesInvoices;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String city;
    private String address;
    private String gstNumber;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User owner;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Category> categories = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Product> products = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SalesInvoice> salesInvoices = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Purchase> purchases = new java.util.ArrayList<>();

    public List<Purchase> getPurchases() { return purchases; }
    public void setPurchases(List<Purchase> purchases) { this.purchases = purchases; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    public List<Category> getCategories() { return categories; }
    public void setCategories(List<Category> categories) { this.categories = categories; }
    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
    public List<SalesInvoice> getSalesInvoices() { return salesInvoices; }
    public void setSalesInvoices(List<SalesInvoice> salesInvoices) { this.salesInvoices = salesInvoices; }
}
