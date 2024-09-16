package ru.inno.IntermediateCertification3;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.spi.PersistenceUnitInfo;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.junit.jupiter.api.*;
import ru.inno.IntermediateCertification3.Helper.EmployeeHelper;
import ru.inno.IntermediateCertification3.Model.AuthResponse;
import ru.inno.IntermediateCertification3.jpa.PUI;
import ru.inno.IntermediateCertification3.jpa.entity.CompanyEntity;
import ru.inno.IntermediateCertification3.jpa.entity.EmployeeEntity;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ContractTests {
    EmployeeHelper helper;
    static String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
    static String appConfigPath = rootPath + "env.properties";
    static Properties properties = new Properties();
    AuthResponse auth = EmployeeHelper.auth(properties.getProperty("app_user.login"), properties.getProperty("app_user.password"));
    private static EntityManager entityManager;
    int companyId;

    @BeforeAll
    public static void setUp() throws IOException {

        properties.load(new FileInputStream(appConfigPath));

        RestAssured.baseURI = properties.getProperty("ui.url");
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

        PersistenceUnitInfo pui = new PUI(properties);

        HibernatePersistenceProvider hibernatePersistenceProvider = new HibernatePersistenceProvider();
        EntityManagerFactory emf = hibernatePersistenceProvider.createContainerEntityManagerFactory(pui, pui.getProperties());
        entityManager = emf.createEntityManager();
    }

    @BeforeEach
    public void setUpB() {
        helper = new EmployeeHelper();
        CompanyEntity company = new CompanyEntity();
        company.setName("Тестовая компания");
        company.setDescription("Тестовое описание322");
        company.setActive(true);

        entityManager.getTransaction().begin();
        entityManager.persist(company);
        entityManager.getTransaction().commit();
        companyId = company.getId();
    }

    @AfterEach
    public void tearDown() {
        entityManager.getTransaction().begin();
        entityManager.createQuery("DELETE FROM EmployeeEntity ee WHERE ee.companyId = :companyId").setParameter("companyId", companyId).executeUpdate();
        entityManager.createQuery("DELETE FROM CompanyEntity ce WHERE ce.id = :companyId").setParameter("companyId", companyId).executeUpdate();
        entityManager.getTransaction().commit();
    }

    @Test
    @DisplayName("При запросе списка сотрудников статус-код 200 и Content-Type JSON")
    public void status200OnGetEmployees() {
        given()
                .log().all()
                .basePath("employee")
                .contentType(ContentType.JSON)
                .queryParam("company", companyId)
                .when()
                .get()
                .then()
                .statusCode(200)
                .contentType("application/json; charset=utf-8");
    }

    @Test
    @DisplayName("Проверка авторизации через токен")
    public void iCanAuth() {
        given()
                .basePath("auth/login")
                .body(properties.getProperty("app_user.login"))
                .body("{\n\"username\": \"" + properties.getProperty("app_user.login") + "\",\n\"password\": \"" + properties.getProperty("app_user.password") + "\"\n}")
                .contentType(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(201)
                .contentType("application/json; charset=utf-8 ")
                .body("userToken", is(not(blankString())));
    }

    @Test
    @DisplayName("При создании сотрудника статус-код 201 и Content-Type JSON")
    public void iCanCreateANewEmployee() {
        given()
                .basePath("employee")
                .header("x-client-token", auth.userToken())
                .body(helper.createFullEmployee(companyId))
                .contentType(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(201)
                .contentType("application/json; charset=utf-8 ");
    }

    @Test
    @DisplayName("При получении сотрудника по id статус-код 200 и Content-Type JSON")
    public void iCanGetEmployeeById() {
        given()
                .basePath("employee")
                .contentType(ContentType.JSON)
                .when()
                .get("{id}", helper.createNewEmployee(companyId, auth.userToken()))
                .then()
                .statusCode(200)
                .contentType("application/json; charset=utf-8 ");
    }

    @Test
    @DisplayName("При изменении информации о сотруднике статус-код 200 и Content-Type JSON")
    public void iCanChangeEmployeeInfo() {
        given()
                .basePath("employee")
                .header("x-client-token", auth.userToken())
                .contentType(ContentType.JSON)
                .body(helper.randomEmployee(companyId))
                .when()
                .patch("{id}", helper.createNewEmployee(companyId, auth.userToken()))
                .then()
                .statusCode(200)
                .contentType("application/json; charset=utf-8 ");
    }

    @Test
    @DisplayName("При получении сотрудника несуществующей компании статус-код 200 и Content-Type JSON")
    public void getListOfEmployeeNotExistedCompany() {
        given()
                .log().all()
                .basePath("employee")
                .contentType(ContentType.JSON)
                .queryParam("company", 1 - companyId)
                .when()
                .get()
                .then()
                .statusCode(200)
                .contentType("application/json; charset=utf-8");
    }

    @Test
    @DisplayName("При создании сотрудника в несуществующей компании статус-код 500 и Content-Type JSON")
    public void createEmployeeNotExistedCompany() {
        given()
                .basePath("employee")
                .header("x-client-token", auth.userToken())
                .body(helper.createFullEmployee(1 - companyId))
                .contentType(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(500)
                .contentType("application/json; charset=utf-8 ");
    }

    @Test
    @DisplayName("При создании сотрудника без авторизационного токена статус-код 401 и Content-Type JSON")
    public void createEmployeeWithoutToken() {
        given()
                .basePath("employee")
                .body(helper.createFullEmployee(companyId))
                .contentType(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(401)
                .contentType("application/json; charset=utf-8 ");
    }

    @Test
    @DisplayName("При запросе несуществующего сотрудника статус-код 404 и Content-Type JSON")
    public void getEmployeeWithNotExistedId() {
        given()
                .basePath("employee")
                .contentType(ContentType.JSON)
                .when()
                .get("{id}", 1 - companyId)
                .then()
                .statusCode(404)
                .contentType("application/json; charset=utf-8 ");
    }

    @Test
    @DisplayName("При изменении информации о несуществующем сотруднике статус-код 404 и Content-Type JSON")
    public void changeEmployeeInfoNotExistedId() {
        given()
                .basePath("employee")
                .header("x-client-token", auth.userToken())
                .contentType(ContentType.JSON)
                .body(helper.randomEmployee(companyId))
                .when()
                .patch("{id}", 1 - companyId)
                .then()
                .statusCode(404)
                .contentType("application/json; charset=utf-8 ");
    }

    @Test
    @DisplayName("При изменении всей информации о сотруднике статус-код 200 и Content-Type JSON")
    public void changeFullEmployeeInfo() {
        given()
                .basePath("employee")
                .header("x-client-token", auth.userToken())
                .contentType(ContentType.JSON)
                .body(helper.createFullEmployee(companyId))
                .when()
                .patch("{id}", helper.createNewEmployee(companyId, auth.userToken()))
                .then()
                .statusCode(200)
                .contentType("application/json; charset=utf-8 ");
    }
}