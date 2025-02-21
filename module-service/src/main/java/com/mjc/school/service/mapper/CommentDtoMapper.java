package com.mjc.school.service.mapper;

import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.model.Comment;
import com.mjc.school.service.dto.CommentDtoRequest;
import com.mjc.school.service.dto.CommentDtoResponse;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentDtoMapper {

    @Mappings({
            @Mapping(target = "newsDtoResponse", expression = "java(newsDtoMapper.modelToDto(model.getNews()))")
    })
    List<CommentDtoResponse> modelListToDtoList(List<Comment> modelList, @Context NewsDtoMapper newsDtoMapper);

    @Mappings({
            @Mapping(target = "newsDtoResponse", expression = "java(newsDtoMapper.modelToDto(model.getNews()))")
    })
    CommentDtoResponse modelToDto(Comment model, @Context NewsDtoMapper newsDtoMapper);

    @Mappings({
            @Mapping(target = "news", expression = "java(newsRepository.getReferenceById(dtoRequest.getNewsId()))"),
            @Mapping(target = "createDate", ignore = true),
            @Mapping(target = "lastUpdateDate", ignore = true),
    })
    Comment dtoToModel(CommentDtoRequest dtoRequest, @Context NewsRepository newsRepository);
}
