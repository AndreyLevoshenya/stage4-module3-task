package com.mjc.school.service.implementation;

import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.TagRepository;
import com.mjc.school.repository.filter.EntitySpecification;
import com.mjc.school.repository.model.Tag;
import com.mjc.school.service.TagService;
import com.mjc.school.service.annotations.Valid;
import com.mjc.school.service.dto.SearchingRequest;
import com.mjc.school.service.dto.TagDtoRequest;
import com.mjc.school.service.dto.TagDtoResponse;
import com.mjc.school.service.exceptions.NotFoundException;
import com.mjc.school.service.mapper.TagDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mjc.school.service.exceptions.ExceptionErrorCodes.NEWS_DOES_NOT_EXIST;
import static com.mjc.school.service.exceptions.ExceptionErrorCodes.TAG_DOES_NOT_EXIST;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final NewsRepository newsRepository;

    private final TagDtoMapper tagDtoMapper;

    private final TagServiceImpl self;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository, NewsRepository newsRepository, TagDtoMapper tagDtoMapper, TagServiceImpl self) {
        this.tagRepository = tagRepository;
        this.newsRepository = newsRepository;
        this.tagDtoMapper = tagDtoMapper;
        this.self = self;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TagDtoResponse> readAll(@Valid SearchingRequest searchingRequest, Pageable pageable) {
        if (searchingRequest == null) {
            return tagRepository.findAll(pageable).map(tagDtoMapper::modelToDto);
        }
        String[] specs = searchingRequest.getFieldNameAndValue().split(":");
        Specification<Tag> specification = EntitySpecification.searchByField(specs[0], specs[1]);
        return tagRepository.findAll(specification, pageable).map(tagDtoMapper::modelToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public TagDtoResponse readById(@Valid Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(TAG_DOES_NOT_EXIST.getErrorMessage(), id)));
        return tagDtoMapper.modelToDto(tag);
    }

    @Override
    @Transactional
    public TagDtoResponse create(@Valid TagDtoRequest createRequest) {
        Tag tag = tagDtoMapper.dtoToModel(createRequest);
        return tagDtoMapper.modelToDto(tagRepository.save(tag));
    }

    @Override
    @Transactional
    public TagDtoResponse update(@Valid TagDtoRequest updateRequest) {
        if (!tagRepository.existsById(updateRequest.getId())) {
            throw new NotFoundException(String.format(TAG_DOES_NOT_EXIST.getErrorMessage(), updateRequest.getId()));
        }
        Tag tag = tagDtoMapper.dtoToModel(updateRequest);
        return tagDtoMapper.modelToDto(tagRepository.save(tag));
    }

    @Override
    @Transactional
    public TagDtoResponse patch(TagDtoRequest patchRequest) {
        Long id = patchRequest.getId();
        String name = patchRequest.getName();
        if (id == null) {
            throw new NotFoundException(String.format(TAG_DOES_NOT_EXIST.getErrorMessage(), id));
        }
        Tag prevTag = tagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(TAG_DOES_NOT_EXIST.getErrorMessage(), id)));
        name = name != null ? name : prevTag.getName();

        TagDtoRequest updateRequest = new TagDtoRequest(id, name);
        return self.update(updateRequest);
    }

    @Override
    @Transactional
    public void deleteById(@Valid Long id) {
        if (!tagRepository.existsById(id)) {
            throw new NotFoundException(String.format(TAG_DOES_NOT_EXIST.getErrorMessage(), id));
        }
        tagRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TagDtoResponse> readByNewsId(@Valid Long newsId, Pageable pageable) {
        if (!newsRepository.existsById(newsId)) {
            throw new NotFoundException(String.format(NEWS_DOES_NOT_EXIST.getErrorMessage(), newsId));
        }
        return tagRepository.readByNewsId(newsId, pageable).map(tagDtoMapper::modelToDto);
    }
}
