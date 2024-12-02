/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.matheus.cupcakeshop.dao;

import com.matheus.cupcakeshop.conn.Conector;
import com.matheus.cupcakeshop.model.Cupcake;
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
public class CupcakeDAO {

    // SQL Statements
    private static final String INSERT_SQL = "INSERT INTO cupcake (sabor, cobertura, decoracao, preco, tipo, imagem) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE cupcake SET sabor = ?, cobertura = ?, decoracao = ?, preco = ?, tipo = ?, imagem = ? WHERE id = ?";
    private static final String DESATIVAR_SQL = "UPDATE cupcake SET ATIVO ='F' WHERE id = ?";
    private static final String ATIVAR_SQL = "UPDATE cupcake SET ATIVO ='T' WHERE id = ?";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM cupcake WHERE id = ? ";
    private static final String SELECT_ALL_SQL = "SELECT * FROM cupcake ";
    private static final String SELECT_ALL_ACTIVE_SQL = "SELECT * FROM cupcake WHERE ATIVO = 'T' ";

    // CREATE
    public void create(Cupcake cupcake) {
        try (Connection connection = Conector.getConnection(); PreparedStatement statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, cupcake.getSabor());
            statement.setString(2, cupcake.getCobertura());
            statement.setString(3, cupcake.getDecoracao());
            statement.setDouble(4, cupcake.getPreco());
            statement.setString(5, cupcake.getTipo());
            statement.setBytes(6, cupcake.getImagem());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        cupcake.setId(generatedKeys.getLong(1));
                    }
                }
                System.out.println("Cupcake inserido com sucesso!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // READ by ID
    public Cupcake readById(Long id) {
        try (Connection connection = Conector.getConnection(); PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_SQL)) {

            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return parseCupcake(resultSet);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // READ all
    public List<Cupcake> readAll(boolean onlyActives) throws SQLException {
        List<Cupcake> cupcakes = new ArrayList<>();

        // Escolhe o SQL com base no parâmetro
        String query = onlyActives ? SELECT_ALL_ACTIVE_SQL : SELECT_ALL_SQL;

        try (Connection connection = Conector.getConnection(); PreparedStatement statement = connection.prepareStatement(query); ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                cupcakes.add(parseCupcake(resultSet));
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar cupcakes.", e);
        }
        return cupcakes;
    }

    // UPDATE
    public void update(Cupcake cupcake) {
        try (Connection connection = Conector.getConnection(); PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {

            statement.setString(1, cupcake.getSabor());
            statement.setString(2, cupcake.getCobertura());
            statement.setString(3, cupcake.getDecoracao());
            statement.setDouble(4, cupcake.getPreco());
            statement.setString(5, cupcake.getTipo());
            statement.setBytes(6, cupcake.getImagem());
            statement.setLong(7, cupcake.getId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Cupcake atualizado com sucesso!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // DELETE
    public boolean desactiveOrActive(Long id, boolean action) {
    // Seleciona a query com base no valor de `action`
    String query = action ? ATIVAR_SQL : DESATIVAR_SQL;

    try (Connection connection = Conector.getConnection();
         PreparedStatement statement = connection.prepareStatement(query)) {

        statement.setLong(1, id);

        // Executa a atualização e verifica se foi bem-sucedida
        int rowsAffected = statement.executeUpdate();
        return rowsAffected > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}


    // Parse ResultSet to Cupcake
    private Cupcake parseCupcake(ResultSet resultSet) throws SQLException {
        return Cupcake.builder()
                .id(resultSet.getLong("id"))
                .sabor(resultSet.getString("sabor"))
                .cobertura(resultSet.getString("cobertura"))
                .decoracao(resultSet.getString("decoracao"))
                .preco(resultSet.getDouble("preco"))
                .tipo(resultSet.getString("tipo"))
                .imagem(resultSet.getBytes("imagem"))
                .ativo(resultSet.getString("ativo"))
                .build();
    }

}
