package com.tests;

import com.core.BaseTest;
import com.core.Movimentacao;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class BarrigaTest extends BaseTest {
    String token;

    @Before
    public void before(){
        // Login Data
        Map<String, String> login = new HashMap<>();
        login.put("email", "guilherme@email.com");
        login.put("senha" , "senha");

        // Getting Token
        token =
                given()
                        .body(login)
                        .when()
                        .post("/signin")
                        .then()
                        .statusCode(200)
                        .extract().path("token")
                ;
    }
    @Test
    public void naoDeveAcessarApiSemToken(){
        given()
        .when()
                .get("/contas")
        .then()
                .statusCode(401)
        ;
    }

    @Test
    public void deveIncluirContaComSucesso(){
        // Sending the token and creating a new "conta"
        given()
                .header("Authorization", "JWT " + token)
                .body("{\"nome\":\"conta test\"}")
        .when()
                .post("/contas")
        .then()
                .statusCode(201);

    }

    @Test
    public void deveAlterarContaComSucesso(){
        // Sending the token and creating a new "conta"
        given()
                .header("Authorization", "JWT " + token)
                .body("{\"nome\":\"conta 123\"}")
        .when()
                .put("/contas/891544")
        .then()
                .statusCode(200)
                .body("nome" , is("conta 123"))
        ;
    }

    @Test
    public void naoDeveIncluirContaComNomeRepetido(){
        given()
                .header("Authorization", "JWT " + token)
                .body("{\"nome\":\"conta 123\"}")
        .when()
                .post("/contas")
        .then()
                .statusCode(400)
                .body("error", is("Já existe uma conta com esse nome!"))
        ;
    }

    @Test
    public void deveInserirMovimentacaoComSucesso(){
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setConta_id(891544);
        movimentacao.setDescricao("Descricao movimentacao");
        movimentacao.setEnvolvido("Envolvido na movimentacao");
        movimentacao.setTipo("REC");
        movimentacao.setData_transacao("01/01/2000");
        movimentacao.setData_pagamento("11/06/2010");
        movimentacao.setValor(100f);
        movimentacao.setStatus(true);

        given()
                .header("Authorization", "JWT " + token)
                .body(movimentacao)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(201)
        ;
    }

    @Test
    public void deveValidarDadosObrigatoriosTransacao(){
        given()
                .header("Authorization", "JWT " + token)
                .body("{}")
        .when()
                .post("/transacoes")
        .then()
                .statusCode(400)
                .body("$", hasSize(8))
                .body("msg", hasItems(
                        "Data da Movimentação é obrigatório",
                        "Data do pagamento é obrigatório",
                        "Descrição é obrigatório",
                        "Interessado é obrigatório",
                        "Valor é obrigatório",
                        "Valor deve ser um número",
                        "Conta é obrigatório",
                        "Situação é obrigatório"
                ))
        ;
    }

    @Test
    public void naoDeveInserirMovimentacaoComDataFutura(){
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setConta_id(891544);
        movimentacao.setDescricao("Descricao movimentacao");
        movimentacao.setEnvolvido("Envolvido na movimentacao");
        movimentacao.setTipo("REC");
        movimentacao.setData_transacao("10/01/2022");
        movimentacao.setData_pagamento("10/01/2022");
        movimentacao.setValor(100f);
        movimentacao.setStatus(true);

        given()
                .header("Authorization", "JWT " + token)
                .body(movimentacao)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(400)
                .body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
        ;
    }

    @Test
    public void naoDeveRemoverContaComMovimentacao(){
        given()
                .header("Authorization", "JWT " + token)
        .when()
                .delete("contas/891544")
        .then()
                .statusCode(500)
                .body("constraint", is("transacoes_conta_id_foreign"))
                .body("name", is("error"))
        ;
    }

    @Test
    public void deveCalcularSaldoContas(){
        //find{it...} percorre a coleção e procura pelo que foi sugerido
        given()
                .header("Authorization", "JWT " + token)
        .when()
                .get("/saldo")
        .then()
                .statusCode(200)
                .body("find{it.conta_id == 891544}.saldo", is("400.00"))
        ;
    }

    @Test
    public void deveRemoverMovimentacao(){
        given()
                .header("Authorization", "JWT " + token)
        .when()
                .delete("/transacoes/829285")
        .then()
                .statusCode(204)
        ;
    }
}
