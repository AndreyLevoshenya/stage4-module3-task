package com.mjc.school.service.implementation;

import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.TagRepository;
import com.mjc.school.repository.filter.Page;
import com.mjc.school.repository.model.Tag;
import com.mjc.school.service.dto.PageDtoResponse;
import com.mjc.school.service.dto.SearchingRequest;
import com.mjc.school.service.dto.TagDtoRequest;
import com.mjc.school.service.dto.TagDtoResponse;
import com.mjc.school.service.exceptions.NotFoundException;
import com.mjc.school.service.exceptions.ValidationException;
import com.mjc.school.service.implementation.config.JPAConfig;
import com.mjc.school.service.implementation.config.TestConfig;
import com.mjc.school.service.mapper.TagDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static com.mjc.school.service.exceptions.ExceptionErrorCodes.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {TestConfig.class, JPAConfig.class})
class TagServiceImplTest {
    @MockBean
    private TagRepository tagRepository;
    @MockBean
    private NewsRepository newsRepository;

    @Autowired
    private TagDtoMapper tagDtoMapper;
    @Autowired
    private TagServiceImpl tagService;

    private Tag tag;
    private TagDtoRequest dtoRequest;

    @BeforeEach
    void setUp() {
        Long id = 1L;
        String name = "tag1";
        tag = new Tag(name);
        tag.setId(id);
        dtoRequest = new TagDtoRequest(id, name);
    }

    @Test
    public void readAllTagsTest() {
        //given
        given(tagRepository.readAll(any(), any())).willReturn(new Page<>(List.of(tag), 1, 1));

        //when
        SearchingRequest searchingRequest = new SearchingRequest(1, 10, "name:desc", null);
        PageDtoResponse<TagDtoResponse> pageDtoResponse = tagService.readAll(searchingRequest);

        //then
        assertThat(pageDtoResponse.getEntityDtoList()).isEqualTo(tagDtoMapper.modelListToDtoList(List.of(tag)));
        assertThat(pageDtoResponse.getPageNumber()).isEqualTo(1);
        assertThat(pageDtoResponse.getEntitiesCount()).isEqualTo(1);
    }

    @Test
    void readExistentTagByIdTest() {
        //given
        Long id = 1L;
        given(tagRepository.existById(id)).willReturn(true);
        given(tagRepository.readById(id)).willReturn(Optional.of(tag));

        //when
        TagDtoResponse dtoResponse = tagService.readById(id);

        //then
        assertThat(dtoResponse).isEqualTo(tagDtoMapper.modelToDto(tag));
    }

    @Test
    void readNonExistentTagByIdThrowsNotFoundExceptionTest() {
        //given
        Long id = 1L;
        given(tagRepository.existById(id)).willReturn(false);

        //when

        //then
        assertThatThrownBy(() -> tagService.readById(id)).isInstanceOf(NotFoundException.class)
                .hasMessage(String.format(TAG_DOES_NOT_EXIST.getErrorMessage(), id));
    }

    @Test
    void createValidTagTest() {
        //given

        //when
        tagService.create(dtoRequest);

        //then
        ArgumentCaptor<Tag> argumentCaptor = ArgumentCaptor.forClass(Tag.class);
        verify(tagRepository).create(argumentCaptor.capture());
        Tag capturedValue = argumentCaptor.getValue();
        assertThat(capturedValue).isEqualTo(tag);
    }

    @Test
    void createTagThrowsValidationExceptionTest() {
        //given
        Long id = 1L;
        TagDtoRequest invalidDtoRequest = new TagDtoRequest(1L, "t");
        given(tagRepository.existById(id)).willReturn(true);

        //when

        //then
        assertThatThrownBy(() -> tagService.create(invalidDtoRequest)).isInstanceOf(ValidationException.class)
                .hasMessage(String.format(VALIDATION_EXCEPTION.getErrorMessage(), "[ConstraintViolation[message=Constraint StringField violated for value t]]"));
    }

    @Test
    void updateValidTagTest() {
        //given
        Long id = 1L;
        given(tagRepository.existById(id)).willReturn(true);

        //when
        tagService.update(dtoRequest);

        // then
        ArgumentCaptor<Tag> argumentCaptor = ArgumentCaptor.forClass(Tag.class);
        verify(tagRepository).update(argumentCaptor.capture());
        Tag capturedValue = argumentCaptor.getValue();
        assertThat(capturedValue).isEqualTo(tag);
    }

