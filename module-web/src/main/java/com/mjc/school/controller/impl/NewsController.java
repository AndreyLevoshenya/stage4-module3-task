package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseController;
import com.mjc.school.service.AuthorService;
import com.mjc.school.service.CommentService;
import com.mjc.school.service.NewsService;
import com.mjc.school.service.TagService;
import com.mjc.school.service.dto.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.mjc.school.controller.RestConstants.NEWS_V1_API_PATH;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(value = NEWS_V1_API_PATH)
@Api(produces = MediaType.APPLICATION_JSON_VALUE, value = "Operations for creating, updating, retrieving and deleting news in the application")
public class NewsController implements BaseController<NewsDtoRequest, NewsDtoResponse, Long> {
    private final NewsService newsService;
    private final AuthorService authorService;
    private final TagService tagService;
    private final CommentService commentService;

    @Autowired
    public NewsController(NewsService newsService, AuthorService authorService, TagService tagService, CommentService commentService) {
        this.newsService = newsService;
        this.authorService = authorService;
        this.tagService = tagService;
        this.commentService = commentService;
    }

    @Override
    @ApiOperation(value = "View all news", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved all news"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")})
    @GetMapping
    @ResponseStatus(OK)
    @PreAuthorize("permitAll()")
    public ResponseEntity<PageDtoResponse<NewsDtoResponse>> readAll(
            @RequestParam(name = "page", required = false, defaultValue = "1") int pageNumber,
            @RequestParam(name = "size", required = false, defaultValue = "10") int pageSize,
            @RequestParam(name = "sort_by", required = false, defaultValue = "createDate:desc") String sortBy,
            @RequestParam(name = "search_by", required = false) String searchBy) {
        SearchingRequest searchingRequest = new SearchingRequest(pageNumber, pageSize, sortBy, searchBy);
        PageDtoResponse<NewsDtoResponse> pageDtoResponse = newsService.readAll(searchingRequest);
        for (NewsDtoResponse newsDtoResponse : pageDtoResponse.getEntityDtoList()) {
            setLinks(newsDtoResponse);
        }
        return new ResponseEntity<>(pageDtoResponse, OK);
    }

    @Override
    @ApiOperation(value = "Get news by id", response = NewsDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved news by id"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")})
    @GetMapping(value = "/{id}")
    @ResponseStatus(OK)
    @PreAuthorize("permitAll()")
    public ResponseEntity<NewsDtoResponse> readById(@PathVariable Long id) {
        NewsDtoResponse newsDtoResponse = newsService.readById(id);
        setLinks(newsDtoResponse);
        return new ResponseEntity<>(newsDtoResponse, OK);
    }

    @Override
    @ApiOperation(value = "Create news", response = NewsDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created a news"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")})
    @PostMapping
    @ResponseStatus(CREATED)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<NewsDtoResponse> create(@RequestBody NewsDtoRequest createRequest) {
        NewsDtoResponse newsDtoResponse = newsService.create(createRequest);
        setLinks(newsDtoResponse);
        return new ResponseEntity<>(newsDtoResponse, CREATED);
    }

    @Override
    @ApiOperation(value = "Update news information", response = NewsDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated news information"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")})
    @PutMapping(value = "/{id}")
    @ResponseStatus(OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<NewsDtoResponse> update(@PathVariable Long id, @RequestBody NewsDtoRequest updateRequest) {
        updateRequest.setId(id);
        NewsDtoResponse newsDtoResponse = newsService.update(updateRequest);
        setLinks(newsDtoResponse);
        return new ResponseEntity<>(newsDtoResponse, OK);
    }

    @Override
    @ApiOperation(value = "Patch news information", response = NewsDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully patched news information"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")})
    @PatchMapping(value = "/{id}")
    @ResponseStatus(OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<NewsDtoResponse> patch(@PathVariable Long id, @RequestBody NewsDtoRequest updateRequest) {
        updateRequest.setId(id);
        NewsDtoResponse newsDtoResponse = newsService.patch(updateRequest);
        setLinks(newsDtoResponse);
        return new ResponseEntity<>(newsDtoResponse, OK);
    }

    @Override
    @ApiOperation(value = "Deletes specific news with the supplied id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deletes the specific news"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")})
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteById(@PathVariable Long id) {
        newsService.deleteById(id);
    }

    @ApiOperation(value = "Get news by params", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved news by params"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")})
    @GetMapping(value = "/get")
    @ResponseStatus(OK)
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<NewsDtoResponse>> readByParams(@RequestBody ParametersDtoRequest parametersDtoRequest) {
        List<NewsDtoResponse> newsDtoResponseList = newsService.readByParams(parametersDtoRequest);
        for (NewsDtoResponse newsDtoResponse : newsDtoResponseList) {
            setLinks(newsDtoResponse);
        }
        return new ResponseEntity<>(newsDtoResponseList, OK);
    }

    @ApiOperation(value = "Get author by news id", response = AuthorDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved author by news id"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")})
    @GetMapping(value = "/{id}/authors")
    @ResponseStatus(OK)
    @PreAuthorize("permitAll()")
    public ResponseEntity<AuthorDtoResponse> readAuthorByNewsId(@PathVariable Long id) {
        AuthorDtoResponse authorDtoResponse = authorService.readByNewsId(id);
        Link selfRel = linkTo(AuthorController.class).slash(authorDtoResponse.getId()).withSelfRel();
        authorDtoResponse.add(selfRel);
        return new ResponseEntity<>(authorDtoResponse, OK);
    }

    @ApiOperation(value = "Get tags by news id", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved tags by news id"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")})
    @GetMapping(value = "/{id}/tags")
    @ResponseStatus(OK)
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<TagDtoResponse>> readTagsByNewsId(@PathVariable Long id) {
        List<TagDtoResponse> tagDtoResponseList = tagService.readByNewsId(id);
        for (TagDtoResponse tagDtoResponse : tagDtoResponseList) {
            Link selfRel = linkTo(TagController.class).slash(tagDtoResponse.getId()).withSelfRel();
            tagDtoResponse.add(selfRel);
        }
        return new ResponseEntity<>(tagDtoResponseList, OK);
    }

    @ApiOperation(value = "Get comments by news id", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved comments by news id"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")})
    @GetMapping(value = "/{id}/comments")
    @ResponseStatus(OK)
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<CommentDtoResponse>> readCommentsByNewsId(@PathVariable Long id) {
        List<CommentDtoResponse> commentDtoResponseList = commentService.readByNewsId(id);
        for (CommentDtoResponse commentDtoResponse : commentDtoResponseList) {
            Link selfRel = linkTo(CommentController.class).slash(commentDtoResponse.getId()).withSelfRel();
            commentDtoResponse.add(selfRel);
        }
        return new ResponseEntity<>(commentDtoResponseList, OK);
    }

    private static void setLinks(NewsDtoResponse newsDtoResponse) {
        Link selfRel = linkTo(NewsController.class).slash(newsDtoResponse.getId()).withSelfRel();
        newsDtoResponse.add(selfRel);
        Link authorRel = linkTo(AuthorController.class).slash(newsDtoResponse.getAuthorDtoResponse().getId()).withSelfRel();
        newsDtoResponse.getAuthorDtoResponse().add(authorRel);
        for (TagDtoResponse tagDtoResponse : newsDtoResponse.getTagDtoResponseList()) {
            Link tagRel = linkTo(TagController.class).slash(tagDtoResponse.getId()).withSelfRel();
            tagDtoResponse.add(tagRel);
        }
    }
}
