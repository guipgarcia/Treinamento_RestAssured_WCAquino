package UsersJsonTest;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

public class UsersJsonTest {
    String globalUrl = "http://restapi.wcaquino.me/users/";
    @Test
    public void verificacaoPrimeiroNivel(){
        given()
                .when()
                .get(globalUrl+"1")
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("name", containsString("Silva"))
                .body("age", greaterThan(18));
    }

    @Test
    public void verificarPrimerioNivelDeOutraForma(){
        Response response = RestAssured.request(Method.GET, globalUrl+"1");
        assertEquals(new Integer(1), response.path("id"));

        JsonPath jsonPath = new JsonPath(response.asString());
        assertEquals(1,jsonPath.getInt("id"));

        int id = JsonPath.from(response.asString()).getInt("id");
        assertEquals(1, id);

    }

    @Test
    public void verificarSegundoNivel(){
        given()
                .when()
                .get(globalUrl+"2")
                .then()
                .statusCode(200)
                .body("id", is(2))
                .body("name", containsString("Joaquina"))
                .body("endereco.rua", is("Rua dos bobos"));
    }

    @Test
    public void verificarLista(){
        given()
                .when()
                .get(globalUrl+"3")
                .then()
                .statusCode(200)
                .body("name", containsString("Ana"))
                .body("filhos", hasSize(2))
                .body("filhos[0].name", is("Zezinho"))
                .body("filhos[1].name" , is ("Luizinho"))
                .body("filhos.name", hasItems("Luizinho","Zezinho"))
        ;
    }

    @Test
    public void verificiarUsuarioInexistente(){
        given()
                .when()
                .get(globalUrl+"4")
                .then()
                .statusCode(404)
                .body("error", is("Usuário inexistente"))
        ;
    }

    @Test
    public void listaNaRaiz(){
        given()
                .when()
                .get(globalUrl)
                .then()
                .statusCode(200)
                .body("$", hasSize(3)) // $ faz a busca na raiz
                .body("name", hasItems("João da Silva", "Maria Joaquina", "Ana Júlia"))
                .body("age[1]" , is(25))
                .body("filhos.name", hasItem(Arrays.asList("Zezinho","Luizinho")))
                .body("salary" , contains(1234.5678f, 2500, null))
        ;
    }

    @Test
    public void validacoesAvançadasEmLista(){
        // findAll > retorna uma lista de objetos
        // find retona apenas 1 objeto
        // it faz referência ao objeto encontrado
        given()
                .when()
                .get(globalUrl)
                .then()
                .statusCode(200)
                .body("$", hasSize(3)) // $ faz a busca na raiz
                .body("age.findAll{it <= 25}.size()", is(2))
                .body("age.findAll{it <= 25 && it > 20}.size()", is(1))
                .body("findAll{it.age <= 25 && it.age > 20}.name", hasItem ("Maria Joaquina"))
                .body("findAll{it.age <= 25}[0].name", is ("Maria Joaquina"))
                .body("findAll{it.age <= 25}[-1].name", is ("Ana Júlia"))
                .body("findAll{it.name.contains('n')}.name", hasItems("Maria Joaquina", "Ana Júlia"))
                .body("findAll{it.name.length() > 10}.name", hasItems("João da Silva","Maria Joaquina"))
                .body("name.findAll{it.startsWith('Ana')}.collect{it.toUpperCase()}", hasItem("ANA JÚLIA"))
                .body("name.findAll{it.startsWith('Ana')}.collect{it.toUpperCase()}.toArray()", allOf(arrayContaining("ANA JÚLIA"), arrayWithSize(1)))
                .body("age.collect{it * 2}", hasItems(60,50,40))
                .body("id.max()", is(3))
                .body("salary.findAll{it != null}.sum()", is(closeTo(3734.5678f,0.001)))
                .body("salary.findAll{it != null}.sum()", allOf(greaterThan(3000d), lessThan(5000d)));
    }

    @Test
    public void jsonPathAddingJAVA(){
        ArrayList<String> namesStartingWithParameter =
                given()
                        .when()
                        .get(globalUrl)
                        .then()
                        .statusCode(200)
                        .extract().path("name.findAll{it.startsWith('Ana')}");

        assertEquals(1, namesStartingWithParameter.size());
        Assert.assertTrue(namesStartingWithParameter.get(0).equalsIgnoreCase("Ana Júlia"));
    }
}
