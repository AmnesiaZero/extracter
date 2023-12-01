package org.example.models;

import com.google.gson.Gson;

import java.util.List;

public class Document {
    private List<Page> pages;
    public Response response;
    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }


    public Document(List<Page> pages) {
        this.pages = pages;
    }

    // геттеры и сеттеры

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
