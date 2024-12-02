package com.matheus.cupcakeshop.model;

import java.io.Serializable;
import lombok.*;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cupcake implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String sabor;

    private String cobertura;

    private String decoracao;

    private Double preco;

    private String tipo;

    private byte[] imagem;
    
     private String ativo;
    
     public Cupcake(String sabor, String cobertura, String decoracao, double preco, String tipo, String ativo) {
        this.sabor = sabor;
        this.cobertura = cobertura;
        this.decoracao = decoracao;
        this.preco = preco;
        this.tipo = tipo;
        this.ativo = ativo;
    }

}

