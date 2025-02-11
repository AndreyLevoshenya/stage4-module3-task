package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseController;
import com.mjc.school.service.TagService;
import com.mjc.school.service.dto.PageDtoResponse;
import com.mjc.school.service.dto.SearchingRequest;
import com.mjc.school.service.dto.TagDtoRequest;
import com.mjc.school.service.dto.TagDtoResponse;
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

import static com.mjc.school.controller.RestConstants.TAGS_V1_API_PATH;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(value = TAGS_V1_API_PATH)
@Api(produces = MediaType.APPLICATION_JSON_VALUE, value = "Operations for creating, updating, retrieving and deleting tags in the application")
public class TagController implements BaseController<TagDtoRequest, TagDtoResponse, Long> {
    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @Override
    @ApiOperation(value = "View all tags", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved all tags"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")})
    @GetMapping
    @ResponseStatus(OK)
    @PreAuthorize("permitAll()")
    public ResponseEntity<PageDtoResponse<TagDtoResponse>> readAll(
            @RequestParam(name = "page", required = false, defaultValue = "1") int pageNumber,
            @RequestParam(name = "size", required = false, defaultValue = "10") int pageSize,
            @RequestParam(name = "sort_by", required = false, defaultValue = "name:desc") String sortBy,
            @RequestParam(name = "search_by", required = false) String searchBy) {
        SearchingRequest searchingRequest = new SearchingRequest(pageNumber, pageSize, sortBy, searchBy);
        PageDtoResponse<TagDtoResponse> pageDtoResponse = tagService.readAll(searchingRequest);
        for (TagDtoResponse tagDtoResponse : pageDtoResponse.getEntityDtoList()) {
            Link selfRel = linkTo(TagController.class).slash(tagDtoResponse.getId()).withSelfRel();
            tagDtoResponse.add(selfRel);
        }

        return new ResponseEntity<>(pageDtoResponse, OK);
    }

    @Override
    @ApiOperation(value = "Get tag by id", response = TagDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved tag by id"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")})
    @GetMapping(value = "/{id}")
    @ResponseStatus(OK)
    @PreAuthorize("permitAll()")
    public ResponseEntity<TagDtoResponse> readById(@PathVariable Long id) {
        TagDtoResponse tagDtoResponse = tagService.readById(id);
        Link selfRel = linkTo(TagController.class).slash(tagDtoResponse.getId()).withSelfRel();
        tagDtoResponse.add(selfRel);
        return new ResponseEntity<>(tagDtoResponse, OK);
    }

    @Override
    @ApiOperation(value = "Create tag", response = TagDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created a tag"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")})
    @PostMapping
    @ResponseStatus(CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<TagDtoResponse> create(@RequestBody TagDtoRequest createRequest) {
        TagDtoResponse tagDtoResponse = tagService.create(createRequest);
        Link selfRel = linkTo(TagController.class).slash(tagDtoResponse.getId()).withSelfRel();
        tagDtoResponse.add(selfRel);
        return new ResponseEntity<>(tagDtoResponse, CREATED);
    }

    @Override
    @ApiOperation(value = "Update tag information", response = TagDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated tag information"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")})
    @PutMapping(value = "/{id}")
    @ResponseStatus(OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<TagDtoResponse> update(@PathVariable Long id, @RequestBody TagDtoRequest updateRequest) {
        updateRequest.setId(id);
        TagDtoResponse tagDtoResponse = tagService.update(updateRequest);
        Link selfRel = linkTo(TagController.class).slash(tagDtoResponse.getId()).withSelfRel();
        tagDtoResponse.add(selfRel);
        return new ResponseEntity<>(tagDtoResponse, OK);
    }

    @Override
    @ApiOperation(value = "Patch tag information", response = TagDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully patched tag information"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")})
    @PatchMapping(value = "/{id}")
    @ResponseStatus(OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<TagDtoResponse> patch(@PathVariable Long id, @RequestBody TagDtoRequest updateRequest) {
        updateRequest.setId(id);
        TagDtoResponse tagDtoResponse = tagService.patch(updateRequest);
        Link selfRel = linkTo(TagController.class).slash(tagDtoResponse.getId()).withSelfRel();
        tagDtoResponse.add(selfRel);
        return new ResponseEntity<>(tagDtoResponse, OK);
    }

    @Override
    @ApiOperation(value = "Deletes specific tag with the supplied id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deletes the specific tag"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")})
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteById(@PathVariable Long id) {
        tagService.deleteById(id);
    }
}
