package ru.inno.x_clients.helper;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.inno.x_clients.model.AuthRequest;
import ru.inno.x_clients.model.AuthResponse;
import ru.inno.x_clients.model.CreateCompanyRequest;
import ru.inno.x_clients.model.CreateCompanyResponse;

import static io.restassured.RestAssured.given;

public class CompanyApiHelper {

    public AuthResponse auth(String username, String password) {
        AuthRequest authRequest = new AuthRequest(username, password);

        return given()
                .basePath("/auth/login")
                .body(authRequest)
                .contentType(ContentType.JSON)
                .when()
                .post()
                .as(AuthResponse.class);
    }

    public void printCompanyInfo(int id) {
        given()
                .basePath("company")
                .when()
                .get("{companyId}", id)
                .body().prettyPrint();
    }

    public CreateCompanyResponse createCompany(String name, String descr) {
        AuthResponse info = auth("leonardo", "leads");

        CreateCompanyRequest createCompanyRequest = new CreateCompanyRequest(name, descr);

        return given()
                .basePath("company")
                .body(createCompanyRequest)
                .header("x-client-token", info.userToken())
                .contentType(ContentType.JSON)
                .when()
                .post().body().as(CreateCompanyResponse.class);

    }

    public Response deleteCompany(int id){
        AuthResponse info = auth("leonardo", "leads");

        return given()
                .basePath("company/delete")
                .header("x-client-token", info.userToken())
                .contentType(ContentType.JSON)
                .when()
                .get("{id}", id);

    }
}
