package com.mjc.school.service.dto;

import com.mjc.school.service.annotation.IdField;
import com.mjc.school.service.annotation.NotNull;
import com.mjc.school.service.annotation.StringField;

public final class CommentDtoRequest {
    @IdField
    private Long id;

    @StringField(min = 5, max = 255)
    @NotNull
    private String content;

    @IdField
    private Long newsId;

    public CommentDtoRequest() {
    }

    public CommentDtoRequest(Long id, String content, Long newsId) {
        this.id = id;
        this.content = content;
        this.newsId = newsId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getNewsId() {
        return newsId;
    }

    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }
}
