package com.core;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;

public class BaseTest implements Constants{
    @BeforeClass
    public static void setup(){
//      Setting baseURI, basePath and port with default values
        RestAssured.baseURI = APP_URL_BASE;
        RestAssured.basePath = APP_BASE_PATH;
        RestAssured.port = APP_PORT;

//      Setting "JSON" as default contet type in Request
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setContentType(APP_CONTENT_TYPE);
        RestAssured.requestSpecification = requestSpecBuilder.build();

//      Setting the application to wait less time than "MAX_TIMEOUT" constant
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectResponseTime(Matchers.lessThan(MAX_TIMEOUT));
        RestAssured.responseSpecification = responseSpecBuilder.build();

//      If fails, shows the log
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
}
