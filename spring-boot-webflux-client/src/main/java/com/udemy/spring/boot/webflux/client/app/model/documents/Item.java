package com.udemy.spring.boot.webflux.client.app.model.documents;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Item {

    private String id;
    private String name;
    private Double price;
    private Date createAt;
    private Brand brand;
    private String photo;

    public Item(String name, Double price) {
        this.name = name;
        this.price = price;
    }

    public Item(String name, Double price, Brand brand) {
        this(name, price);
        this.brand = brand;
    }

}
