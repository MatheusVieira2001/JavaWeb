package com.matheus.cupcakeshop.conn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Matheus
 */
public class Conector {
      // Informações do banco de dados
    private static final String URL = "jdbc:mysql://localhost:3306/loja";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    // Método para obter conexão
    public static Connection getConnection() throws SQLException {
        try {
            // Carregar o driver JDBC (opcional para versões mais novas do JDBC)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC do MySQL não encontrado.", e);
        }

        // Retorna a conexão
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
