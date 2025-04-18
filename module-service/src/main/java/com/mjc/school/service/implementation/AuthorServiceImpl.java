package com.mjc.school.service.implementation;

import com.mjc.school.repository.AuthorRepository;
import com.mjc.school.repository.filter.EntitySpecification;
import com.mjc.school.repository.model.Author;
import com.mjc.school.service.AuthorService;
import com.mjc.school.service.annotations.Valid;
import com.mjc.school.service.dto.AuthorDtoRequest;
import com.mjc.school.service.dto.AuthorDtoResponse;
import com.mjc.school.service.dto.SearchingRequest;
import com.mjc.school.service.exceptions.NotFoundException;
import com.mjc.school.service.mapper.AuthorDtoMapper;
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

import static com.mjc.school.service.exceptions.ExceptionErrorCodes.AUTHOR_DOES_NOT_EXIST;
import static com.mjc.school.service.exceptions.ExceptionErrorCodes.NEWS_DOES_NOT_EXIST;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AuthorServiceImpl implements AuthorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorServiceImpl.class);

    private final AuthorRepository authorRepository;

    private final AuthorDtoMapper authorDtoMapper;

    private final AuthorServiceImpl self;

    @Autowired
    public AuthorServiceImpl(AuthorRepository authorRepository, AuthorDtoMapper authorDtoMapper, AuthorServiceImpl self) {
        this.authorRepository = authorRepository;
        this.authorDtoMapper = authorDtoMapper;
        this.self = self;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuthorDtoResponse> readAll(@Valid SearchingRequest searchingRequest, Pageable pageable) {
        LOGGER.info("Reading all authors for {}", searchingRequest);
        if (searchingRequest == null) {
            return authorRepository.findAll(pageable).map(authorDtoMapper::modelToDto);
        }
        String[] specs = searchingRequest.getFieldNameAndValue().split(":");
        Specification<Author> specification = EntitySpecification.searchByField(specs[0], specs[1]);
        return authorRepository.findAll(specification, pageable).map(authorDtoMapper::modelToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorDtoResponse readById(@Valid Long id) {
        LOGGER.info("Reading author by id {}", id);
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.warn("Author with id {} not found", id);
                    return new NotFoundException(String.format(AUTHOR_DOES_NOT_EXIST.getErrorMessage(), id));
                });
        return authorDtoMapper.modelToDto(author);
    }

    @Override
    @Transactional
    public AuthorDtoResponse create(@Valid AuthorDtoRequest createRequest) {
        LOGGER.info("Creating author {}", createRequest.getName());

        Author model = authorDtoMapper.dtoToModel(createRequest);
        Author author = authorRepository.save(model);
        return authorDtoMapper.modelToDto(author);
    }

    @Override
    @Transactional
    public AuthorDtoResponse update(@Valid AuthorDtoRequest updateRequest) {
        LOGGER.info("Updating author with id {}", updateRequest.getId());
        if (!authorRepository.existsById(updateRequest.getId())) {
            LOGGER.warn("Author with id {} not found. Unable to update author", updateRequest.getId());
            throw new NotFoundException(String.format(AUTHOR_DOES_NOT_EXIST.getErrorMessage(), updateRequest.getId()));
        }
        Author author = authorDtoMapper.dtoToModel(updateRequest);
        return authorDtoMapper.modelToDto(authorRepository.save(author));
    }

    @Override
    @Transactional
    public AuthorDtoResponse patch(AuthorDtoRequest patchRequest) {
        LOGGER.info("Patching author with id {}", patchRequest.getId());
        Long id = patchRequest.getId();
        String name = patchRequest.getName();

        Author prevAuthor = authorRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.warn("Author with id {} not found. Unable to patch author", id);
                    return new NotFoundException(String.format(AUTHOR_DOES_NOT_EXIST.getErrorMessage(), id));
                });
        name = name != null ? name : prevAuthor.getName();

        AuthorDtoRequest updateRequest = new AuthorDtoRequest(id, name);
        return self.update(updateRequest);
    }

    @Override
    @Transactional
    public void deleteById(@Valid Long id) {
        LOGGER.info("Deleting author by id {}", id);
        if (!authorRepository.existsById(id)) {
            LOGGER.warn("Author with id {} not found. Unable to delete author", id);
            throw new NotFoundException(String.format(AUTHOR_DOES_NOT_EXIST.getErrorMessage(), id));
        }
        authorRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorDtoResponse readByNewsId(@Valid Long newsId) {
        LOGGER.info("Reading author by news id {}", newsId);
        Author author = authorRepository.readByNewsId(newsId).orElseThrow(() -> {
            LOGGER.warn("News with id {} not found", newsId);
            return new NotFoundException(String.format(NEWS_DOES_NOT_EXIST.getErrorMessage(), newsId));
        });
        return authorDtoMapper.modelToDto(author);
    }
}
