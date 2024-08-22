package ru.inno.x_clients;

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.nullValue;


public class JSONSchemaTest {

    @Test
    public void checkResponseBody() {
        given()
                .baseUri("https://x-clients-be.onrender.com")
                .basePath("company")
                .pathParam("id", 3376)
                .when().get("/{id}")
                .then()
                .body("id", isA(Integer.class))
                .body("isActive", isA(Boolean.class))
                .body("createDateTime", isA(String.class))
                .body("lastChangedDateTime", isA(String.class))
                .body("name", isA(String.class))
                .body("description", isA(String.class))
                .body("deletedAt", nullValue());
    }

    @Test
    public void checkBySchema() {
        String SWAGGER = "https://x-clients-be.onrender.com/docs-json";

        given()
                .filters(
                        new RequestLoggingFilter(),
                        new ResponseLoggingFilter(),
                        new OpenApiValidationFilter(SWAGGER)
                )
                .baseUri("https://x-clients-be.onrender.com")
                .basePath("company")
                .pathParam("id", 3376)
                .when().get("/{id}")
                .then()
                .statusCode(200);
    }
}