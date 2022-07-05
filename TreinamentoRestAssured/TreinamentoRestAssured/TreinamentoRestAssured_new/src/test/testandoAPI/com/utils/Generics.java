package com.utils;

import com.core.Movimentacao;
import io.restassured.RestAssured;

public class Generics {

    public static Integer getIdContaPeloNome(String nome){
        return RestAssured.get("/contas?nome="+nome).then().extract().path("id[0]");
    }

    public static Integer getIdMovimentacaoPelaDescricao(String descricao){
        return RestAssured.get("/transacoes?descricao="+descricao).then().extract().path("id[0]");
    }

    public static Movimentacao inserirMovimentacaoValida(String nome){
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setConta_id(getIdContaPeloNome(nome));
        movimentacao.setDescricao("Descricao movimentacao Futura");
        movimentacao.setEnvolvido("Envolvido na movimentacao Futura");
        movimentacao.setTipo("REC");
        movimentacao.setData_transacao(DataUtils.getDataDiferencaDeDias(-2));
        movimentacao.setData_pagamento(DataUtils.getDataDiferencaDeDias(5));
        movimentacao.setValor(100f);
        movimentacao.setStatus(true);
        return movimentacao;
    }
}
