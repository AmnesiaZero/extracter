package org.example.DAO;

import org.example.models.Document;

import java.sql.PreparedStatement;
import java.sql.SQLException;

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
