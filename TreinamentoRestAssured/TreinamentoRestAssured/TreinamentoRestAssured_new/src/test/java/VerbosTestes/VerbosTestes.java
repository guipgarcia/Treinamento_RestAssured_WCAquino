package VerbosTestes;

import Users.Users;
import io.restassured.http.ContentType;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class VerbosTestes {
    private String globalUrl = "http://restapi.wcaquino.me/users/";
    private String globalUrlXML = "http://restapi.wcaquino.me/usersXML/";
    @Test
    public void devoSalvarUmUser(){
        given().
                log().all().
                contentType("application/json").
                body("{\"name\":\"Jose\",\"age\": 50}"). // ao enviar um objeto para a requisição, tem que informar o tipo dele
        when().
                post(globalUrl).
        then().
                log().all().
                statusCode(201).//201 criação de um recurso
                body("id", is(notNullValue())).
                body("name", is("Jose")).
                body("age", is(50))
        ;
    }

    @Test
    public void naoDeveSalvarUserSemNome(){
        given().
                log().all().
                    contentType("application/json").
                    body("{\"age\": 50}"). // ao enviar um objeto para a requisição, tem que informar o tipo dele
                when().
                    post(globalUrl).
                then().
                    log().all().
                    statusCode(400).//400 bad request
                    body("id", is(nullValue())).
                    body("error", is("Name é um atributo obrigatório"))
        ;
    }

    @Test
    public void devoSalvarUmUserXML(){
        given().
                log().all().
                    contentType(ContentType.XML).
                    body("<user><name>Jose</name><age>50</age></user>"). // ao enviar um objeto para a requisição, tem que informar o tipo dele
                when().
                    post(globalUrlXML).
                then().
                    log().all().
                    statusCode(201).//201 criação de um recurso
                    body("user.@id", is(notNullValue())).
                    body("user.name", is("Jose")).
                    body("user.age", is("50"))
        ;
    }

    @Test
    public void devoAlterarUmUser(){
        given().
                log().all().
                contentType("application/json").
                body("{\"name\":\"Usuario Alterado\",\"age\": 200}"). // ao enviar um objeto para a requisição, tem que informar o tipo dele
        when().
                put(globalUrl+"1").
        then().
                log().all().
                statusCode(200).//200 sucesso na alteração do recurso
                body("id", is(1)).
                body("name", is("Usuario Alterado")).
                body("age", is(200))
        ;
    }

    @Test
    public void urlParametrizavel(){
        given().
                log().all().
                contentType("application/json").
                body("{\"name\":\"Usuario Alterado\",\"age\": 200}"). // ao enviar um objeto para a requisição, tem que informar o tipo dele
                pathParam("field", "Users").
                pathParam("uid","1").
        when().
                put("http://restapi.wcaquino.me/{field}/{uid}").//{} elementos dentro das chaves são parametros e podemos definir seus values pelo pathParam
         then().
                log().all().
                statusCode(200).//200 sucesso na alteração do recurso
                body("id", is(1)).
                body("name", is("Usuario Alterado")).
                body("age", is(200))
        ;
    }

    @Test
    public void devoRemoverUmUser(){
        given().
                log().all().
//                contentType("application/json").
//                body("{\"name\":\"Usuario Alterado\",\"age\": 200}"). // ao enviar um objeto para a requisição, tem que informar o tipo dele
                pathParam("field", "Users").
                pathParam("uid","1").
        when().
                delete("http://restapi.wcaquino.me/{field}/{uid}").//{} elementos dentro das chaves são parametros e podemos definir seus values pelo pathParam
        then().
                log().all().
                statusCode(204)//.//204 no content
//                body("id", is(1)).
//                body("name", is("Usuario Alterado")).
//                body("age", is(200))
        ;
    }

    @Test
    public void devoSalvarUmUserMap(){
        Map<String, Object> restParameters = new HashMap<String, Object>();
        restParameters.put("name","Nome do usuario");
        restParameters.put("age", 30);

        given().
                log().all().
                contentType("application/json").
                body(restParameters). // ao enviar um objeto para a requisição, tem que informar o tipo dele
        when().
                post(globalUrl).
        then().
                log().all().
                statusCode(201).//201 criação de um recurso
                body("id", is(notNullValue())).
                body("name", is("Nome do usuario")).
                body("age", is(30))
        ;
    }

    @Test
    public void devoSalvarUmUserObject(){
        Users newUser = new Users("Bafranio", 40);
        given().
                log().all().
                contentType("application/json").
                body(newUser). // ao enviar um objeto para a requisição, tem que informar o tipo dele
                when().
                post(globalUrl).
                then().
                log().all().
                statusCode(201).//201 criação de um recurso
                body("id", is(notNullValue())).
                body("name", is("Bafranio")).
                body("age", is(40))
        ;
    }

    @Test
    public void devoDeserializarUmObjeto(){
        Users newUser = new Users("User Deserializado", 26);
        Users userInserido=
            given().
                    log().all().
                    contentType("application/json").
                    body(newUser). // ao enviar um objeto para a requisição, tem que informar o tipo dele
            when().
                    post(globalUrl).
            then().
                    log().all().
                    statusCode(201).//201 criação de um recurso
                    extract().body().as(Users.class) // extrai no formato indicado no `as` que nesse caso sera a classe. Como sera extraido, alguem deve receber.
            ;

        Assert.assertEquals("User Deserializado", userInserido.getName());
        Assert.assertThat(userInserido.getAge(), is(26));
    }

    @Test
    public void deveDeserializarXML(){
        Users userXML = new Users("Arnaldo", 98);
        Users userXMLretornado =
        given().
                log().all().
                contentType(ContentType.XML).
                body(userXML). // ao enviar um objeto para a requisição, tem que informar o tipo dele
        when().
                post(globalUrlXML).
        then().
                log().all().
                statusCode(201).//201 criação de um recurso
                extract().body().as(Users.class);
        ;
        Assert.assertThat(userXMLretornado.getId(), notNullValue());
        Assert.assertEquals("Arnaldo", userXMLretornado.getName());
        Assert.assertThat(userXMLretornado.getAge(), is(98));
    }
}
