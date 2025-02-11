package com.mjc.school.repository.filter;

public class Pagination {
    private int pageNumber;
    private int pageSize;
    private SortDirection sortDirection;
    private String sortBy;

    public Pagination() {
    }

    public Pagination(int pageNumber, int pageSize, SortDirection sortDirection, String sortBy) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.sortDirection = sortDirection;
        this.sortBy = sortBy;
    }

    public enum SortDirection {
        ASC, DESC
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

    public SortDirection getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(SortDirection sortDirection) {
        this.sortDirection = sortDirection;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
}
