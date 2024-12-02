package com.matheus.cupcakeshop.dao;

import com.matheus.cupcakeshop.conn.Conector;
import com.matheus.cupcakeshop.model.ItensPedido;
import com.matheus.cupcakeshop.model.Pedido;
import java.sql.Connection; 
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matheus
 */
public class PedidoDAO {
    
     // SQL Statements
    private static final String INSERT_SQL = "INSERT INTO pedido (cliente, data_pedido, status, total) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE pedido SET cliente = ?, data_pedido = ?, status = ?, total = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM pedido WHERE id = ?";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM pedido WHERE id = ?";
    private static final String SELECT_ALL_SQL = "SELECT p.id, c.nome, p.status, p.total, p.data_pedido \n" +
                                                                                "FROM pedido p\n" +
                                                                                "inner join cliente c on c.id = p.cliente ";

    private final ItensPedidoDAO itensPedidoDAO = new ItensPedidoDAO(); // Para gerenciar itens do pedido

    // CREATE
public void create(Pedido pedido) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    try (Connection connection = Conector.getConnection()) {
        //connection.setAutoCommit(false); // Inicia transação 

        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            // Inserir pedido
           
            statement.setInt(1, pedido.getCliente());
            statement.setDate(2, pedido.getDataPedido());
            statement.setString(3, pedido.getStatus());
            statement.setDouble(4, pedido.getTotal());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        pedido.setId(generatedKeys.getLong(1)); // Obtem o ID gerado
                    }
                }
                System.out.println("Pedido inserido com sucesso!");
            }

            // Salvar itens do pedido
            for (ItensPedido item : pedido.getItensPedido()) {
                item.setPedido(pedido);
                itensPedidoDAO.create(item); // Passa a mesma conexão
            }

           // connection.commit(); // Finaliza a transação
        } catch (SQLException e) {
           // connection.rollback(); // Reverte em caso de erro
            throw e;
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    // READ by ID
    public Pedido readById(Long id) {
        try (Connection connection = Conector.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_SQL)) {

            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Pedido pedido = parsePedido(resultSet);
                    List<ItensPedido> itens = itensPedidoDAO.readByPedidoId(pedido.getId());
                    pedido.setItensPedido(itens);
                    return pedido;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // READ all
    public List<Pedido> readAll() {
        List<Pedido> pedidos = new ArrayList<>();
        try (Connection connection = Conector.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Pedido pedido = parsePedido(resultSet);
                List<ItensPedido> itens = itensPedidoDAO.readByPedidoId(pedido.getId());
                pedido.setItensPedido(itens);
                pedidos.add(pedido);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pedidos;
    }

    // UPDATE
    public void update(Pedido pedido) {
        try (Connection connection = Conector.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {

            statement.setInt(1, pedido.getCliente());
            statement.setDate(2, pedido.getDataPedido());
            statement.setString(3, pedido.getStatus());
            statement.setDouble(4, pedido.getTotal());
            statement.setLong(5, pedido.getId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Pedido atualizado com sucesso!");
            }

            // Atualizar itens do pedido
            for (ItensPedido item : pedido.getItensPedido()) {
                if (item.getId() == null) {
                    item.setPedido(pedido);
                    itensPedidoDAO.create(item);
                } else {
                    itensPedidoDAO.update(item);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // DELETE
    public void delete(Long id) {
        try (Connection connection = Conector.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {

            // Deletar itens associados
            List<ItensPedido> itens = itensPedidoDAO.readByPedidoId(id);
            for (ItensPedido item : itens) {
                itensPedidoDAO.delete(item.getId());
            }

            statement.setLong(1, id);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Pedido deletado com sucesso!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Parse ResultSet to Pedido
    private Pedido parsePedido(ResultSet resultSet) throws SQLException {
        return Pedido.builder()
                .id(resultSet.getLong("id"))
                .nome(resultSet.getString("nome"))
                .dataPedido(resultSet.getDate("data_pedido"))
                .status(resultSet.getString("status"))
                .total(resultSet.getDouble("total"))
                .build();
    }
    
}
