package com.tests_refactor;

import com.core.BaseTest;
import org.junit.Test;
import static com.utils.Generics.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class SaldosTest extends BaseTest {

    @Test
    public void deveCalcularSaldoContas(){
        given()
        .when()
                .get("/saldo")
        .then()
                .statusCode(200)
                .body("find{it.conta_id == "+getIdContaPeloNome("Conta para saldo")+"}.saldo", is("534.00"))
        ;
    }
}
