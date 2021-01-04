package com.udemy.springboot.api.rest.reactor.app.model.documents;

import javax.validation.constraints.NotEmpty;

import com.udemy.springboot.api.rest.reactor.app.common.DocumentCollection;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Document(collection = DocumentCollection.COLLECTION_BRAND)
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@ToString
public class Brand {

    @Id
    @NotEmpty
    private String id;
    private String name;

    public Brand(String name) {
        this.name = name;
    }
    
}
