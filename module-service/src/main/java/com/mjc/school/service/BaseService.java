package com.mjc.school.service;

import com.mjc.school.service.dto.PageDtoResponse;
import com.mjc.school.service.dto.SearchingRequest;

public interface BaseService<T, R, K> {
    PageDtoResponse<R> readAll(SearchingRequest searchingRequest);

    R readById(K id);

    R create(T createRequest);

    R update(T updateRequest);

    R patch(T patchRequest);

    boolean deleteById(K id);
}
