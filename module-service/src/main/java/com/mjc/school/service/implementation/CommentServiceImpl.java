package com.mjc.school.service.implementation;

import com.mjc.school.repository.CommentRepository;
import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.filter.EntitySpecification;
import com.mjc.school.repository.model.Comment;
import com.mjc.school.service.CommentService;
import com.mjc.school.service.annotations.Valid;
import com.mjc.school.service.dto.CommentDtoRequest;
import com.mjc.school.service.dto.CommentDtoResponse;
import com.mjc.school.service.dto.SearchingRequest;
import com.mjc.school.service.exceptions.NotFoundException;
import com.mjc.school.service.mapper.CommentDtoMapper;
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

import static com.mjc.school.service.exceptions.ExceptionErrorCodes.COMMENT_DOES_NOT_EXIST;
import static com.mjc.school.service.exceptions.ExceptionErrorCodes.NEWS_DOES_NOT_EXIST;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CommentServiceImpl implements CommentService {
    private final static Logger LOGGER = LoggerFactory.getLogger(CommentServiceImpl.class);

    private final CommentRepository commentRepository;
    private final NewsRepository newsRepository;

    private final CommentDtoMapper commentDtoMapper;
    private final NewsDtoMapper newsDtoMapper;

    private final CommentServiceImpl self;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, NewsRepository newsRepository, CommentDtoMapper commentDtoMapper, NewsDtoMapper newsDtoMapper, CommentServiceImpl self) {
        this.commentRepository = commentRepository;
        this.newsRepository = newsRepository;
        this.commentDtoMapper = commentDtoMapper;
        this.newsDtoMapper = newsDtoMapper;
        this.self = self;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentDtoResponse> readAll(@Valid SearchingRequest searchingRequest, Pageable pageable) {
        LOGGER.info("Reading all the comments for {}" , searchingRequest);
        if (searchingRequest == null) {
            return commentRepository.findAll(pageable).map(comment -> commentDtoMapper.modelToDto(comment, newsDtoMapper));
        }
        String[] specs = searchingRequest.getFieldNameAndValue().split(":");
        Specification<Comment> specification = EntitySpecification.searchByField(specs[0], specs[1]);
        return commentRepository.findAll(specification, pageable).map(comment -> commentDtoMapper.modelToDto(comment, newsDtoMapper));
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDtoResponse readById(@Valid Long id) {
        LOGGER.info("Reading comment with id {}", id);
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("Comment with id {} not found", id);
                    return new NotFoundException(String.format(COMMENT_DOES_NOT_EXIST.getErrorMessage(), id));
                });
        return commentDtoMapper.modelToDto(comment, newsDtoMapper);
    }

    @Override
    @Transactional
    public CommentDtoResponse create(@Valid CommentDtoRequest createRequest) {
        LOGGER.info("Creating comment with id {}", createRequest.getId());
        Comment model = commentDtoMapper.dtoToModel(createRequest, newsRepository);
        return commentDtoMapper.modelToDto(commentRepository.save(model), newsDtoMapper);
    }

    @Override
    @Transactional
    public CommentDtoResponse update(@Valid CommentDtoRequest updateRequest) {
        LOGGER.info("Updating comment with id {}", updateRequest.getId());
        if (!commentRepository.existsById(updateRequest.getId())) {
            LOGGER.error("Comment with id {} not found. Unable to update comment", updateRequest.getId());
            throw new NotFoundException(String.format(COMMENT_DOES_NOT_EXIST.getErrorMessage(), updateRequest.getId()));
        }
        Comment comment = commentDtoMapper.dtoToModel(updateRequest, newsRepository);
        return commentDtoMapper.modelToDto(commentRepository.save(comment), newsDtoMapper);
    }

    @Override
    @Transactional
    public CommentDtoResponse patch(CommentDtoRequest patchRequest) {
        LOGGER.info("Patching comment with id {}", patchRequest.getId());
        Long id = patchRequest.getId();
        String content = patchRequest.getContent();

        Comment prevComment = commentRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("Comment with id {} not found. Unable to patch comment", id);
                    return new NotFoundException(String.format(COMMENT_DOES_NOT_EXIST.getErrorMessage(), id));
                });
        content = content != null ? content : prevComment.getContent();

        CommentDtoRequest updateRequest = new CommentDtoRequest(id, content, prevComment.getNews().getId());
        return self.update(updateRequest);
    }

    @Override
    @Transactional
    public void deleteById(@Valid Long id) {
        LOGGER.info("Deleting comment with id {}", id);
        if (!commentRepository.existsById(id)) {
            LOGGER.error("Comment with id {} not found. Unable to delete comment", id);
            throw new NotFoundException(String.format(COMMENT_DOES_NOT_EXIST.getErrorMessage(), id));
        }
        commentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentDtoResponse> readByNewsId(@Valid Long newsId, Pageable pageable) {
        LOGGER.info("Reading comments by news id {}", newsId);
        if (!newsRepository.existsById(newsId)) {
            LOGGER.error("News with id {} not found. Unable to read comments", newsId);
            throw new NotFoundException(String.format(NEWS_DOES_NOT_EXIST.getErrorMessage(), newsId));
        }
        return commentRepository.readByNewsId(newsId, pageable).map(comment -> commentDtoMapper.modelToDto(comment, newsDtoMapper));
    }
}
