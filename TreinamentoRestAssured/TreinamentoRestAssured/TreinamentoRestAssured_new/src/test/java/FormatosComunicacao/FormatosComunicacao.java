package FormatosComunicacao;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

public class FormatosComunicacao {

    @BeforeClass
    public static void setup(){
        RestAssured.baseURI = "https://restapi.wcaquino.me/";
        RestAssured.basePath = "v2";
    }

    @Test
    public void deveEnviarValorViaQuery(){
        given().
                log().all().
        when().
                get("/users?format=xml").
        then().
                log().all().
                statusCode(200).
                contentType(ContentType.XML)
        ;
    }

    @Test
    public void deveEnviarValorViaQueryParametrizavel(){
        given().
                log().all().
                queryParam("format", "xml").
        when().
                get("/users").
        then().
                log().all().
                statusCode(200).
                contentType(ContentType.XML).
                contentType(containsString("utf-8"))
        ;
    }

    @Test
    public void deveEnviarValorViaHeader(){
        given()
                .log().all()
                .accept(ContentType.JSON) // .accepet é quando eu quero que o sistema me responda com o formato indicado. No .contentType eu digo qual o formato que estou enviando.
        .when()
                .get("/users")
        .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.JSON)
        ;
    }

}
