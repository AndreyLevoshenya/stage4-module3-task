package com.mjc.school.service.implementation;

import com.mjc.school.repository.CommentRepository;
import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.filter.Page;
import com.mjc.school.repository.model.Author;
import com.mjc.school.repository.model.Comment;
import com.mjc.school.repository.model.News;
import com.mjc.school.service.dto.CommentDtoRequest;
import com.mjc.school.service.dto.CommentDtoResponse;
import com.mjc.school.service.dto.PageDtoResponse;
import com.mjc.school.service.dto.SearchingRequest;
import com.mjc.school.service.exceptions.NotFoundException;
import com.mjc.school.service.exceptions.ValidationException;
import com.mjc.school.service.implementation.config.JPAConfig;
import com.mjc.school.service.implementation.config.TestConfig;
import com.mjc.school.service.mapper.CommentDtoMapper;
import com.mjc.school.service.mapper.NewsDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mjc.school.service.exceptions.ExceptionErrorCodes.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {TestConfig.class, JPAConfig.class})
class CommentServiceImplTest {
    @MockBean
    private CommentRepository commentRepository;
    @MockBean
    private NewsRepository newsRepository;

    @Autowired
    private CommentDtoMapper commentDtoMapper;

    @Autowired
    private NewsDtoMapper newsDtoMapper;

    @Autowired
    private CommentServiceImpl commentService;

    private Comment comment;
    private CommentDtoRequest dtoRequest;

    @BeforeEach
    void setUp() {
        Long id = 1L;
        String content = "content";
        LocalDateTime date = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Author author = new Author(
                "authorName",
                date,
                date,
                new ArrayList<>());
        author.setId(id);
        News news = new News(
                "title",
                content,
                date,
                date,
                author,
                new ArrayList<>(),
                new ArrayList<>());
        news.setId(id);

        comment = new Comment(content, news, date, date);
        comment.setId(id);
        dtoRequest = new CommentDtoRequest(id, content, id);
    }

    @Test
    public void readAllCommentsTest() {
        //given
        given(commentRepository.readAll(any(), any())).willReturn(new Page<>(List.of(comment), 1, 1));

        //when
        SearchingRequest searchingRequest = new SearchingRequest(1, 10, "createDate:desc", null);
        PageDtoResponse<CommentDtoResponse> pageDtoResponse = commentService.readAll(searchingRequest);

        //then
        assertThat(pageDtoResponse.getEntityDtoList()).isEqualTo(commentDtoMapper.modelListToDtoList(List.of(comment), newsDtoMapper));
        assertThat(pageDtoResponse.getPageNumber()).isEqualTo(1);
        assertThat(pageDtoResponse.getEntitiesCount()).isEqualTo(1);
    }

    @Test
    void readExistentCommentByIdTest() {
        //given
        Long id = 1L;
        given(commentRepository.existById(id)).willReturn(true);
        given(commentRepository.readById(id)).willReturn(Optional.of(comment));

        //when
        CommentDtoResponse dtoResponse = commentService.readById(id);

        //then
        assertThat(dtoResponse).isEqualTo(commentDtoMapper.modelToDto(comment, newsDtoMapper));
    }

    @Test
    void readNonExistentCommentByIdThrowsNotFoundExceptionTest() {
        //given
        Long id = 1L;
        given(commentRepository.existById(id)).willReturn(false);

        //when

        //then
        assertThatThrownBy(() -> commentService.readById(id)).isInstanceOf(NotFoundException.class)
                .hasMessage(String.format(COMMENT_DOES_NOT_EXIST.getErrorMessage(), id));
    }

