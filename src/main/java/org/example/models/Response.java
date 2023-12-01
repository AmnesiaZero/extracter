package org.example.models;

import java.util.List;

public class Response {
    private boolean success;
    private String message;

    private String title;

    private List<Page> pages;

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    private List<String> errors;

    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    public Response(boolean success, String message,String title,List<Page> pages,List<String> errors) {
        this.success = success;
        this.message = message;
        this.title = title;
        this.pages = pages;
        this.errors = errors;
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
