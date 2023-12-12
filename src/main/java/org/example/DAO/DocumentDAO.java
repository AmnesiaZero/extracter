package org.example.DAO;

import org.example.models.Document;
import org.example.models.Page;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DocumentDAO {
    public DataSource dataSource;

    public DocumentDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void create(Document document) throws SQLException {
        PreparedStatement preparedStatement = dataSource.connection.prepareStatement("INSERT INTO `failed_books`(`id`,`book_id`) VALUES(NULL,?)");
        preparedStatement.setInt(1, document.getNumber());
        preparedStatement.execute();
    }


}
