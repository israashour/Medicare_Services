package com.medicare_service.model;

public class GroupMessage {

    private String id = "";
    private String message = "";
    private String userName = "";
    private String userId = "";
    private long timestamp = 0;

    public GroupMessage() {
    }

    public GroupMessage(String id, String message, String userName,  String userId, long timestamp) {
        this.id = id;
        this.message = message;
        this.userName = userName;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
