package me.juhezi.eternal.model;

import io.realm.RealmObject;

/**
 * 文章实体类
 * - id: String
 * - title: 标题 String
 * - content: 内容 String
 * - author： 作者 ID [User] String
 * - createTime: 创建时间 String 时间戳
 * - updateTime: 更新时间 String 时间戳
 * - labelId: 标签 Id String
 * -
 **/
public class Article extends RealmObject {

    private String id;
    private String title;
    private String content;
    private String author;
    private String createTime;
    private String updateTime;
    private String labelId;

    public String getId() {
        return id;
    }

    public Article setId(String id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Article setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Article setContent(String content) {
        this.content = content;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public Article setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getCreateTime() {
        return createTime;
    }

    public Article setCreateTime(String createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public Article setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public String getLabelId() {
        return labelId;
    }

    public Article setLabelId(String labelId) {
        this.labelId = labelId;
        return this;
    }
}
