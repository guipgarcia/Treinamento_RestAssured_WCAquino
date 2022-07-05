package com.suite;

import com.core.BaseTest;
import com.tests_refactor.AutenticacaoTest;
import com.tests_refactor.ContasTest;
import com.tests_refactor.MovimentacaoTest;
import com.tests_refactor.SaldosTest;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@RunWith(Suite.class)
@SuiteClasses({
        ContasTest.class,
        MovimentacaoTest.class,
        SaldosTest.class,
        AutenticacaoTest.class
})
public class SuiteTest extends BaseTest {
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
        RestAssured.get("/reset").then().statusCode(200);
    }
}
