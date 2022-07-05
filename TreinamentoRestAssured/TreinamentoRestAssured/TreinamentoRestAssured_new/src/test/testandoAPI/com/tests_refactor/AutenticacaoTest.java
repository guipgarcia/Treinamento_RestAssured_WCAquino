package com.tests_refactor;

import com.core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class AutenticacaoTest extends BaseTest {

    @Test
    public void naoDeveAcessarAPISemToken(){
        FilterableRequestSpecification reqSpec = (FilterableRequestSpecification) RestAssured.requestSpecification;
        reqSpec.removeHeader("Authorization");

        given()
        .when()
                .get("/contas")
        .then()
                .statusCode(401)
        ;
    }
}
