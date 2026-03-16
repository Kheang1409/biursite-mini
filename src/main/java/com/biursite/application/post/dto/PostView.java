package com.biursite.application.post.dto;

import java.time.Instant;

public class PostView {
    private Long id;
    private String title;
    private String content;
    private String authorUsername;
    private Long authorId;
    private Instant createdAt;
    private Instant updatedAt;
    private Boolean banned;
    private String banReason;

    public PostView() {}

    public PostView(Long id, String title, String content, String authorUsername, Long authorId, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorUsername = authorUsername;
        this.authorId = authorId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public PostView(Long id, String title, String content, String authorUsername, Long authorId, Instant createdAt, Instant updatedAt, Boolean banned, String banReason) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorUsername = authorUsername;
        this.authorId = authorId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.banned = banned;
        this.banReason = banReason;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthorUsername() { return authorUsername; }
    public Long getAuthorId() { return authorId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Boolean getBanned() { return banned; }
    public String getBanReason() { return banReason; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public void setBanned(Boolean banned) { this.banned = banned; }
    public void setBanReason(String banReason) { this.banReason = banReason; }
}
