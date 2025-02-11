package com.mjc.school.service.implementation;

import com.mjc.school.repository.AuthorRepository;
import com.mjc.school.repository.filter.Page;
import com.mjc.school.repository.model.Author;
import com.mjc.school.service.dto.AuthorDtoRequest;
import com.mjc.school.service.dto.AuthorDtoResponse;
import com.mjc.school.service.dto.PageDtoResponse;
import com.mjc.school.service.dto.SearchingRequest;
import com.mjc.school.service.exceptions.NotFoundException;
import com.mjc.school.service.exceptions.ValidationException;
import com.mjc.school.service.implementation.config.JPAConfig;
import com.mjc.school.service.implementation.config.TestConfig;
import com.mjc.school.service.mapper.AuthorDtoMapper;
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

import static com.mjc.school.service.exceptions.ExceptionErrorCodes.AUTHOR_DOES_NOT_EXIST;
import static com.mjc.school.service.exceptions.ExceptionErrorCodes.VALIDATION_EXCEPTION;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {TestConfig.class, JPAConfig.class})
class AuthorServiceImplTest {
    @MockBean
    private AuthorRepository authorRepository;

    @Autowired
    private AuthorDtoMapper authorDtoMapper;

    @Autowired
    private AuthorServiceImpl authorService;

    Author author;
    AuthorDtoRequest dtoRequest;

    @BeforeEach
    void setUp() {
        Long id = 1L;
        LocalDateTime date = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        String name = "authorName";
        author = new Author(
                name,
                date,
                date,
                new ArrayList<>());
        author.setId(id);
        dtoRequest = new AuthorDtoRequest(id, name);
    }

    @Test
    public void readAllAuthorsTest() {
        //given
        given(authorRepository.readAll(any(), any())).willReturn(new Page<>(List.of(author), 1, 1));

        //when
        SearchingRequest searchingRequest = new SearchingRequest(1, 10, "name:desc", null);
        PageDtoResponse<AuthorDtoResponse> pageDtoResponse = authorService.readAll(searchingRequest);

        //then
        assertThat(pageDtoResponse.getEntityDtoList()).isEqualTo(authorDtoMapper.modelListToDtoList(List.of(author)));
        assertThat(pageDtoResponse.getPageNumber()).isEqualTo(1);
        assertThat(pageDtoResponse.getEntitiesCount()).isEqualTo(1);
    }

    @Test
    void readExistentAuthorByIdTest() {
        //given
        Long id = 1L;
        given(authorRepository.existById(id)).willReturn(true);
        given(authorRepository.readById(id)).willReturn(Optional.of(author));

        //when
        AuthorDtoResponse dtoResponse = authorService.readById(id);

        //then
        assertThat(dtoResponse).isEqualTo(authorDtoMapper.modelToDto(author));
    }

    @Test
    void readNonExistentAuthorByIdThrowsNotFoundExceptionTest() {
        //given
        Long id = 1L;
        given(authorRepository.existById(id)).willReturn(false);

        //when

        //then
        assertThatThrownBy(() -> authorService.readById(id)).isInstanceOf(NotFoundException.class)
                .hasMessage(String.format(AUTHOR_DOES_NOT_EXIST.getErrorMessage(), id));
    }

    @Test
    void createValidAuthorTest() {
        //given

        //when
        authorService.create(dtoRequest);

        //then
        ArgumentCaptor<Author> argumentCaptor = ArgumentCaptor.forClass(Author.class);
        verify(authorRepository).create(argumentCaptor.capture());
        Author capturedValue = argumentCaptor.getValue();
        assertThat(capturedValue).isEqualTo(author);
    }

    @Test
    void createAuthorThrowsValidationExceptionTest() {
        //given
        Long id = 1L;
        AuthorDtoRequest invalidDtoRequest = new AuthorDtoRequest(1L, "a");
        given(authorRepository.existById(id)).willReturn(true);

        //when

        //then
        assertThatThrownBy(() -> authorService.create(invalidDtoRequest)).isInstanceOf(ValidationException.class)
                .hasMessage(String.format(VALIDATION_EXCEPTION.getErrorMessage(), "[ConstraintViolation[message=Constraint StringField violated for value a]]"));
    }

