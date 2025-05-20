package com.mjc.school.service.dto;

import com.mjc.school.service.annotation.Search;

public class SearchingRequest {
    @Search
    private String fieldNameAndValue;

    public SearchingRequest() {
    }

    public SearchingRequest(String fieldNameAndValue) {
        this.fieldNameAndValue = fieldNameAndValue;
    }

    public String getFieldNameAndValue() {
        return fieldNameAndValue;
    }

    public void setFieldNameAndValue(String fieldNameAndValue) {
        this.fieldNameAndValue = fieldNameAndValue;
    }
}
