package com.matheus.cupcakeshop.dao;

import com.matheus.cupcakeshop.conn.Conector;
import com.matheus.cupcakeshop.model.Cupcake;
import com.matheus.cupcakeshop.model.ItensPedido;
import com.matheus.cupcakeshop.model.Pedido;
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
public class ItensPedidoDAO {
    
     // SQL Statements
    private static final String INSERT_SQL = "INSERT INTO itens_pedido (pedido, cupcake, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE itens_pedido SET pedido = ?, cupcake_id = ?, quantidade = ?, preco_unitario = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM itens_pedido WHERE id = ?";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM itens_pedido WHERE id = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM itens_pedido";
    private static final String SELECT_BY_PEDIDO_ID_SQL = "SELECT * FROM itens_pedido WHERE pedido = ?";


    // CREATE
    public void create(ItensPedido itensPedido) {
        try (Connection connection = Conector.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, itensPedido.getPedido().getId());
            statement.setLong(2, itensPedido.getCupcake().getId());
            statement.setInt(3, itensPedido.getQuantidade());
            statement.setDouble(4, itensPedido.getPrecoUnitario());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        itensPedido.setId(generatedKeys.getLong(1));
                    }
                }
                System.out.println("Item de pedido inserido com sucesso!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // READ by ID
    public ItensPedido readById(Long id) {
        try (Connection connection = Conector.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_SQL)) {

            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return parseItensPedido(resultSet);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // READ all
    public List<ItensPedido> readAll() {
        List<ItensPedido> itensPedidos = new ArrayList<>();
        try (Connection connection = Conector.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                itensPedidos.add(parseItensPedido(resultSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return itensPedidos;
    }

    // UPDATE
    public void update(ItensPedido itensPedido) {
        try (Connection connection = Conector.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {

            statement.setLong(1, itensPedido.getPedido().getId());
            statement.setLong(2, itensPedido.getCupcake().getId());
            statement.setInt(3, itensPedido.getQuantidade());
            statement.setDouble(4, itensPedido.getPrecoUnitario());
            statement.setLong(5, itensPedido.getId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Item de pedido atualizado com sucesso!");
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
                System.out.println("Item de pedido deletado com sucesso!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Parse ResultSet to ItensPedido 
    private ItensPedido parseItensPedido(ResultSet resultSet) throws SQLException {
        return ItensPedido.builder()
                .id(resultSet.getLong("id"))
                .pedido(Pedido.builder()
                        .id(resultSet.getLong("pedido"))
                        .build()) // Apenas referência ao Pedido
                .cupcake(Cupcake.builder()
                        .id(resultSet.getLong("cupcake"))
                        .build()) // Apenas referência ao Cupcake
                .quantidade(resultSet.getInt("quantidade"))
                .precoUnitario(resultSet.getDouble("preco_unitario"))
                .build();
    }
    
    public List<ItensPedido> readByPedidoId(Long pedidoId) {
        List<ItensPedido> itens = new ArrayList<>();
        try (Connection connection = Conector.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_PEDIDO_ID_SQL)) {

            statement.setLong(1, pedidoId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ItensPedido item = parseItensPedido(resultSet);
                    itens.add(item);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return itens;
    }
    
}