    @Test
    void updateTagThrowsTagNotFoundExceptionTest() {
        //given
        Long id = 1L;
        given(tagRepository.existById(id)).willReturn(false);

        //when

        //then
        assertThatThrownBy(() -> tagService.update(dtoRequest)).isInstanceOf(NotFoundException.class)
                .hasMessage(String.format(TAG_DOES_NOT_EXIST.getErrorMessage(), id));
    }

    @Test
    void updateTagThrowsValidationExceptionTest() {
        //given
        Long id = 1L;
        TagDtoRequest invalidDtoRequest = new TagDtoRequest(1L, "t");
        given(tagRepository.existById(id)).willReturn(true);

        //when

        //then
        assertThatThrownBy(() -> tagService.update(invalidDtoRequest)).isInstanceOf(ValidationException.class)
                .hasMessage(String.format(VALIDATION_EXCEPTION.getErrorMessage(), "[ConstraintViolation[message=Constraint StringField violated for value t]]"));
    }

    @Test
    void patchValidTagTest() {
        //given
        Long id = 1L;
        TagDtoRequest patchRequest = new TagDtoRequest(id, null);
        given(tagRepository.existById(id)).willReturn(true);
        given(tagRepository.readById(id)).willReturn(Optional.of(tag));

        //when
        tagService.patch(patchRequest);

        // then
        ArgumentCaptor<Tag> argumentCaptor = ArgumentCaptor.forClass(Tag.class);
        verify(tagRepository).update(argumentCaptor.capture());
        Tag capturedValue = argumentCaptor.getValue();
        assertThat(capturedValue).isEqualTo(tag);
    }

    @Test
    void patchTagThrowsTagNotFoundExceptionTest() {
        //given
        Long id = 1L;
        TagDtoRequest patchRequest = new TagDtoRequest(id, null);
        given(tagRepository.existById(id)).willReturn(false);

        //when

        //then
        assertThatThrownBy(() -> tagService.patch(patchRequest)).isInstanceOf(NotFoundException.class)
                .hasMessage(String.format(TAG_DOES_NOT_EXIST.getErrorMessage(), id));

        //given
        id = null;
        TagDtoRequest patchRequest2 = new TagDtoRequest(id, null);

        //when

        //then
        assertThatThrownBy(() -> tagService.patch(patchRequest2)).isInstanceOf(NotFoundException.class)
                .hasMessage(String.format(TAG_DOES_NOT_EXIST.getErrorMessage(), id));
    }

    @Test
    void deleteExistentTagByIdTest() {
        //given
        Long id = 1L;
        given(tagRepository.existById(id)).willReturn(true);
        given(tagRepository.deleteById(id)).willReturn(true);

        //when
        boolean isDeleted = tagService.deleteById(id);

        //then
        assertThat(isDeleted).isTrue();
    }

    @Test
    void deleteNonExistentTagByIdTest() {
        //given
        Long id = 1L;
        given(tagRepository.existById(id)).willReturn(false);

        //when

        //then
        assertThatThrownBy(() -> tagService.deleteById(id)).isInstanceOf(NotFoundException.class)
                .hasMessage(String.format(TAG_DOES_NOT_EXIST.getErrorMessage(), id));
    }

    @Test
    public void readTagsByValidNewsIdTest() {
        //given
        Long id = 1L;
        given(newsRepository.existById(id)).willReturn(true);
        given(tagRepository.readByNewsId(id)).willReturn(List.of(tag));

        //when
        List<TagDtoResponse> dtoResponse = tagService.readByNewsId(id);

        //then
        assertThat(dtoResponse).isEqualTo(tagDtoMapper.modelListToDtoList(List.of(tag)));
    }

    @Test
    public void readTagsByNewsIdThrowsNotFoundExceptionTest() {
        //given
        Long id = 1L;
        given(newsRepository.existById(id)).willReturn(false);

        //when

        //then
        assertThatThrownBy(() -> tagService.readByNewsId(id)).isInstanceOf(NotFoundException.class)
                .hasMessage(String.format(NEWS_DOES_NOT_EXIST.getErrorMessage(), id));
    }
}