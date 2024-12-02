/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.matheus.cupcakeshop.resources;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.matheus.cupcakeshop.dao.ClienteDAO;
import com.matheus.cupcakeshop.dao.PedidoDAO;
import com.matheus.cupcakeshop.model.Cliente;
import com.matheus.cupcakeshop.model.Cupcake;
import com.matheus.cupcakeshop.model.ItensPedido;
import com.matheus.cupcakeshop.model.Pedido;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Matheus
 */
@WebServlet("/Pedidos")
public class PedidosController extends HttpServlet { 
    PedidoDAO pedidoDao = new PedidoDAO();
    // Método para processar requisições POST
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Lê o parâmetro 'acao' da URL
        String acao = (request.getParameter("acao") != null && !"".equals(request.getParameter("acao")))  ?  request.getParameter("acao"): "";

        // Configura o tipo de conteúdo da resposta como JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Usamos o Gson para converter o JSON recebido para objetos Java
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        switch (acao) {
            case "":
            listarPedidos(request, response, gson);
            break;
            case "adicionarPedido":
            handleAdicionarPedido(request, response);
            break; 
            default:
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Ação não encontrada\"}");
                break;
        }
    }
    
    private void listarPedidos(HttpServletRequest request, HttpServletResponse response, Gson gson) throws IOException {  
            List<Pedido> pedidos = pedidoDao.readAll();
            String result = gson.toJson(pedidos); 
            try (PrintWriter out = response.getWriter()) {
            out.print(result);
            out.flush(); 
            } 
    } 

    // Método para lidar com o cadastro de um cliente
   private void handleAdicionarPedido(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Lê o JSON da requisição
    BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
    StringBuilder jsonBuilder = new StringBuilder();
    String line;
    
    // Concatena as linhas do JSON
    while ((line = reader.readLine()) != null) {
        jsonBuilder.append(line);
    }
    
    // Obtém o JSON como string
    String jsonString = jsonBuilder.toString();
    
    // Converte o JSON em um JsonObject
    JsonObject jsonObject = new Gson().fromJson(jsonString, JsonObject.class);

    // Extração e formatação da data
    String dataPedidoStr = jsonObject.get("dataPedido").getAsString();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Date dataPedido = null;
    try {
        dataPedido = new Date(sdf.parse(dataPedidoStr).getTime());
    } catch (Exception e) {
        e.printStackTrace();
        // Tratar erro de parsing se necessário
    }

    // Criação do objeto Pedido
    Pedido pedido = new Pedido();
    pedido.setCliente(jsonObject.get("cliente").getAsInt());
    pedido.setDataPedido(dataPedido);
    pedido.setStatus(jsonObject.get("status").getAsString());
    pedido.setTotal(jsonObject.get("total").getAsDouble());

    // Processamento dos itensPedido
    List<ItensPedido> itensPedido = new ArrayList<>();
    JsonArray itensArray = jsonObject.getAsJsonArray("itensPedido");
    
    for (JsonElement itemElement : itensArray) {
        JsonObject itemObj = itemElement.getAsJsonObject();
        
        // Criação do item
        ItensPedido itemPedido = new ItensPedido();
        
        // Criação do objeto Cupcake
        JsonObject cupcakeObj = itemObj.getAsJsonObject("cupcake");
        Cupcake cupcake = new Cupcake();
        cupcake.setId(cupcakeObj.get("id").getAsLong()); 
        //cupcake.setPreco(cupcakeObj.get("precoUnitario").getAsDouble());
        
        // Preenchendo o ItensPedido
        itemPedido.setCupcake(cupcake);
        itemPedido.setQuantidade(itemObj.get("quantidade").getAsInt());
        itemPedido.setPrecoUnitario(itemObj.get("precoUnitario").getAsDouble());
        
        // Adiciona o item à lista
        itensPedido.add(itemPedido);
    }
    
    // Associa os itens ao pedido
    pedido.setItensPedido(itensPedido);

    // Agora o objeto Pedido está pronto para ser salvo
    try {
        PedidoDAO pedidoDAO = new PedidoDAO();
        pedidoDAO.create(pedido);

        // Retorna o ID do pedido salvo
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(new Gson().toJson("Pedido criado com sucesso"));
    } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write("{\"message\": \"Erro ao salvar pedido.\"}");
        e.printStackTrace();
    }
}

}
