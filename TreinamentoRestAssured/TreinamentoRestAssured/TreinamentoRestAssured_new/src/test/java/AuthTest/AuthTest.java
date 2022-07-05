package AuthTest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AuthTest {
    // API KEY 7ce1334f3c88edb4c7fa993c06c0422e
    @BeforeClass
    public static void setup(){
        //RestAssured.baseURI = "https://swapi.dev/api";

    }
    @Test
    public void deveAcessarSWA(){
        given()
                .log().all()
        .when()
                .get("https://swapi.dev/api/people/1")
        .then()
                .log().all()
                .statusCode(200)
                .body("name" , is("Luke Skywalker"))
        ;
    }

    @Test
    public void deveObterClima(){
        given()
                .log().all()
                .queryParam("q", "Recife")
                .queryParam("appid", "7ce1334f3c88edb4c7fa993c06c0422e" )
        .when()
                .get("https://api.openweathermap.org/data/2.5/weather")
        .then()
                .log().all()
                .statusCode(200)
                .body("name",is("Recife"))
                .body("id", is(3390760))
                .body("cod", is(200))
                .body("main.temp", greaterThan(300f))
        ;

    }

    @Test
    public void devePedirAutenticaoSenhaErrada(){
        given()
                .log().all()
        .when()
                .get("http://restapi.wcaquino.me/basicauth")
        .then()
                .log().all()
                .statusCode(401)
        ;
    }

    @Test
    public void devePedirAutenticacaoSenhaFornecida(){
        given()
                .log().all()
        .when()
                .get("http://admin:senha@restapi.wcaquino.me/basicauth")//admin:senha são os autenticadores, ou seja: irão preencher os respectivos login e senha pedidos no browser
        .then()
                .log().all()
                .statusCode(200)
                .body("status", is("logado"))
        ;
    }

    @Test
    public void devePedirAutenticacaoSenhaFornecidaOutraForma(){
        given()
                .log().all()
                .auth().basic("admin","senha")
        .when()
                .get("http://restapi.wcaquino.me/basicauth")
        .then()
                .log().all()
                .statusCode(200)
                .body("status", is("logado"))
        ;
    }

    @Test
    public void devePedirAutenticacaoSenhaFornecidaNovaRota(){
        given()
                .log().all()
                .auth().preemptive().basic("admin","senha")
        .when()
                .get("http://restapi.wcaquino.me/basicauth2")
        .then()
                .log().all()
                .statusCode(200)
                .body("status", is("logado"))
        ;
    }

    @Test
    public void deveFazerAutenticacaoComTokenJWT(){
        Map<String, String> login = new HashMap<String, String>();
        login.put("email", "guilherme@email.com");
        login.put("senha" , "senha");

        // Logando para pegar o token da requisicao
        String token =
            given()
                .log().all()
                .body(login)
                .contentType(ContentType.JSON)
            .when()
                .post("http://barrigarest.wcaquino.me/signin")
            .then()
                .log().all()
                .statusCode(200)
                .extract().path("token")
        ;

        // Assertivando as contas possíveis
        given()
                .log().all()
                .header("Authorization", "JWT " + token)
        .when()
                .get("http://barrigarest.wcaquino.me/contas")
        .then()
                .log().all()
                .statusCode(200)
                .body("id", hasItem(864330))
                .body("nome", hasItem("conta de teste"))
                .body("visivel", hasItem(true))
                .body("usuario_id", hasItem(25563))
        ;

    }

}
