package ru.inno.x_clients.helper;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.inno.x_clients.model.*;

import java.util.Optional;

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

    public Optional<Company> getById(int id) throws InterruptedException {
        Thread.sleep(3000);
        Response response = given()
                .basePath("company")
                .pathParam("id", id)
                .when().get("/{id}");

        String header = response.header("Content-Length");
        if (header != null && header.equals("0")){
            return Optional.empty();

        }

        Company company = response.as(Company.class);
        return Optional.of(company);
    }

}
