package com.biursite.application.post.dto;

public class UpdatePostCommand {
    private final Long postId;
    private final String title;
    private final String content;
    private final Long currentUserId;

    public UpdatePostCommand(Long postId, String title, String content, Long currentUserId) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.currentUserId = currentUserId;
    }

    public Long getPostId() { return postId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public Long getCurrentUserId() { return currentUserId; }
}
