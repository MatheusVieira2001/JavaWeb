package com.matheus.cupcakeshop.model;


import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import lombok.*;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cliente implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String nome;

    private String email;

    private String senha;

    private Date dataNascimento;
}