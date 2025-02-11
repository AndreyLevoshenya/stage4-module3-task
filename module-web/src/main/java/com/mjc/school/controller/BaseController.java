package com.mjc.school.controller;

import com.mjc.school.service.dto.PageDtoResponse;
import org.springframework.http.ResponseEntity;

public interface BaseController<T, R, K> {

    ResponseEntity<PageDtoResponse<R>> readAll(int pageNumber, int pageSize, String sortBy, String searchBy);

    ResponseEntity<R> readById(K id);

    ResponseEntity<R> create(T createRequest);

    ResponseEntity<R> update(Long id, T updateRequest);

    ResponseEntity<R> patch(Long id, T patchRequest);

    void deleteById(K id);
}
