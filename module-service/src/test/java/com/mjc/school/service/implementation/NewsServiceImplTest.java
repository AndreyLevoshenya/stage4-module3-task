package com.mjc.school.service.implementation;

import com.mjc.school.repository.AuthorRepository;
import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.TagRepository;
import com.mjc.school.repository.filter.Page;
import com.mjc.school.repository.model.Author;
import com.mjc.school.repository.model.News;
import com.mjc.school.repository.model.Tag;
import com.mjc.school.service.dto.*;
import com.mjc.school.service.exceptions.NotFoundException;
import com.mjc.school.service.exceptions.ValidationException;
import com.mjc.school.service.implementation.config.JPAConfig;
import com.mjc.school.service.implementation.config.TestConfig;
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
class NewsServiceImplTest {
    @MockBean
    private NewsRepository newsRepository;
    @MockBean
    private AuthorRepository authorRepository;
    @MockBean
    private TagRepository tagRepository;

    @Autowired
    private NewsDtoMapper newsDtoMapper;
    @Autowired
    private NewsServiceImpl newsService;

    private News news;
    private NewsDtoRequest dtoRequest;

    @BeforeEach
    void setUp() {
        LocalDateTime date = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        Long id = 1L;
        String title = "title";
        String content = "content";
        Tag tag1 = new Tag("tag1");
        Tag tag2 = new Tag("tag2");
        tag1.setId(id);
        tag2.setId(2L);
        List<Tag> tagList = List.of(tag1, tag2);

        dtoRequest = new NewsDtoRequest(
                id,
                title,
                content,
                id,
                List.of(id, 2L)
        );

        Author author = new Author(
                "authorName",
                date,
                date,
                new ArrayList<>());
        author.setId(id);
        news = new News(
                title,
                content,
                date,
                date,
                author,
                tagList,
                new ArrayList<>());
        news.setId(id);
    }

    @Test
    void readAllNewsTest() {
        //given
        given(newsRepository.readAll(any(), any())).willReturn(new Page<>(List.of(news), 1, 1));

        //when
        SearchingRequest searchingRequest = new SearchingRequest(1, 10, "createDate:desc", null);
        PageDtoResponse<NewsDtoResponse> pageDtoResponse = newsService.readAll(searchingRequest);

        //then
        assertThat(pageDtoResponse.getEntityDtoList()).isEqualTo(newsDtoMapper.modelListToDtoList(List.of(news)));
        assertThat(pageDtoResponse.getPageNumber()).isEqualTo(1);
        assertThat(pageDtoResponse.getEntitiesCount()).isEqualTo(1);
    }

    @Test
    void readExistentNewsByIdTest() {
        //given
        Long id = 1L;
        given(newsRepository.existById(id)).willReturn(true);
        given(newsRepository.readById(id)).willReturn(Optional.of(news));

        //when
        NewsDtoResponse dtoResponse = newsService.readById(id);

        //then
        assertThat(dtoResponse).isEqualTo(newsDtoMapper.modelToDto(news));
    }

    @Test
    void readNonExistentNewsByIdThrowsNotFoundExceptionTest() {
        //given
        Long id = 1L;
        given(newsRepository.existById(id)).willReturn(false);

        //when

        //then
        assertThatThrownBy(() -> newsService.readById(id)).isInstanceOf(NotFoundException.class)
                .hasMessage(String.format(NEWS_DOES_NOT_EXIST.getErrorMessage(), id));
    }

    @Test
    void createValidNewsTest() {
        //given
        Long id = 1L;
        given(authorRepository.existById(id)).willReturn(true);

        //when
        newsService.create(dtoRequest);

        //then
        ArgumentCaptor<News> argumentCaptor = ArgumentCaptor.forClass(News.class);
        verify(newsRepository).create(argumentCaptor.capture());
        News capturedValue = argumentCaptor.getValue();
        assertThat(capturedValue).isEqualTo(news);
    }

    @Test
    void createNewsThrowsNotFoundExceptionTest() {
        //given
        Long id = 1L;
        given(authorRepository.existById(id)).willReturn(false);

        //when

        //then
        assertThatThrownBy(() -> newsService.create(dtoRequest)).isInstanceOf(NotFoundException.class)
                .hasMessage(String.format(AUTHOR_DOES_NOT_EXIST.getErrorMessage(), id));
    }

