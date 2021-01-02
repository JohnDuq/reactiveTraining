package com.udemy.springboot.api.rest.reactor.app.controller.data;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@ToString
public class ErrorBadRequestResponse {
    
    private String field;
    private String error;
    private String date;
    private int status;

    public ErrorBadRequestResponse(FieldError fieldError, HttpStatus httpStatus){
        this.field = fieldError.getField();
        this.error = fieldError.getDefaultMessage();
        this.date = new Date().toString();
        this.status = httpStatus.value();
    }

}
