package org.example.models;

import com.google.gson.Gson;
import org.example.DAO.DataSource;
import org.example.DAO.PageDAO;

import java.sql.SQLException;
import java.util.List;

public class PageCollection {
    private List<Page> pages;
    private PageDAO pageDAO;

    public PageCollection(List<Page> pages) throws Exception {
        this.pages = pages;
        this.pageDAO = new PageDAO(new DataSource());
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

    // геттеры и сеттеры

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public void store() throws SQLException {

        for (Page page : pages) {
            pageDAO.create(page);
        }
    }
}