    @Test
    void createNewsThrowsValidationExceptionTest() {
        //given
        Long id = 1L;
        NewsDtoRequest invalidDtoRequest = new NewsDtoRequest(1L, "n", "content", id, new ArrayList<>());
        given(authorRepository.existById(id)).willReturn(true);

        //when

        //then
        assertThatThrownBy(() -> newsService.create(invalidDtoRequest)).isInstanceOf(ValidationException.class)
                .hasMessage(String.format(VALIDATION_EXCEPTION.getErrorMessage(), "[ConstraintViolation[message=Constraint StringField violated for value n]]"));
    }

    @Test
    void updateValidNewsTest() {
        //given
        Long id = 1L;
        given(newsRepository.existById(id)).willReturn(true);
        given(authorRepository.existById(id)).willReturn(true);
        given(tagRepository.existById(id)).willReturn(true);
        given(tagRepository.existById(2L)).willReturn(true);

        //when
        newsService.update(dtoRequest);

        // then
        ArgumentCaptor<News> argumentCaptor = ArgumentCaptor.forClass(News.class);
        verify(newsRepository).update(argumentCaptor.capture());
        News capturedValue = argumentCaptor.getValue();
        assertThat(capturedValue).isEqualTo(news);
    }

    @Test
    void updateNewsThrowsNewsNotFoundExceptionTest() {
        //given
        Long id = 1L;
        given(newsRepository.existById(id)).willReturn(false);

        //when

        //then
        assertThatThrownBy(() -> newsService.update(dtoRequest)).isInstanceOf(NotFoundException.class)
                .hasMessage(String.format(NEWS_DOES_NOT_EXIST.getErrorMessage(), id));
    }

    @Test
    void updateNewsThrowsAuthorNotFoundExceptionTest() {
        //given
        Long id = 1L;
        given(newsRepository.existById(id)).willReturn(true);
        given(authorRepository.existById(id)).willReturn(false);

        //when

        //then
        assertThatThrownBy(() -> newsService.update(dtoRequest)).isInstanceOf(NotFoundException.class)
                .hasMessage(String.format(AUTHOR_DOES_NOT_EXIST.getErrorMessage(), id));
    }

    @Test
    void updateNewsThrowsTagNotFoundExceptionTest() {
        //given
        Long id = 1L;
        given(newsRepository.existById(id)).willReturn(true);
        given(authorRepository.existById(id)).willReturn(true);
        given(tagRepository.existById(id)).willReturn(false);

        //when

        //then
        assertThatThrownBy(() -> newsService.update(dtoRequest)).isInstanceOf(NotFoundException.class)
                .hasMessage(String.format(TAG_DOES_NOT_EXIST.getErrorMessage(), id));
    }

    @Test
    void updateNewsThrowsValidationExceptionTest() {
        //given
        Long id = 1L;
        NewsDtoRequest invalidDtoRequest = new NewsDtoRequest(1L, "n", "content", id, new ArrayList<>());
        given(newsRepository.existById(id)).willReturn(true);
        given(authorRepository.existById(id)).willReturn(true);

        //when

        //then
        assertThatThrownBy(() -> newsService.update(invalidDtoRequest)).isInstanceOf(ValidationException.class)
                .hasMessage(String.format(VALIDATION_EXCEPTION.getErrorMessage(), "[ConstraintViolation[message=Constraint StringField violated for value n]]"));
    }

    @Test
    void patchValidNewsTest() {
        //given
        Long id = 1L;
        NewsDtoRequest patchRequest = new NewsDtoRequest(id, null, null, id, List.of(id));
        given(newsRepository.existById(id)).willReturn(true);
        given(authorRepository.existById(id)).willReturn(true);
        given(tagRepository.existById(id)).willReturn(true);
        given(newsRepository.readById(id)).willReturn(Optional.of(news));

        //when
        newsService.patch(patchRequest);

        // then
        ArgumentCaptor<News> argumentCaptor = ArgumentCaptor.forClass(News.class);
        verify(newsRepository).update(argumentCaptor.capture());
        News capturedValue = argumentCaptor.getValue();
        assertThat(capturedValue).isEqualTo(news);
    }

