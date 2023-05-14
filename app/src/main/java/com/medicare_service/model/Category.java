package com.medicare_service.model;

import java.io.Serializable;
import java.util.HashMap;

public class Category implements Serializable {

    private String id = "";
    private String title = "";
    private String description = "";
    private String topic = "";
    private long createAt = 0;
    private String createBy = "";
    private HashMap<String, Object> users;


    public Category() {
    }

    public Category(String id, String title, String description, String topic, long createAt, String createBy) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.topic = topic;
        this.createAt = createAt;
        this.createBy = createBy;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public HashMap<String, Object> getUsers() {
        return users;
    }

    public void setUsers(HashMap<String, Object> users) {
        this.users = users;
    }
}
