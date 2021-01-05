package com.udemy.spring.boot.webflux.client.app.common;

public class Path {
    private Path() {
    }

    public static final String END_POINT = "http://localhost:8080/";
    public static final String SLASH = "/";
    public static final String ID = "/{id}";

    public static final String API_ITEM = "/api/item";
    public static final String API_V2_ITEM = "/api-v2/item";
    public static final String API_V2_OR_ITEM = "/api-v2-or/item";

    public static final String UPLOAD_ID = "/upload/{id}";
    public static final String UPLOAD_V2 = "/upload/v2";
    public static final String VALID = "/valid";
    public static final String VALID_V2 = "/valid/v2";
    public static final String FUNC = "/func";

}
