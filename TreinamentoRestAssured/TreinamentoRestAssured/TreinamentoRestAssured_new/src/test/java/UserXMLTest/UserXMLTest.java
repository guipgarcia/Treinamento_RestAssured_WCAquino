package UserXMLTest;


import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.internal.path.xml.NodeImpl;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

public class UserXMLTest {
    private String globalUrl = "http://restapi.wcaquino.me/usersXML";
    private static RequestSpecification requestSpecification;
    private static ResponseSpecification responseSpecification;

    @BeforeClass
    public static void setup(){
        RestAssured.baseURI = "https://restapi.wcaquino.me"; // Caminho base
//        RestAssured.port = 80; // 80 se for http, https = 443  -- não precisa ser colocado
//        RestAssured.basePath = "v2";

        RequestSpecBuilder specBuilder = new RequestSpecBuilder();
        specBuilder.log(LogDetail.ALL);
        requestSpecification = specBuilder.build();

        ResponseSpecBuilder responseBuilder = new ResponseSpecBuilder();
        responseBuilder.expectStatusCode(200);
        responseSpecification = responseBuilder.build();

        RestAssured.responseSpecification = responseSpecification;
        RestAssured.requestSpecification = requestSpecification;

    }
    @Test
    public void devoTrabalharComXML(){
        // Para o XML todos os valores são Strings
     given().
        when().
                get("usersXML/3").
         then().
//                statusCode(200).
                body("user.name", is("Ana Julia")).
                body("user.@id", is("3")).
                body("user.filhos.name.size()", is(2)).
                body("user.filhos.name[0]", is("Zezinho")).
                body("user.filhos.name[1]", is("Luizinho")).
                body("user.filhos.name", hasItem("Luizinho")). //Trabalhando com coleção
                body("user.filhos.name", hasItems("Zezinho", "Luizinho")); // Trabalhando com coleção - Tanto faz a ordem dos dados
    }

    @Test
    public void utilizandoNoRaiz(){
       // O caminho definido pelo rootpath serve de atalho para conseguirmos acessar seus filhos. Podemos usar como exemplo a importação estática de elementos no java.

        given().
        when().
                get("usersXML/3").
        then().
//                statusCode(200).
                rootPath("user.filhos"). // utilizando isso a navegação para achar filhos não precisa mais da indicação do pai
                body("name", hasItem("Luizinho")).
                body("name", hasItem("Zezinho")).
                body("name", hasItems("Zezinho","Luizinho")).

                detachRootPath("filhos"). // Retira o "filhos" da raiz, ou seja: agora para entrarmos nesse metodo precisaremos declará-lo novamente
                body("filhos.name", hasItem("Luizinho")).
                body("filhos.name", hasItem("Zezinho")).

                appendRootPath("filhos"). // Insere um nó filho ao nó raiz já existente. Nesse caso foi o nó "filhos"
                body("name", hasItems("Zezinho","Luizinho"));
        ;
    }

    @Test
    public void pesquisasAvancadasComXML(){
        given().
        when().
                get("usersXML").
        then().
//                statusCode(200).
                rootPath("Users").
                body("user.size()", is (3)).
                body("user.findAll{it.age.toInteger() <=25}.size()", is (2)).  //it = iterador
                body("user.@id", hasItems("1","2", "3")).
                body("user.find{it.age == 25}.name", is("Maria Joaquina")).
                body("user.findAll{it.name.toString().contains('n')}.name", hasItems("Maria Joaquina", "Ana Julia")). //Sempre espcificar o campo após os {}
                body("user.salary.find{it != null}.toDouble()", is(1234.5678d)).
                body("user.age.collect{it.toInteger() * 2}", hasItems(40,50,60)).
                body("user.name.findAll{it.toString().startsWith('Maria')}.collect{it.toString().toUpperCase()}", is("MARIA JOAQUINA"));
    }

    @Test
    public void xmlPathComJava(){
        ArrayList<NodeImpl> users = // a lista retornada não é string, mas nodeImp
                given().
                when().
                    get("usersXML").
                then().
//                    statusCode(200).
                    extract().path("users.user.name.findAll{it.toString().contains('n')}");

        assertEquals(2, users.size());
        assertEquals("Maria Joaquina", users.get(0).toString());
        assertEquals("Ana Julia", users.get(1).toString());
        System.out.println(users);

    }

    @Test
    public void pesquisasComXpath(){
        // Rosetta Stone xpath

        given().
        when().
                get("usersXML").
        then().
//                statusCode(200).
                body(hasXPath("count(/users/user)", is("3"))).
                body(hasXPath("/users/user[@id = '1']")).
                body(hasXPath("//user[@id = '1']")).// Simplificando o xpath da linha acima
                body(hasXPath("//name[text() = 'Zezinho']/parent::filhos/parent::user/name", is ("Ana Julia"))). // Recebendo nome da mãe a partir dos filhos
                body(hasXPath("//name[text() = 'Ana Julia']/following-sibling::filhos"), allOf(containsString("Zezinho"), containsString("Luizinho"))). // Following-sibling faz você descer o nível para o indicado depois do ::
                body(hasXPath("//user[last()]/name", is ("Ana Julia"))).
                body(hasXPath("count(//user/name[contains(.,'n')])", is("2"))).
                body(hasXPath("//user[age < 24]/name", is("Ana Julia"))).
                body(hasXPath("//user[age < 30] [age >20]/name", is("Maria Joaquina"))).
                body(hasXPath("//user[age >= 30]/name", is("João da Silva")))
                ;
    }

    @Test
    public void xmlComBaseUriBasePath(){
        given().
                log().all().
        when().
                get("/usersXML").
        then().
                statusCode(200);

    }

    @Test
    public void requestAndResponse(){
     /*   RequestSpecBuilder specBuilder = new RequestSpecBuilder();
        specBuilder.log(LogDetail.ALL);
        RequestSpecification build = specBuilder.build();

        ResponseSpecBuilder responseBuilder = new ResponseSpecBuilder();
        responseBuilder.expectStatusCode(200);
        ResponseSpecification responseSpecification = responseBuilder.build();
       */

        /*
        * Usando os "specs" eu consigo especificar pré condições para o meu given e meu then: pegar o log completo, validar o status code e alguma validação do body, por exemplo.
        * Colocando especificando o spec utilizando o restassured, todos os testes herdam.
        * No caso acima, todos foram movidos para o BEFORE.
        * */

        given().
        when().
                get("/usersXML").
        then();

    }
}