    @Test
    void patchValidNewsWithNullAuthorId() {
        //given
        Long id = 1L;
        NewsDtoRequest patchRequest = new NewsDtoRequest(id, null, null, null, null);
        given(newsRepository.existById(id)).willReturn(true);
        given(authorRepository.existById(id)).willReturn(true);
        given(tagRepository.existById(id)).willReturn(true);
        given(tagRepository.existById(2L)).willReturn(true);
        given(newsRepository.readById(id)).willReturn(Optional.of(news));

        //when
        newsService.patch(patchRequest);

        //then
        ArgumentCaptor<News> argumentCaptor = ArgumentCaptor.forClass(News.class);
        verify(newsRepository).update(argumentCaptor.capture());
        News capturedValue = argumentCaptor.getValue();
        assertThat(capturedValue).isEqualTo(news);
    }

    @Test
    void patchNewsThrowsNewsNotFoundExceptionTest() {
        //given
        Long id = 1L;
        NewsDtoRequest patchRequest = new NewsDtoRequest(id, null, null, id, null);
        given(newsRepository.existById(id)).willReturn(false);

        //when

        //then
        assertThatThrownBy(() -> newsService.patch(patchRequest)).isInstanceOf(NotFoundException.class)
                .hasMessage(String.format(NEWS_DOES_NOT_EXIST.getErrorMessage(), id));

        //given
        id = null;
        NewsDtoRequest patchRequest2 = new NewsDtoRequest(id, null, null, id, null);

        //when

        //then
        assertThatThrownBy(() -> newsService.patch(patchRequest2)).isInstanceOf(NotFoundException.class)
                .hasMessage(String.format(NEWS_DOES_NOT_EXIST.getErrorMessage(), id));
    }

    @Test
    void patchNewsThrowsAuthorNotFoundExceptionTest() {
        //given
        Long id = 1L;
        NewsDtoRequest patchRequest = new NewsDtoRequest(id, null, null, id, null);
        given(newsRepository.existById(id)).willReturn(true);
        given(newsRepository.readById(id)).willReturn(Optional.of(news));
        given(authorRepository.existById(id)).willReturn(false);

        //when

        //then
        assertThatThrownBy(() -> newsService.patch(patchRequest)).isInstanceOf(NotFoundException.class)
                .hasMessage(String.format(AUTHOR_DOES_NOT_EXIST.getErrorMessage(), id));
    }

    @Test
    void patchNewsThrowsTagNotFoundExceptionTest() {
        //given
        Long id = 1L;
        NewsDtoRequest patchRequest = new NewsDtoRequest(id, null, null, id, List.of(id));
        given(newsRepository.existById(id)).willReturn(true);
        given(newsRepository.readById(id)).willReturn(Optional.of(news));
        given(authorRepository.existById(id)).willReturn(true);
        given(tagRepository.existById(id)).willReturn(false);

        //when

        //then
        assertThatThrownBy(() -> newsService.patch(patchRequest)).isInstanceOf(NotFoundException.class)
                .hasMessage(String.format(TAG_DOES_NOT_EXIST.getErrorMessage(), id));
    }

    @Test
    void deleteExistentNewsByIdTest() {
        //given
        Long id = 1L;
        given(newsRepository.existById(id)).willReturn(true);
        given(newsRepository.deleteById(id)).willReturn(true);

        //when
        boolean isDeleted = newsService.deleteById(id);

        //then
        assertThat(isDeleted).isTrue();
    }

    @Test
    void deleteNonExistentNewsByIdTest() {
        //given
        Long id = 1L;
        given(newsRepository.existById(id)).willReturn(false);

        //when

        //then
        assertThatThrownBy(() -> newsService.deleteById(id)).isInstanceOf(NotFoundException.class)
                .hasMessage(String.format(NEWS_DOES_NOT_EXIST.getErrorMessage(), id));
    }

    @Test
    void readByParamsTest() {
        //given
        ParametersDtoRequest dtoRequest = new ParametersDtoRequest(
                "title",
                "content",
                "name",
                List.of(1),
                List.of("tag1"));
        given(newsRepository.readByParams(any())).willReturn(List.of(news));

        //when
        List<NewsDtoResponse> dtoResponses = newsService.readByParams(dtoRequest);

        //then
        assertThat(dtoResponses).isEqualTo(newsDtoMapper.modelListToDtoList(List.of(news)));
    }
}