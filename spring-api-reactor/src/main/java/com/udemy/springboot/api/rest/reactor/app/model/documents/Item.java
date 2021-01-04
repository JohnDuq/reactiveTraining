package com.udemy.springboot.api.rest.reactor.app.model.documents;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.udemy.springboot.api.rest.reactor.app.common.DocumentCollection;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Document(collection = DocumentCollection.COLLECTION_ITEM)
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ToString
public class Item {
    
    @Id
    private String id;
    @NotEmpty
    private String name;
    @NotNull
    private Double price;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createAt;
    @Valid
    @NotNull
    private Brand brand;
    private String photo;

    public Item(String name, Double price){
        this.name = name;
        this.price = price;
    }

    public Item(String name, Double price, Brand brand) {
        this(name, price);
        this.brand = brand;
    }

}
