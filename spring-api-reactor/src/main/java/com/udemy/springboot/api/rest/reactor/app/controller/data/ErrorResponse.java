package com.udemy.springboot.api.rest.reactor.app.controller.data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@ToString
public class ErrorResponse {
    
    private int status;
    private List<ErrorBadRequestResponse> errors;

}
