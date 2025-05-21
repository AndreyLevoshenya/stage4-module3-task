package com.mjc.school.dto;

import com.mjc.school.annotation.IdField;
import com.mjc.school.annotation.NotNull;
import com.mjc.school.annotation.StringField;

public final class AuthorDtoRequest {
    @IdField
    private Long id;

    @StringField(min = 3, max = 15)
    @NotNull
    private String name;

    public AuthorDtoRequest() {
    }

    public AuthorDtoRequest(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
