package ru.inno.x_clients;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.inno.x_clients.ext.ApiHelperParameterResolver;
import ru.inno.x_clients.ext.NewCompanyParameterResolver;
import ru.inno.x_clients.helper.CompanyApiHelper;
import ru.inno.x_clients.model.Company;
import ru.inno.x_clients.model.CreateCompanyResponse;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith({ApiHelperParameterResolver.class, NewCompanyParameterResolver.class})
public class CompanyBusinessTest {

    @BeforeAll
    public static void setUp() {

        //TODO: ЗАБОРОТЬ переопределние конфига ObjectMapper
        //new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        RestAssured.baseURI = "https://x-clients-be.onrender.com";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void iCanDeleteCompany(CompanyApiHelper helper, CreateCompanyResponse newCompany) throws InterruptedException {
        helper.deleteCompany(newCompany.id());
        Optional<Company> optional = helper.getById(newCompany.id());
        assertFalse(optional.isPresent());
    }
}
