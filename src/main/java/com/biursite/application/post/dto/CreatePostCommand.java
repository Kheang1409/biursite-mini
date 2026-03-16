package com.biursite.application.post.dto;

public class CreatePostCommand {
    private final String title;
    private final String content;
    private final Long authorId;

    public CreatePostCommand(String title, String content, Long authorId) {
        this.title = title;
        this.content = content;
        this.authorId = authorId;
    }

    public String getTitle() { return title; }
    public String getContent() { return content; }
    public Long getAuthorId() { return authorId; }
}
