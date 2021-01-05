package com.udemy.spring.boot.webflux.client.app.model.documents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Brand {

    private String id;
    private String name;

    public Brand(String name) {
        this.name = name;
    }

}
