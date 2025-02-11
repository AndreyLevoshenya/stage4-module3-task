package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseController;
import com.mjc.school.service.CommentService;
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

import static com.mjc.school.controller.RestConstants.COMMENTS_V1_API_PATH;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(value = COMMENTS_V1_API_PATH)
@Api(produces = MediaType.APPLICATION_JSON_VALUE, value = "Operations for creating, updating, retrieving and deleting comments in the application")
public class CommentController implements BaseController<CommentDtoRequest, CommentDtoResponse, Long> {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Override
    @ApiOperation(value = "View all comments", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved all comments"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")})
    @GetMapping
    @ResponseStatus(OK)
    @PreAuthorize("permitAll()")
    public ResponseEntity<PageDtoResponse<CommentDtoResponse>> readAll(
            @RequestParam(name = "page", required = false, defaultValue = "1") int pageNumber,
            @RequestParam(name = "size", required = false, defaultValue = "10") int pageSize,
            @RequestParam(name = "sort_by", required = false, defaultValue = "createDate:desc") String sortBy,
            @RequestParam(name = "search_by", required = false) String searchBy) {
        SearchingRequest searchingRequest = new SearchingRequest(pageNumber, pageSize, sortBy, searchBy);
        PageDtoResponse<CommentDtoResponse> pageDtoResponse = commentService.readAll(searchingRequest);
        for (CommentDtoResponse commentDtoResponse : pageDtoResponse.getEntityDtoList()) {
            setLinks(commentDtoResponse);
        }

        return new ResponseEntity<>(pageDtoResponse, OK);
    }

    @Override
    @ApiOperation(value = "Get comment by id", response = CommentDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved comment by id"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")})
    @GetMapping(value = "/{id}")
    @ResponseStatus(OK)
    @PreAuthorize("permitAll()")
    public ResponseEntity<CommentDtoResponse> readById(@PathVariable Long id) {
        CommentDtoResponse commentDtoResponse = commentService.readById(id);
        setLinks(commentDtoResponse);
        return new ResponseEntity<>(commentDtoResponse, OK);
    }

    @Override
    @ApiOperation(value = "Create comment", response = CommentDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created a comment"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")})
    @PostMapping
    @ResponseStatus(CREATED)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<CommentDtoResponse> create(@RequestBody CommentDtoRequest createRequest) {
        CommentDtoResponse commentDtoResponse = commentService.create(createRequest);
        setLinks(commentDtoResponse);
        return new ResponseEntity<>(commentDtoResponse, CREATED);
    }

    @Override
    @ApiOperation(value = "Update comment information", response = CommentDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated comment information"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")})
    @PutMapping(value = "/{id}")
    @ResponseStatus(OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CommentDtoResponse> update(@PathVariable Long id, @RequestBody CommentDtoRequest updateRequest) {
        updateRequest.setId(id);
        CommentDtoResponse commentDtoResponse = commentService.update(updateRequest);
        setLinks(commentDtoResponse);
        return new ResponseEntity<>(commentDtoResponse, OK);
    }

    @Override
    @ApiOperation(value = "Patch comment information", response = CommentDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully patched comment information"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")})
    @PatchMapping(value = "/{id}")
    @ResponseStatus(OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CommentDtoResponse> patch(@PathVariable Long id, @RequestBody CommentDtoRequest updateRequest) {
        updateRequest.setId(id);
        CommentDtoResponse commentDtoResponse = commentService.patch(updateRequest);
        setLinks(commentDtoResponse);
        return new ResponseEntity<>(commentDtoResponse, OK);
    }

    @Override
    @ApiOperation(value = "Deletes specific comment with the supplied id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deletes the specific comment"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")})
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteById(@PathVariable Long id) {
        commentService.deleteById(id);
    }

    private static void setLinks(CommentDtoResponse commentDtoResponse) {
        Link selfRel = linkTo(CommentController.class).slash(commentDtoResponse.getId()).withSelfRel();
        commentDtoResponse.add(selfRel);
        Link newsRel = linkTo(NewsController.class).slash(commentDtoResponse.getNewsDtoResponse().getId()).withSelfRel();
        commentDtoResponse.getNewsDtoResponse().add(newsRel);
        Link authorRel = linkTo(AuthorController.class).slash(commentDtoResponse.getNewsDtoResponse().getAuthorDtoResponse().getId()).withSelfRel();
        commentDtoResponse.getNewsDtoResponse().getAuthorDtoResponse().add(authorRel);
        for (TagDtoResponse tagDtoResponse : commentDtoResponse.getNewsDtoResponse().getTagDtoResponseList()) {
            Link tagRel = linkTo(TagController.class).slash(tagDtoResponse.getId()).withSelfRel();
            tagDtoResponse.add(tagRel);
        }
    }
}
