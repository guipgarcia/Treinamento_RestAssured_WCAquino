package com.tests_refactor;

import com.core.BaseTest;
import org.junit.Test;
import static com.utils.Generics.getIdContaPeloNome;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class ContasTest extends BaseTest {

    @Test
    public void deveIncluirContaComSucesso(){
        given()
               .body("{\"nome\" : \"Nova Conta\"}")
        .when()
               .post("/contas")
        .then()
               .statusCode(201)
        ;
    }

    @Test
    public void deveAlterarContaComSucesso(){
        given()
                .body("{\"nome\" : \"Nova Conta - Alterada\"}")
                .pathParam("id", getIdContaPeloNome("Conta para alterar"))
        .when()
                .put("/contas/{id}")
        .then()
                .statusCode(200)
                .body("nome", is("Nova Conta - Alterada"))
        ;
    }

    @Test
    public void naoDeveInserirContaComMesmoNome(){
        given()
                .body("{\"nome\" : \"Conta mesmo nome\"}")
        .when()
                .post("/contas")
        .then()
                .statusCode(400)
                .body("error", is("Já existe uma conta com esse nome!"))
        ;
    }



}
