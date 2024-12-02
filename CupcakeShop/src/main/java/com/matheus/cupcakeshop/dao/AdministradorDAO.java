/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.matheus.cupcakeshop.dao;

import com.matheus.cupcakeshop.conn.Conector;
import com.matheus.cupcakeshop.model.Administrador;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Matheus
 */
public class AdministradorDAO {
    
    // SQL Statements
    private static final String INSERT_SQL = "INSERT INTO administrador (nome, email, senha) VALUES (?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE administrador SET nome = ?, email = ?, senha = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM administrador WHERE id = ?";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM administrador WHERE id = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM administrador";

    // CREATE
    public void create(Administrador administrador) {
        try (Connection connection = Conector.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, administrador.getNome());
            statement.setString(2, administrador.getEmail());
            statement.setString(3, administrador.getSenha());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        administrador.setId(generatedKeys.getLong(1));
                    }
                }
                System.out.println("Administrador inserido com sucesso!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // READ by ID
    public Administrador readById(Long id) {
        try (Connection connection = Conector.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_SQL)) {

            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return parseAdministrador(resultSet);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // READ all
    public List<Administrador> readAll() {
        List<Administrador> administradores = new ArrayList<>();
        try (Connection connection = Conector.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                administradores.add(parseAdministrador(resultSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return administradores;
    }

    // UPDATE
    public void update(Administrador administrador) {
        try (Connection connection = Conector.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {

            statement.setString(1, administrador.getNome());
            statement.setString(2, administrador.getEmail());
            statement.setString(3, administrador.getSenha());
            statement.setLong(4, administrador.getId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Administrador atualizado com sucesso!");
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
                System.out.println("Administrador deletado com sucesso!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Parse ResultSet to Administrador
    private Administrador parseAdministrador(ResultSet resultSet) throws SQLException {
        return Administrador.builder()
                .id(resultSet.getLong("id"))
                .nome(resultSet.getString("nome"))
                .email(resultSet.getString("email"))
                .senha(resultSet.getString("senha"))
                .build();
    }
    
}
