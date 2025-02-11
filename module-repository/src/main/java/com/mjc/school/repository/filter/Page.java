package com.mjc.school.repository.filter;

import java.util.List;

public class Page<T> {
    private List<T> entities;
    private int pageNumber;
    private long pagesCount;

    public Page(List<T> entities, int pageNumber, long pagesCount) {
        this.entities = entities;
        this.pageNumber = pageNumber;
        this.pagesCount = pagesCount;
    }

    public List<T> getEntities() {
        return entities;
    }

    public void setEntities(List<T> entities) {
        this.entities = entities;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public long getPagesCount() {
        return pagesCount;
    }

    public void setPagesCount(long pagesCount) {
        this.pagesCount = pagesCount;
    }
}
