package com.imooc.pojo;

import javax.persistence.*;

@Table(name = "search_records")
public class SearchRecords {
    @Id
    private String id;

    private String content;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}