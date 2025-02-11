package com.mjc.school.service;

import com.mjc.school.service.dto.NewsDtoRequest;
import com.mjc.school.service.dto.NewsDtoResponse;
import com.mjc.school.service.dto.ParametersDtoRequest;

import java.util.List;

public interface NewsService extends BaseService<NewsDtoRequest, NewsDtoResponse, Long> {
    List<NewsDtoResponse> readByParams(ParametersDtoRequest parametersDtoRequest);
}
