/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.matheus.cupcakeshop.resources;

import com.google.gson.Gson;
import com.matheus.cupcakeshop.dao.CupcakeDAO;
import com.matheus.cupcakeshop.model.Cupcake;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Matheus
 */
@WebServlet(name = "CupcakeServlet", urlPatterns = {"/cupcakes"})
public class CupcakeController extends HttpServlet {

    private final CupcakeDAO cupcakeDAO = new CupcakeDAO();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write("{\"message\": \"Erro interno !\"}");
    }

    private void SalvarCupcake(HttpServletRequest request, HttpServletResponse response, Gson gson) throws IOException {
        Cupcake cupcake;
        String jsonString = getBodyJson(request);
        cupcake = gson.fromJson(jsonString, Cupcake.class);
        cupcakeDAO.create(cupcake);
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"message\": \"Cupcake salvo com sucesso!\"}");
    }

    private static String getBodyJson(HttpServletRequest request) throws IOException {
        StringBuilder jsonString = new StringBuilder();
        String line;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
        }
        return jsonString.toString();
    }

    private void getCupcake(HttpServletRequest request, Gson gson, PrintWriter out) {
        String cupcakesJson;
        Cupcake cupcake;
        String idCupcake = request.getParameter("cupcakeId");
        cupcake = cupcakeDAO.readById(Long.valueOf(idCupcake));
        cupcakesJson = gson.toJson(cupcake);
        out.print(cupcakesJson);
        out.flush();
    }

    private void ListarCupcakes(Gson gson, PrintWriter out, HttpServletRequest request) {
        boolean resultado = request.getParameter("onlyActives") != null && !"".equals(request.getParameter("onlyActives"));  
        
        String cupcakesJson;
        // Busca todos os cupcakes do banco de dados
        List<Cupcake> cupcakes = null;
        try {
            cupcakes = cupcakeDAO.readAll(resultado);
        } catch (SQLException ex) {
            Logger.getLogger(CupcakeController.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Converte a lista para JSON usando Gson
        cupcakesJson = gson.toJson(cupcakes);

        // Envia o JSON para o front-end
        out.print(cupcakesJson);
        out.flush();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String acao = (request.getParameter("acao") != null && !"".equals(request.getParameter("acao"))) ? request.getParameter("acao") : "";

        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            switch (acao) {
                case "":
                    ListarCupcakes(gson, out, request);
                    break;
                case "getCupcake":
                    getCupcake(request, gson, out);
                    break;
                case "salvar":
                   try {
                    SalvarCupcake(request, response, gson);
                } catch (IOException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"message\": \"Cupcake não criado !\"}");
                }
                break;

                default:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"message\": \"Ação não enviada !\"}");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

 @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Obtém o ID do cupcake a partir da URL
        String idReq = (request.getParameter("id") != null && !"".equals(request.getParameter("id"))) ? request.getParameter("id") : "";
        boolean ativar = request.getParameter("ativar") != null && !"".equals(request.getParameter("ativar"));  
        
        if (idReq != null && !idReq.isEmpty()) { 
            long id = Long.parseLong(idReq);  // Converte o ID para long

            // Tenta desativar ou excluir o cupcake com o ID fornecido
            boolean success = cupcakeDAO.desactiveOrActive(id, ativar);  // Chama o método de desativar/ativar no DAO

            if (success) {
                response.setStatus(HttpServletResponse.SC_OK); // Código 200 - sucesso 
                response.getWriter().write("Cupcake desativado/ativado com sucesso.");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);  // Código 404 - Não encontrado
                response.getWriter().write("Cupcake não encontrado.");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);  // Código 400 - Requisição inválida
            response.getWriter().write("ID do cupcake não fornecido.");
        }
    }
    
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
