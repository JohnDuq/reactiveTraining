package com.udemy.springboot.api.rest.reactor.app.common;

public class DataCommon {
    private DataCommon() {
    }

    public static final String SLASH = "/";
    public static final String ID = "/{id}";
    public static final String UPLOAD_ID = "/upload/{id}";
    public static final String POST_ITEM_V2 = "/upload/v2";
    public static final String POST_ITEM_VALID = "/valid";
    public static final String POST_ITEM_VALID_V2 = "/valid/v2";

    public static final String COLLECTION_API_ITEM = "/api/item";
    public static final String COLLECTION_ITEM = "item";
    public static final String COLLECTION_BRAND = "brand";
}
