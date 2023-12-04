package org.example.models;

public class Page {


    private int bookId;

    private int number;
    private String content;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public Page(int bookId, int number, String pageText) {
        this.bookId = bookId;
        this.number = number;
        this.content = pageText;
    }

}
