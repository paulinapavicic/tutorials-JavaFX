package com.paulina.tutorials.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Tutorial {
    private Long id;
    private String title;
    private String description;
    private boolean published;


    public Tutorial() {
    }


    @JsonCreator
    public Tutorial(@JsonProperty("id") Long id,
                    @JsonProperty("title") String title,
                    @JsonProperty("description") String description,
                    @JsonProperty("published") boolean published) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.published = published;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    @Override
    public String toString() {
        return "Tutorial{id=" + id + ", title='" + title + "', published=" + published + "}";
    }
}