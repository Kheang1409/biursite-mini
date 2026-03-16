package com.biursite.application.user.dto;

import com.biursite.application.post.dto.PostView;

import java.time.Instant;
import java.util.List;

public class UserProfileView {
    private String username;
    private String bio;
    private Instant joinedAt;
    private String email;
    private String role;
    private Boolean deactivated;
    private List<PostView> posts;

    public UserProfileView() {}

    public UserProfileView(String username, String bio, Instant joinedAt, List<PostView> posts) {
        this.username = username;
        this.bio = bio;
        this.joinedAt = joinedAt;
        this.posts = posts;
    }

    public String getEmail() { return email; }
    public String getRole() { return role; }
    public Boolean getDeactivated() { return deactivated; }
    public Instant getCreatedAt() { return joinedAt; }

    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
    public void setDeactivated(Boolean deactivated) { this.deactivated = deactivated; }

    public String getUsername() { return username; }
    public String getBio() { return bio; }
    public Instant getJoinedAt() { return joinedAt; }
    public List<PostView> getPosts() { return posts; }

    public void setUsername(String username) { this.username = username; }
    public void setBio(String bio) { this.bio = bio; }
    public void setJoinedAt(Instant joinedAt) { this.joinedAt = joinedAt; }
    public void setPosts(List<PostView> posts) { this.posts = posts; }
}
