/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.matheus.cupcakeshop.resources;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.matheus.cupcakeshop.dao.ClienteDAO;
import com.matheus.cupcakeshop.model.Cliente;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
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
@WebServlet("/Clientes")
public class ClientesController extends HttpServlet {

    ClienteDAO clienteDao = new ClienteDAO();

    // Método para processar requisições POST
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Lê o parâmetro 'acao' da URL
        String acao = (request.getParameter("acao") != null && !"".equals(request.getParameter("acao"))) ? request.getParameter("acao") : "";

        // Configura o tipo de conteúdo da resposta como JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Usamos o Gson para converter o JSON recebido para objetos Java
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        switch (acao) {
            case "login":
                handleLogin(request, response, json);
                break;
            case "adicionarCliente":
                handleAdicionarCliente(request, response, gson);
                break;
            case "":
                listarClientes(request, response, gson);
                break;
            default:
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Ação não encontrada\"}");
                break;
        }
    }

    private void listarClientes(HttpServletRequest request, HttpServletResponse response, Gson gson) throws IOException {
        List<Cliente> clientes = clienteDao.readAll();
        String result = gson.toJson(clientes);
        try (PrintWriter out = response.getWriter()) {
            out.print(result);
            out.flush();
        }

    }

    // Método para lidar com o login
    private void handleLogin(HttpServletRequest request, HttpServletResponse response, JsonObject json) throws IOException {
        // Recebe o JSON do corpo da requisição
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        if ("admin".equals(email) && "123".equals(senha)) {
            json.addProperty("status", 2);
            json.addProperty("message", "Login bem-sucedido !");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(json.toString());
        } else {
            clienteDao = new ClienteDAO();
            Cliente cliente = clienteDao.findByEmailAndSenha(email, senha);//gson.fromJson(reader, Cliente.class);  // Cliente deve ter 'email' e 'senha'

            if (cliente != null) {
                json.addProperty("usuario", cliente.getNome());
                json.addProperty("idCliente", String.valueOf(cliente.getId()));
                json.addProperty("status", 1);
                json.addProperty("message", "Login bem-sucedido !");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(json.toString());
            } else {
                json.addProperty("status", 0);
                json.addProperty("message", "Credenciais inválidas !");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(json.toString());
            }
        }
    }

    // Método para lidar com o cadastro de um cliente
    private void handleAdicionarCliente(HttpServletRequest request, HttpServletResponse response, Gson gson) throws IOException {
        // Lê o JSON da requisição
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder jsonBuilder = new StringBuilder();
        String line;

        // Concatena as linhas do JSON
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }
        // Recebe o JSON do corpo da requisição
        String jsonString = jsonBuilder.toString();

        // Converte o JSON em um JsonObject
        JsonObject jsonObject = new Gson().fromJson(jsonString, JsonObject.class);
        //Cliente cliente = gson.fromJson(reader, Cliente.class);  // Cliente com todos os dados

        String dataNascimentoStr = jsonObject.get("dataNascimento").getAsString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dataNasc = null;
        try {
            dataNasc = new Date(sdf.parse(dataNascimentoStr).getTime());
        } catch (Exception e) {
            e.printStackTrace();
            // Tratar erro de parsing se necessário
        }

        Cliente cliente = new Cliente();
        cliente.setEmail(jsonObject.get("email").getAsString());
        cliente.setDataNascimento(dataNasc);
        cliente.setNome(jsonObject.get("nome").getAsString());
        cliente.setSenha(jsonObject.get("senha").getAsString());
        
        clienteDao.create(cliente);
        // Aqui você pode adicionar o cliente ao banco de dados (lógica de persistência)
        // Exemplo simples de sucesso:
        response.getWriter().write("{\"message\": \"Cliente adicionado com sucesso!\"}");
    }
}
