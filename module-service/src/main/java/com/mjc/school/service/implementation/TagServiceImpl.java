package com.mjc.school.service.implementation;

import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.TagRepository;
import com.mjc.school.repository.filter.Page;
import com.mjc.school.repository.filter.Pagination;
import com.mjc.school.repository.filter.SearchCriteria;
import com.mjc.school.repository.model.Tag;
import com.mjc.school.service.TagService;
import com.mjc.school.service.annotations.Valid;
import com.mjc.school.service.dto.PageDtoResponse;
import com.mjc.school.service.dto.SearchingRequest;
import com.mjc.school.service.dto.TagDtoRequest;
import com.mjc.school.service.dto.TagDtoResponse;
import com.mjc.school.service.exceptions.NotFoundException;
import com.mjc.school.service.mapper.TagDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.mjc.school.service.exceptions.ExceptionErrorCodes.NEWS_DOES_NOT_EXIST;
import static com.mjc.school.service.exceptions.ExceptionErrorCodes.TAG_DOES_NOT_EXIST;
import static com.mjc.school.service.utils.Utils.getPagination;
import static com.mjc.school.service.utils.Utils.getSearchCriteria;

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
    public PageDtoResponse<TagDtoResponse> readAll(@Valid SearchingRequest searchingRequest) {
        Pagination pagination = getPagination(searchingRequest);
        SearchCriteria searchCriteria = getSearchCriteria(searchingRequest);

        Page<Tag> page = tagRepository.readAll(pagination, searchCriteria);
        return new PageDtoResponse<>(tagDtoMapper.modelListToDtoList(page.getEntities()), page.getPageNumber(), page.getPagesCount());
    }

    @Override
    @Transactional(readOnly = true)
    public TagDtoResponse readById(@Valid Long id) {
        if (!tagRepository.existById(id)) {
            throw new NotFoundException(String.format(TAG_DOES_NOT_EXIST.getErrorMessage(), id));
        }
        Tag tag = tagRepository.readById(id).get();
        return tagDtoMapper.modelToDto(tag);
    }

    @Override
    @Transactional
    public TagDtoResponse create(@Valid TagDtoRequest createRequest) {
        Tag tag = tagDtoMapper.dtoToModel(createRequest);
        return tagDtoMapper.modelToDto(tagRepository.create(tag));
    }

    @Override
    @Transactional
    public TagDtoResponse update(@Valid TagDtoRequest updateRequest) {
        if (!tagRepository.existById(updateRequest.getId())) {
            throw new NotFoundException(String.format(TAG_DOES_NOT_EXIST.getErrorMessage(), updateRequest.getId()));
        }
        Tag tag = tagDtoMapper.dtoToModel(updateRequest);
        return tagDtoMapper.modelToDto(tagRepository.update(tag));
    }

    @Override
    @Transactional
    public TagDtoResponse patch(TagDtoRequest patchRequest) {
        Long id = patchRequest.getId();
        String name = patchRequest.getName();
        if (id == null || !tagRepository.existById(id)) {
            throw new NotFoundException(String.format(TAG_DOES_NOT_EXIST.getErrorMessage(), id));
        }
        Tag prevTag = tagRepository.readById(id).get();
        name = name != null ? name : prevTag.getName();

        TagDtoRequest updateRequest = new TagDtoRequest(id, name);
        return self.update(updateRequest);
    }

    @Override
    @Transactional
    public boolean deleteById(@Valid Long id) {
        if (!tagRepository.existById(id)) {
            throw new NotFoundException(String.format(TAG_DOES_NOT_EXIST.getErrorMessage(), id));
        }
        return tagRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagDtoResponse> readByNewsId(@Valid Long newsId) {
        if (!newsRepository.existById(newsId)) {
            throw new NotFoundException(String.format(NEWS_DOES_NOT_EXIST.getErrorMessage(), newsId));
        }
        return tagDtoMapper.modelListToDtoList(tagRepository.readByNewsId(newsId));
    }
}
