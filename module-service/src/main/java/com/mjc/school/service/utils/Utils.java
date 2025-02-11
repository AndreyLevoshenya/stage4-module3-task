package com.mjc.school.service.utils;

import com.mjc.school.repository.filter.Pagination;
import com.mjc.school.repository.filter.SearchCriteria;
import com.mjc.school.service.dto.SearchingRequest;

public class Utils {
    public static Pagination getPagination(SearchingRequest searchingRequest) {
        int pageNumber = searchingRequest.getPageNumber();
        int pageSize = searchingRequest.getPageSize();

        String[] sort = searchingRequest.getSortBy().split(":");

        Pagination.SortDirection sortDirection = Pagination.SortDirection.DESC;
        if (sort[1].trim().equals("ASC") || sort[1].trim().equals("asc")) {
            sortDirection = Pagination.SortDirection.ASC;
        }
        String sortBy = sort[0].trim();
        return new Pagination(pageNumber, pageSize, sortDirection, sortBy);
    }

    public static SearchCriteria getSearchCriteria(SearchingRequest searchingRequest) {
        if (searchingRequest.getFieldNameAndValue() != null) {
            String[] search = searchingRequest.getFieldNameAndValue().split(":");
            return new SearchCriteria(search[0].trim(), search[1].trim());
        }
        return new SearchCriteria("", "");
    }
}
