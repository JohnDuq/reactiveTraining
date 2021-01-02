package com.udemy.springbootreactor.app.model;

import java.util.ArrayList;
import java.util.List;

import lombok.ToString;

@ToString
public class Comentario {

    private List<String> lComentarios;

    public Comentario() {
        this.lComentarios = new ArrayList<>();
    }

    public void agregarComentario(String comentario){
        lComentarios.add(comentario);
    }

}
