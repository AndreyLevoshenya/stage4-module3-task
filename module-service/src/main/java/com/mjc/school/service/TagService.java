package com.mjc.school.service;

import com.mjc.school.service.dto.TagDtoRequest;
import com.mjc.school.service.dto.TagDtoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TagService extends BaseService<TagDtoRequest, TagDtoResponse, Long> {
    Page<TagDtoResponse> readByNewsId(Long newsId, Pageable pageable);
}
