package com.udemy.springbootform.form.app.model;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Usuario {

    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
    @NotEmpty
    private String email;

}
