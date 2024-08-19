package ru.inno.x_clients;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class CompanyContractTest {

    @BeforeAll
    public static void setUp(){
        RestAssured.baseURI = "https://x-clients-be.onrender.com";
    }

    @Test
    public void status200OnGetCompanies() {
        given()
                .header("ABC", "123")
                .basePath("company")
                .when()
                .get()
                .then()
                .statusCode(200)
                .header("Content-Type", "application/json; charset=utf-8");
    }

    @Test
    public void iCanAuth(){
        String body = """
                {
                      "username": "leonardo",
                      "password": "leads"
                }
                """;

        given()
                .basePath("/auth/login")
                .body(body)
                .contentType(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("userToken", is(not((blankString()))));
    }

    @Test
    public void iCanCreatNewCompany(){
        String authBody = """
                {
                  "username": "leonardo",
                  "password": "leads"
                }
                """;

        String createBody = """
                {
                  "name": "Inno",
                  "description": "курс aqa java"
                }
                """;

        // получить токен
        Response response = given()
                .basePath("/auth/login")
                .body(authBody)
                .contentType(ContentType.JSON)
                .when()
                .post();

        String token = response.jsonPath().getString("userToken");

        // создать компанию
        given()
                .basePath("company")
                .body(createBody)
                .header("x-client-token", token)
                .contentType(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("id", is(greaterThan(0)));
    }

}
