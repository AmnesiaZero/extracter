package org.example.DAO;

import org.example.models.Page;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PageDAO {
    public DataSource dataSource;
    public PageDAO(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public void create(Page page) throws SQLException {
        PreparedStatement preparedStatement= dataSource.connection.prepareStatement("INSERT INTO `book_content`(`book_id`,`page_id`,`content`) VALUES(?,?,?)");
        preparedStatement.setInt(1,page.getBookId());
        preparedStatement.setInt(2,page.getNumber());
        preparedStatement.setString(3,page.getContent());
        preparedStatement.execute();

    }
}
