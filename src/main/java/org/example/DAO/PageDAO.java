package org.example.DAO;

import org.example.models.Page;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

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

    public int getLastBook() throws SQLException {

        PreparedStatement preparedStatement = dataSource.connection.prepareStatement("SELECT MAX(`book_id`) FROM `book_content` LIMIT 1");
        return  SqlConverter.executeInt(preparedStatement,"book_id");
    }
}
