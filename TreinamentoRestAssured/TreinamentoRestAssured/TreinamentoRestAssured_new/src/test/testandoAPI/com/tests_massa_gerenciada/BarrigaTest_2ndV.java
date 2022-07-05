package com.tests_massa_gerenciada;

import com.core.BaseTest;
import com.core.Movimentacao;
import com.utils.DataUtils;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;

//Executa os testes por ordem alfabética
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BarrigaTest_2ndV extends BaseTest {

    private static String conta = "Conta " + System.nanoTime();
    private static String contaAlterada = conta + " alterada";
    private static Integer conta_id;
    private static Integer movimentacao_id;
    Movimentacao movimentacao = new Movimentacao();

    @BeforeClass
    public static void before(){
        // Login Data
        Map<String, String> login = new HashMap<>();
        login.put("email", "guilherme@email.com");
        login.put("senha" , "senha");

        // Getting Token
        String token =
                given()
                        .body(login)
                .when()
                        .post("/signin")
                .then()
                        .statusCode(200)
                        .extract().path("token")
        ;
        RestAssured.requestSpecification.header("Authorization", "JWT " + token);
    }

    @Test
    public void test11_naoDeveAcessarApiSemToken(){
        FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
        req.removeHeader("Authorization");

        given()
        .when()
                .get("/contas")
        .then()
                .statusCode(401)
        ;
    }

    @Test
    public void test02_deveIncluirContaComSucesso(){
        // Sending the token and creating a new "conta"
        conta_id =
            given()
                    .body("{\"nome\":\""+conta+"\"}")
            .when()
                    .post("/contas")
            .then()
                    .statusCode(201)
                    .extract().path("id")
            ;

    }

    @Test
    public void test03_deveAlterarContaComSucesso(){
        // Sending the token and creating a new "conta"
        given()
                .body("{\"nome\":\""+contaAlterada+"\"}")
                .pathParam("id" , conta_id)
        .when()
                .put("/contas/{id}")
        .then()
                .statusCode(200)
                .body("nome" , is(contaAlterada))
        ;
    }

    @Test
    public void test04_naoDeveIncluirContaComNomeRepetido(){
        given()
                .body("{\"nome\":\""+contaAlterada+"\"}")
        .when()
                .post("/contas")
        .then()
                .statusCode(400)
                .body("error", is("Já existe uma conta com esse nome!"))
        ;
    }

    @Test
    public void test05_deveInserirMovimentacaoComSucesso(){
        movimentacao.setConta_id(conta_id);
        movimentacao.setDescricao("Descricao movimentacao");
        movimentacao.setEnvolvido("Envolvido na movimentacao");
        movimentacao.setTipo("REC");
        movimentacao.setData_transacao(DataUtils.getDataDiferencaDeDias(-5));
        movimentacao.setData_pagamento(DataUtils.getDataDiferencaDeDias(5));
        movimentacao.setValor(100f);
        movimentacao.setStatus(true);

        movimentacao_id =
            given()
                    .body(movimentacao)
            .when()
                    .post("/transacoes")
            .then()
                    .statusCode(201)
                    .extract().path("id")
            ;
    }

    @Test
    public void test06_deveValidarDadosObrigatoriosTransacao(){
        given()
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
    public void test07_naoDeveInserirMovimentacaoComDataFutura(){
        movimentacao.setConta_id(conta_id);
        movimentacao.setDescricao("Descricao movimentacao Futura");
        movimentacao.setEnvolvido("Envolvido na movimentacao Futura");
        movimentacao.setTipo("REC");
        movimentacao.setData_transacao(DataUtils.getDataDiferencaDeDias(2));
        movimentacao.setData_pagamento(DataUtils.getDataDiferencaDeDias(5));
        movimentacao.setValor(100f);
        movimentacao.setStatus(true);

        given()
                .body(movimentacao)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(400)
                .body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
        ;
    }

    @Test
    public void test08_naoDeveRemoverContaComMovimentacao(){
        given()
                .pathParam("id", conta_id)
        .when()
                .delete("contas/{id}")
        .then()
                .statusCode(500)
                .body("constraint", is("transacoes_conta_id_foreign"))
                .body("name", is("error"))
        ;
    }

    @Test
    public void test09_deveCalcularSaldoContas(){
        //find{it...} percorre a coleção e procura pelo que foi sugerido
        given()
        .when()
                .get("/saldo")
        .then()
                .statusCode(200)
                .body("find{it.conta_id == "+conta_id+"}.saldo", is("100.00"))
        ;
    }

    @Test
    public void test10_deveRemoverMovimentacao(){
        given()
                .pathParam("id", movimentacao_id)
        .when()
                .delete("/transacoes/{id}")
        .then()
                .statusCode(204)
        ;
    }
}