    @Test
    void updateValidAuthorTest() {
        //given
        Long id = 1L;
        given(authorRepository.existById(id)).willReturn(true);

        //when
        authorService.update(dtoRequest);

        // then
        ArgumentCaptor<Author> argumentCaptor = ArgumentCaptor.forClass(Author.class);
        verify(authorRepository).update(argumentCaptor.capture());
        Author capturedValue = argumentCaptor.getValue();
        assertThat(capturedValue).isEqualTo(author);
    }

    @Test
    void updateAuthorThrowsAuthorNotFoundExceptionTest() {
        //given
        Long id = 1L;
        given(authorRepository.existById(id)).willReturn(false);

        //when

        //then
        assertThatThrownBy(() -> authorService.update(dtoRequest)).isInstanceOf(NotFoundException.class)
                .hasMessage(String.format(AUTHOR_DOES_NOT_EXIST.getErrorMessage(), id));
    }

    @Test
    void updateAuthorThrowsValidationExceptionTest() {
        //given
        Long id = 1L;
        AuthorDtoRequest invalidDtoRequest = new AuthorDtoRequest(1L, "a");
        given(authorRepository.existById(id)).willReturn(true);

        //when

        //then
        assertThatThrownBy(() -> authorService.update(invalidDtoRequest)).isInstanceOf(ValidationException.class)
                .hasMessage(String.format(VALIDATION_EXCEPTION.getErrorMessage(), "[ConstraintViolation[message=Constraint StringField violated for value a]]"));
    }

    @Test
    void patchValidAuthorTest() {
        //given
        Long id = 1L;
        AuthorDtoRequest patchRequest = new AuthorDtoRequest(id, null);
        given(authorRepository.existById(id)).willReturn(true);
        given(authorRepository.readById(id)).willReturn(Optional.of(author));

        //when
        authorService.patch(patchRequest);

        // then
        ArgumentCaptor<Author> argumentCaptor = ArgumentCaptor.forClass(Author.class);
        verify(authorRepository).update(argumentCaptor.capture());
        Author capturedValue = argumentCaptor.getValue();
        assertThat(capturedValue).isEqualTo(author);
    }

    @Test
    void patchAuthorThrowsAuthorNotFoundExceptionTest() {
        //given
        Long id = 1L;
        AuthorDtoRequest patchRequest = new AuthorDtoRequest(id, null);
        given(authorRepository.existById(id)).willReturn(false);

        //when

        //then
        assertThatThrownBy(() -> authorService.patch(patchRequest)).isInstanceOf(NotFoundException.class)
                .hasMessage(String.format(AUTHOR_DOES_NOT_EXIST.getErrorMessage(), id));

        //given
        id = null;
        AuthorDtoRequest patchRequest2 = new AuthorDtoRequest(id, null);

        //when

        //then
        assertThatThrownBy(() -> authorService.patch(patchRequest2)).isInstanceOf(NotFoundException.class)
                .hasMessage(String.format(AUTHOR_DOES_NOT_EXIST.getErrorMessage(), id));
    }

    @Test
    void deleteExistentAuthorByIdTest() {
        //given
        Long id = 1L;
        given(authorRepository.existById(id)).willReturn(true);
        given(authorRepository.deleteById(id)).willReturn(true);

        //when
        boolean isDeleted = authorService.deleteById(id);

        //then
        assertThat(isDeleted).isTrue();
    }

    @Test
    void deleteNonExistentAuthorByIdTest() {
        //given
        Long id = 1L;
        given(authorRepository.existById(id)).willReturn(false);

        //when

        //then
        assertThatThrownBy(() -> authorService.deleteById(id)).isInstanceOf(NotFoundException.class)
                .hasMessage(String.format(AUTHOR_DOES_NOT_EXIST.getErrorMessage(), id));
    }

    @Test
    public void readAuthorByValidNewsIdTest() {
        //given
        Long id = 1L;
        given(authorRepository.readByNewsId(id)).willReturn(Optional.of(author));

        //when
        AuthorDtoResponse dtoResponse = authorService.readByNewsId(id);

        //then
        assertThat(dtoResponse).isEqualTo(authorDtoMapper.modelToDto(author));
    }

    @Test
    public void readAuthorByNewsIdThrowsNotFoundExceptionTest() {
        //given
        Long id = 1L;
        given(authorRepository.readByNewsId(id)).willReturn(Optional.empty());

        //when

        //then
        assertThatThrownBy(() -> authorService.readByNewsId(id)).isInstanceOf(NotFoundException.class)
                .hasMessage(String.format(AUTHOR_DOES_NOT_EXIST.getErrorMessage(), id));
    }
}