package org.example.models;

public class Page {

    private int pageNumber;
    private String pageText;

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getPageText() {
        return pageText;
    }

    public void setPageText(String pageText) {
        this.pageText = pageText;
    }

    public Page(int pageNumber,String pageText) {
        this.pageNumber = pageNumber;
        this.pageText = pageText;
    }
}
