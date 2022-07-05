package SchemaTest;

import io.restassured.RestAssured;
import io.restassured.matcher.RestAssuredMatchers;

import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXParseException;

import static io.restassured.RestAssured.given;

public class SchemaTest {

    @BeforeClass
    public static void setup(){
        RestAssured.baseURI = "http://restapi.wcaquino.me/";
    }

    @Test(expected = SAXParseException.class)
    public void deveValidarSchemaXML(){
        given()
                .log().all()
        .when()
                .get("usersXML")
        .then()
                .log().all()
                .statusCode(200)
                .body(RestAssuredMatchers.matchesXsdInClasspath("users.xsd"))

        ;
    }

    @Test(expected = SAXParseException.class)
    public void deveValidarSchemaXMLNaRotaInvalida(){
        given()
                .log().all()
        .when()
                .get("invalidUsersXML")
        .then()
                .log().all()
                .statusCode(200)
                .body(RestAssuredMatchers.matchesXsdInClasspath("users.xsd"))

        ;
    }

    @Test(expected = SAXParseException.class)
    public void deveValidarSchemaJson(){
        given()
                .log().all()
        .when()
                .get("users")
        .then()
                .log().all()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("users.json"))

        ;
    }
}
