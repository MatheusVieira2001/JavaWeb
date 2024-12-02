package com.matheus.cupcakeshop.model;

import java.io.Serializable;
import lombok.*;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItensPedido implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Pedido pedido;

    private Cupcake cupcake;

    private Integer quantidade;

    private Double precoUnitario;
}
