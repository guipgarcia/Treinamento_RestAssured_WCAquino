package Html;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.is;

public class Html {
    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "https://restapi.wcaquino.me/";
        RestAssured.basePath = "v2";
    }

    @Test
    public void deveBuscarComHTML(){
        given()
                .log().all()

        .when()
                .get("/users")
        .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body("html.body.div.table.tbody.tr.size()", is(3))
                .body("html.body.div.table.tbody.tr[1].td[2]", is("25"))
                .appendRootPath("html.body.div.table.tbody") // fazendo um atalho no caminho da celula
                .body("tr.find{it.toString().startsWith('2')}.td[1]", is("Maria Joaquina"))// o find vai trazer a coleção, onde o it vai transformar tudo em string e retornar tudo que começar com "2"
        ;
    }

    @Test
    public void deveBuscarComXpathNoHTML(){
        given()
                .log().all()
                .queryParam("format", "clean")
        .when()
                .get("/users")
        .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(hasXPath("count(//table/tr)", is("4")))
                .body(hasXPath("//td[text()='2']/../td[2]", is("Maria Joaquina")))
       ;
    }
}
