package com.mjc.school.service.dto;

import com.mjc.school.service.annotations.Min;
import com.mjc.school.service.annotations.Search;
import com.mjc.school.service.annotations.Sort;

public class SearchingRequest {
    @Min(1)
    private int pageNumber;

    @Min(1)
    private int pageSize;

    @Sort
    private String sortBy;

    @Search
    private String fieldNameAndValue;

    public SearchingRequest() {
    }

    public SearchingRequest(int pageNumber, int pageSize, String sortBy, String fieldNameAndValue) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.sortBy = sortBy;
        this.fieldNameAndValue = fieldNameAndValue;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getFieldNameAndValue() {
        return fieldNameAndValue;
    }

    public void setFieldNameAndValue(String fieldNameAndValue) {
        this.fieldNameAndValue = fieldNameAndValue;
    }
}
