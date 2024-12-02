package com.matheus.cupcakeshop.model;

import java.io.Serializable; 
import java.sql.Date;
import java.util.List;
import lombok.*;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pedido implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    
    private int cliente;
    
    private String nome;

    private Date dataPedido;

    private String status;

    private Double total;

    private List<ItensPedido> itensPedido;
}