    @Test
    void createValidCommentTest() {
        //given

        //when
        commentService.create(dtoRequest);

        //then
        ArgumentCaptor<Comment> argumentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).create(argumentCaptor.capture());
        Comment capturedValue = argumentCaptor.getValue();
        assertThat(capturedValue).isEqualTo(comment);
    }

    @Test
    void createCommentThrowsValidationExceptionTest() {
        //given
        Long id = 1L;
        CommentDtoRequest invalidDtoRequest = new CommentDtoRequest(1L, "c", 1L);
        given(commentRepository.existById(id)).willReturn(true);

        //when

        //then
        assertThatThrownBy(() -> commentService.create(invalidDtoRequest)).isInstanceOf(ValidationException.class)
                .hasMessage(String.format(VALIDATION_EXCEPTION.getErrorMessage(), "[ConstraintViolation[message=Constraint StringField violated for value c]]"));
    }

    @Test
    void updateValidCommentTest() {
        //given
        Long id = 1L;
        given(commentRepository.existById(id)).willReturn(true);

        //when
        commentService.update(dtoRequest);

        // then
        ArgumentCaptor<Comment> argumentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).update(argumentCaptor.capture());
        Comment capturedValue = argumentCaptor.getValue();
        assertThat(capturedValue).isEqualTo(comment);
    }

    @Test
    void updateCommentThrowsCommentNotFoundExceptionTest() {
        //given
        Long id = 1L;
        given(commentRepository.existById(id)).willReturn(false);

        //when

        //then
        assertThatThrownBy(() -> commentService.update(dtoRequest)).isInstanceOf(NotFoundException.class)
                .hasMessage(String.format(COMMENT_DOES_NOT_EXIST.getErrorMessage(), id));
    }

    @Test
    void updateCommentThrowsValidationExceptionTest() {
        //given
        Long id = 1L;
        CommentDtoRequest invalidDtoRequest = new CommentDtoRequest(1L, "c", 1L);
        given(commentRepository.existById(id)).willReturn(true);

        //when

        //then
        assertThatThrownBy(() -> commentService.update(invalidDtoRequest)).isInstanceOf(ValidationException.class)
                .hasMessage(String.format(VALIDATION_EXCEPTION.getErrorMessage(), "[ConstraintViolation[message=Constraint StringField violated for value c]]"));
    }

    @Test
    void patchValidCommentTest() {
        //given
        Long id = 1L;
        CommentDtoRequest patchRequest = new CommentDtoRequest(id, null, id);
        given(commentRepository.existById(id)).willReturn(true);
        given(commentRepository.readById(id)).willReturn(Optional.of(comment));

        //when
        commentService.patch(patchRequest);

        // then
        ArgumentCaptor<Comment> argumentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).update(argumentCaptor.capture());
        Comment capturedValue = argumentCaptor.getValue();
        assertThat(capturedValue).isEqualTo(comment);
    }

    @Test
    void patchCommentThrowsCommentNotFoundExceptionTest() {
        //given
        Long id = 1L;
        CommentDtoRequest patchRequest = new CommentDtoRequest(id, null, id);
        given(commentRepository.existById(id)).willReturn(false);

        //when

        //then
        assertThatThrownBy(() -> commentService.patch(patchRequest)).isInstanceOf(NotFoundException.class)
                .hasMessage(String.format(COMMENT_DOES_NOT_EXIST.getErrorMessage(), id));

        //given
        id = null;
        CommentDtoRequest patchRequest2 = new CommentDtoRequest(id, null, id);

        //when

        //then
        assertThatThrownBy(() -> commentService.patch(patchRequest2)).isInstanceOf(NotFoundException.class)
                .hasMessage(String.format(COMMENT_DOES_NOT_EXIST.getErrorMessage(), id));
    }

    @Test
    void deleteExistentCommentByIdTest() {
        //given
        Long id = 1L;
        given(commentRepository.existById(id)).willReturn(true);
        given(commentRepository.deleteById(id)).willReturn(true);

        //when
        boolean isDeleted = commentService.deleteById(id);

        //then
        assertThat(isDeleted).isTrue();
    }

    @Test
    void deleteNonExistentCommentByIdTest() {
        //given
        Long id = 1L;
        given(commentRepository.existById(id)).willReturn(false);

        //when

        //then
        assertThatThrownBy(() -> commentService.deleteById(id)).isInstanceOf(NotFoundException.class)
                .hasMessage(String.format(COMMENT_DOES_NOT_EXIST.getErrorMessage(), id));
    }

    @Test
    public void readCommentsByValidNewsIdTest() {
        //given
        Long id = 1L;
        given(newsRepository.existById(id)).willReturn(true);
        given(commentRepository.readByNewsId(id)).willReturn(List.of(comment));

        //when
        List<CommentDtoResponse> dtoResponse = commentService.readByNewsId(id);

        //then
        assertThat(dtoResponse).isEqualTo(commentDtoMapper.modelListToDtoList(List.of(comment), newsDtoMapper));
    }

    @Test
    public void readCommentsByNewsIdThrowsNotFoundExceptionTest() {
        //given
        Long id = 1L;
        given(newsRepository.existById(id)).willReturn(false);

        //when

        //then
        assertThatThrownBy(() -> commentService.readByNewsId(id)).isInstanceOf(NotFoundException.class)
                .hasMessage(String.format(NEWS_DOES_NOT_EXIST.getErrorMessage(), id));
    }
}