package com.medicare_service.model;

import java.io.Serializable;

public class Post implements Serializable {

    private String id = "";
    private String categoryId = "";
    private String categoryName = "";
    private String title = "";
    private String content = "";
    private String file = "";
    private String fileType = "";
    private long createAt = 0;
    private String createBy = "";
    private String doctorId = "";
    private String doctorName = "";

    public Post() {
    }

    public Post(String id, String categoryId, String categoryName,
                String title, String content, String file, String fileType, long createAt,
                String createBy, String doctorId, String doctorName
    ) {
        this.id = id;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.title = title;
        this.content = content;
        this.file = file;
        this.fileType = fileType;
        this.createAt = createAt;
        this.createBy = createBy;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
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

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

}
