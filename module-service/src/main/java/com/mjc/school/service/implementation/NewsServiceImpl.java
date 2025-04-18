package com.mjc.school.service.implementation;

import com.mjc.school.repository.AuthorRepository;
import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.TagRepository;
import com.mjc.school.repository.filter.EntitySpecification;
import com.mjc.school.repository.model.News;
import com.mjc.school.repository.model.SearchParameters;
import com.mjc.school.repository.model.Tag;
import com.mjc.school.service.NewsService;
import com.mjc.school.service.annotations.Valid;
import com.mjc.school.service.dto.NewsDtoRequest;
import com.mjc.school.service.dto.NewsDtoResponse;
import com.mjc.school.service.dto.ParametersDtoRequest;
import com.mjc.school.service.dto.SearchingRequest;
import com.mjc.school.service.exceptions.NotFoundException;
import com.mjc.school.service.mapper.NewsDtoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.mjc.school.service.exceptions.ExceptionErrorCodes.AUTHOR_DOES_NOT_EXIST;
import static com.mjc.school.service.exceptions.ExceptionErrorCodes.NEWS_DOES_NOT_EXIST;
import static com.mjc.school.service.exceptions.ExceptionErrorCodes.TAG_DOES_NOT_EXIST;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class NewsServiceImpl implements NewsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewsServiceImpl.class);

    private final NewsRepository newsRepository;
    private final AuthorRepository authorRepository;
    private final TagRepository tagRepository;
    private final NewsDtoMapper newsDtoMapper;

    private final NewsServiceImpl self;

    @Autowired
    public NewsServiceImpl(NewsRepository newsRepository, AuthorRepository authorRepository, TagRepository tagRepository, NewsDtoMapper newsDtoMapper, NewsServiceImpl self) {
        this.newsRepository = newsRepository;
        this.authorRepository = authorRepository;
        this.tagRepository = tagRepository;
        this.newsDtoMapper = newsDtoMapper;
        this.self = self;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NewsDtoResponse> readAll(@Valid SearchingRequest searchingRequest, Pageable pageable) {
        LOGGER.info("Reading all the news for {}", searchingRequest);
        if (searchingRequest == null) {
            return newsRepository.findAll(pageable).map(newsDtoMapper::modelToDto);
        }
        String[] specs = searchingRequest.getFieldNameAndValue().split(":");
        Specification<News> specification = EntitySpecification.searchByField(specs[0], specs[1]);
        return newsRepository.findAll(specification, pageable).map(newsDtoMapper::modelToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public NewsDtoResponse readById(@Valid Long id) {
        LOGGER.info("Reading news with id {}", id);
        News news = newsRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("News with id {} not found", id);
                    return new NotFoundException(String.format(NEWS_DOES_NOT_EXIST.getErrorMessage(), id));
                });
        return newsDtoMapper.modelToDto(news);
    }

    @Override
    @Transactional
    public NewsDtoResponse create(@Valid NewsDtoRequest createRequest) {
        LOGGER.info("Creating new news {}", createRequest);
        if (!authorRepository.existsById(createRequest.getAuthorId())) {
            LOGGER.error("Author with id {} not found. Unable to create news", createRequest.getAuthorId());
            throw new NotFoundException(String.format(AUTHOR_DOES_NOT_EXIST.getErrorMessage(), createRequest.getAuthorId()));
        }
        News model = newsDtoMapper.dtoToModel(createRequest, newsRepository, authorRepository, tagRepository);
        return newsDtoMapper.modelToDto(newsRepository.save(model));
    }

    @Override
    @Transactional
    public NewsDtoResponse update(@Valid NewsDtoRequest updateRequest) {
        LOGGER.info("Updating news with id {}", updateRequest);
        if (!newsRepository.existsById(updateRequest.getId())) {
            LOGGER.error("News with id {} not found. Unable to update news", updateRequest.getId());
            throw new NotFoundException(String.format(NEWS_DOES_NOT_EXIST.getErrorMessage(), updateRequest.getId()));
        }
        if (!authorRepository.existsById(updateRequest.getAuthorId())) {
            LOGGER.error("Author with id {} not found. Unable to update news", updateRequest.getAuthorId());
            throw new NotFoundException(String.format(AUTHOR_DOES_NOT_EXIST.getErrorMessage(), updateRequest.getAuthorId()));
        }

        for (Long id : updateRequest.getTagIds()) {
            if (!tagRepository.existsById(id)) {
                LOGGER.error("Tag with id {} not found. Unable to update news", id);
                throw new NotFoundException(String.format(TAG_DOES_NOT_EXIST.getErrorMessage(), id));
            }
        }

        News news = newsDtoMapper.dtoToModel(updateRequest, newsRepository, authorRepository, tagRepository);
        return newsDtoMapper.modelToDto(newsRepository.save(news));
    }

    @Override
    @Transactional
    public NewsDtoResponse patch(NewsDtoRequest patchRequest) {
        LOGGER.info("Patching news with id {}", patchRequest);
        Long id = patchRequest.getId();
        String title = patchRequest.getTitle();
        String content = patchRequest.getContent();
        Long authorId = patchRequest.getAuthorId();
        List<Long> tagIds = patchRequest.getTagIds();

        News prevNews = newsRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("News with id {} not found. Unable to patch news", id);
                    return new NotFoundException(String.format(NEWS_DOES_NOT_EXIST.getErrorMessage(), id));
                });
        title = title != null ? title : prevNews.getTitle();
        content = content != null ? content : prevNews.getContent();
        if (authorId == null) {
            authorId = prevNews.getAuthor().getId();
        }

        if (tagIds == null) {
            tagIds = prevNews.getTags().stream().map(Tag::getId).toList();
        }
        return self.update(new NewsDtoRequest(id, title, content, authorId, tagIds));
    }

    @Override
    @Transactional
    public void deleteById(@Valid Long id) {
        LOGGER.info("Deleting news with id {}", id);
        if (!newsRepository.existsById(id)) {
            LOGGER.error("News with id {} not found. Unable to delete news", id);
            throw new NotFoundException(String.format(NEWS_DOES_NOT_EXIST.getErrorMessage(), id));
        }
        newsRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NewsDtoResponse> readByParams(ParametersDtoRequest parametersDtoRequest, Pageable pageable) {
        LOGGER.info("Reading news by params {}", parametersDtoRequest);
        SearchParameters params = new SearchParameters(
                !parametersDtoRequest.newsTitle().isEmpty() ? parametersDtoRequest.newsTitle() : null,
                !parametersDtoRequest.newsContent().isEmpty() ? parametersDtoRequest.newsContent() : null,
                !parametersDtoRequest.authorName().isEmpty() ? parametersDtoRequest.authorName() : null,
                (parametersDtoRequest.tagIds() != null && !parametersDtoRequest.tagIds().isEmpty()) ? parametersDtoRequest.tagIds() : null,
                (parametersDtoRequest.tagNames() != null && !parametersDtoRequest.tagNames().isEmpty()) ? parametersDtoRequest.tagNames() : null);
        return newsRepository.readByParams(params, pageable).map(newsDtoMapper::modelToDto);
    }
}
