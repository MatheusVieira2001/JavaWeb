package com.matheus.cupcakeshop.dao;

import com.matheus.cupcakeshop.conn.Conector;
import com.matheus.cupcakeshop.model.Cliente;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author Matheus
 */
public class ClienteDAO {
    // SQL Statements
    private static final String INSERT_SQL = "INSERT INTO cliente (nome, email, senha, data_nascimento) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE cliente SET nome = ?, email = ?, senha = ?, data_nascimento = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM cliente WHERE id = ?";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM cliente WHERE id = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM cliente";

    // CREATE
public boolean create(Cliente cliente) {
    String sql = "INSERT INTO cliente (nome, email, senha, data_nascimento) VALUES (?, ?, ?, ?)";
    try (Connection connection = Conector.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

        statement.setString(1, cliente.getNome());
        statement.setString(2, cliente.getEmail());
        statement.setString(3, cliente.getSenha());
        statement.setDate(4, cliente.getDataNascimento());

        return statement.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
} 

public Cliente findByEmailAndSenha(String email, String senha) {
    String sql = "SELECT * FROM cliente WHERE email = ? AND senha = ?";
    try (Connection connection = Conector.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

        statement.setString(1, email);
        statement.setString(2, senha);

        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                Cliente cliente = Cliente.builder()
                        .id(resultSet.getLong("id"))
                        .nome(resultSet.getString("nome"))
                        .email(resultSet.getString("email"))
                        .senha(resultSet.getString("senha"))
                        .dataNascimento(resultSet.getDate("data_nascimento"))
                        .build();
                return cliente;
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}


    // READ by ID
    public Cliente readById(Long id) {
        try (Connection connection = Conector.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_SQL)) {

            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return parseCliente(resultSet);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // READ all
    public List<Cliente> readAll() {
        List<Cliente> clientes = new ArrayList<>();
        try (Connection connection = Conector.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                clientes.add(parseCliente(resultSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }

    // UPDATE
    public void update(Cliente cliente) {
        try (Connection connection = Conector.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {

            statement.setString(1, cliente.getNome());
            statement.setString(2, cliente.getEmail());
            statement.setString(3, cliente.getSenha());
            statement.setDate(4,  cliente.getDataNascimento());
            statement.setLong(5, cliente.getId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Cliente atualizado com sucesso!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // DELETE
    public void delete(Long id) {
        try (Connection connection = Conector.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {

            statement.setLong(1, id);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Cliente deletado com sucesso!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Parse ResultSet to Cliente
    private Cliente parseCliente(ResultSet resultSet) throws SQLException {
        return Cliente.builder()
                .id(resultSet.getLong("id"))
                .nome(resultSet.getString("nome"))
                .email(resultSet.getString("email"))
                .senha(resultSet.getString("senha"))
                .dataNascimento(resultSet.getDate("data_nascimento"))
                .build();
    }
    
    
}
