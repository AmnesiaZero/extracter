package org.example.DAO;

import org.example.models.Page;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PageDAO {
    public DataSource dataSource;
    public PageDAO(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public boolean create(Page page) throws SQLException {
        PreparedStatement preparedStatement= dataSource.connection.prepareStatement("INSERT INTO `pages`(`id`,`title`,`number`,`text`) VALUES(NULL,?,?,?)");
        preparedStatement.setInt(1,page.getNumber());
        preparedStatement.setString(2,page.getTitle());
        preparedStatement.setString(3,page.getText());
        preparedStatement.execute();
        return true;

    }
}
