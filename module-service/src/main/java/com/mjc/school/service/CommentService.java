package com.mjc.school.service;

import com.mjc.school.service.dto.CommentDtoRequest;
import com.mjc.school.service.dto.CommentDtoResponse;

import java.util.List;

public interface CommentService extends BaseService<CommentDtoRequest, CommentDtoResponse, Long> {
    List<CommentDtoResponse> readByNewsId(Long newsId);
}
