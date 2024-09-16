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
import ru.inno.IntermediateCertification3.Model.Employee;
import ru.inno.IntermediateCertification3.Model.FullEmployee;
import ru.inno.IntermediateCertification3.Model.RandomEmployee;
import ru.inno.IntermediateCertification3.jpa.PUI;
import ru.inno.IntermediateCertification3.jpa.entity.CompanyEntity;
import ru.inno.IntermediateCertification3.jpa.entity.EmployeeEntity;

import javax.validation.constraints.AssertTrue;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class BusinessTests {
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
    @DisplayName("Получение списка сотрудников компании")
    public void iCanGetListOfEmployee() {
        EmployeeEntity testEmployee = entityManager.find(EmployeeEntity.class, helper.createNewEmployee(companyId, auth.userToken()));
        EmployeeEntity secondEmployee = entityManager.find(EmployeeEntity.class, helper.createNewEmployee(companyId, auth.userToken()));


        given()
                .basePath("employee")
                .contentType(ContentType.JSON)
                .queryParam("company", companyId)
                .when()
                .get()
                .then()
                .body("firstName", hasItem(testEmployee.getFirstName()))
                .body("firstName", hasItem(secondEmployee.getFirstName()))
                .body("lastName", hasItem(testEmployee.getLastName()))
                .body("lastName", hasItem(secondEmployee.getLastName()))
                .body("middleName", hasItem(testEmployee.getMiddleName()))
                .body("middleName", hasItem(secondEmployee.getMiddleName()))
                .body("birthdate", hasItem(testEmployee.getBirthdate()))
                .body("birthdate", hasItem(secondEmployee.getBirthdate()))
                .body("phone", hasItem(testEmployee.getPhone()))
                .body("phone", hasItem(secondEmployee.getPhone()))
                .body("isActive", hasItem(testEmployee.isActive()))
                .body("isActive", hasItem(secondEmployee.isActive()))
                .body("avatar_url", hasItem(testEmployee.getUrl()))
                .body("avatar_url", hasItem(secondEmployee.getUrl()))
                .body("email", hasItem(testEmployee.getEmail()))
                .body("email", hasItem(secondEmployee.getEmail()))
                .body("id", hasItem(testEmployee.getId()))
                .body("id", hasItem(secondEmployee.getId()));
    }

    @Test
    @DisplayName("Создание сотрудника")
    public void iCanCreateEmployee() {
        FullEmployee testEmployee = helper.createFullEmployee(companyId);

        int testId = given()
                .basePath("employee")
                .header("x-client-token", auth.userToken())
                .body(testEmployee)
                .contentType(ContentType.JSON)
                .when()
                .post()
                .as(FullEmployee.class).id();

        EmployeeEntity assertEmployee = entityManager.find(EmployeeEntity.class, testId);

        assertEquals(testEmployee.firstName(), assertEmployee.getFirstName());
        assertEquals(testEmployee.lastName(), assertEmployee.getLastName());
        assertEquals(testEmployee.middleName(), assertEmployee.getMiddleName());
        assertEquals(testEmployee.companyId(), assertEmployee.getCompanyId());
        assertEquals(testEmployee.email(), assertEmployee.getEmail());
        assertEquals(testEmployee.url(), assertEmployee.getUrl());
        assertEquals(testEmployee.phone(), assertEmployee.getPhone());
        assertEquals(testEmployee.isActive(), assertEmployee.isActive());
    }

    @Test
    @DisplayName("Получение сотрудника по id")
    public void iCanGetEmployeeById() {
        EmployeeEntity testEmployee = entityManager.find(EmployeeEntity.class, helper.createNewEmployee(companyId, auth.userToken()));

        given()
                .basePath("employee")
                .contentType(ContentType.JSON)
                .when()
                .get("{id}", testEmployee.getId())
                .then()
                .body("firstName", equalTo(testEmployee.getFirstName()))
                .body("lastName", equalTo(testEmployee.getLastName()))
                .body("middleName", equalTo(testEmployee.getMiddleName()))
                .body("birthdate", equalTo(testEmployee.getBirthdate()))
                .body("phone", equalTo(testEmployee.getPhone()))
                .body("isActive", equalTo(testEmployee.isActive()))
                .body("avatar_url", equalTo(testEmployee.getUrl()))
                .body("email", equalTo(testEmployee.getEmail()))
                .body("id", equalTo(testEmployee.getId()));
    }

    @Test
    @DisplayName("Изменение информации о сотруднике")
    public void iCanChangeEmployeeInfo() {
        RandomEmployee testEmployee = helper.randomEmployee(companyId);

        int testId = given()
                .basePath("employee")
                .header("x-client-token", auth.userToken())
                .contentType(ContentType.JSON)
                .body(testEmployee)
                .when()
                .patch("{id}", helper.createNewEmployee(companyId, auth.userToken()))
                .as(FullEmployee.class).id();

        EmployeeEntity assertEmployee = entityManager.find(EmployeeEntity.class, testId);

        assertEquals(testEmployee.lastName(), assertEmployee.getLastName());
        assertEquals(testEmployee.email(), assertEmployee.getEmail());
        assertEquals(testEmployee.url(), assertEmployee.getUrl());
        assertEquals(testEmployee.phone(), assertEmployee.getPhone());
        assertEquals(testEmployee.isActive(), assertEmployee.isActive());
    }

    @Test
    @DisplayName("Изменение всей информации о сотруднике")
    public void changeFullEmployeeInfo() {
        FullEmployee testEmployee = helper.createFullEmployee(companyId);

        int testId = given()
                .basePath("employee")
                .header("x-client-token", auth.userToken())
                .contentType(ContentType.JSON)
                .body(testEmployee)
                .when()
                .patch("{id}", helper.createNewEmployee(companyId, auth.userToken()))
                .as(FullEmployee.class).id();

        EmployeeEntity assertEmployee = entityManager.find(EmployeeEntity.class, testId);

        assertNotEquals(testEmployee.firstName(), assertEmployee.getFirstName());
        assertEquals(testEmployee.lastName(), assertEmployee.getLastName());
        assertNotEquals(testEmployee.middleName(), assertEmployee.getMiddleName());
        assertEquals(testEmployee.companyId(), assertEmployee.getCompanyId());
        assertEquals(testEmployee.email(), assertEmployee.getEmail());
        assertEquals(testEmployee.url(), assertEmployee.getUrl());
        assertEquals(testEmployee.phone(), assertEmployee.getPhone());
        assertEquals(testEmployee.isActive(), assertEmployee.isActive());
    }

    @Test
    @DisplayName("Получение пустого списка сотрудников")
    public void getEmptyEmployeeList() {
        given()
                .queryParam("company", companyId)
                .basePath("employee")
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .header("Content-Length", (String) null);
    }

    @Test
    @DisplayName("Получение сотрудника по несуществующему id")
    public void getNotExistedEmployee() {
        given()
                .basePath("employee")
                .contentType(ContentType.JSON)
                .when()
                .get("{id}", 1 - companyId)
                .then()
                .statusCode(404);
    }
}
