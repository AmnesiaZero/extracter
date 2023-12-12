package org.example.models;

public class Document {
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    private int number;

    public Document(int number)
    {
        this.number = number;
    }
}
