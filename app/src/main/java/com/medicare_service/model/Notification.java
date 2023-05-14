package com.medicare_service.model;

public class Notification {

    private String id = "";
    private String title = "";
    private String content = "";
    private long createAt = 0;
    private String userId = "";

    public Notification() {
    }

    public Notification(String id, String title, String content, long createAt, String userId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createAt = createAt;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
