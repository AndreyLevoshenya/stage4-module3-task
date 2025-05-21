package com.mjc.school.mapper;

import com.mjc.school.model.Tag;
import com.mjc.school.dto.TagDtoRequest;
import com.mjc.school.dto.TagDtoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TagDtoMapper {

    TagDtoResponse modelToDto(Tag tag);

    @Mapping(target = "news", ignore = true)
    Tag dtoToModel(TagDtoRequest dtoRequest);
}
