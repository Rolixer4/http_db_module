package ru.inno.x_clients;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith({ApiHelperParameterResolver.class, NewCompanyParameterResolver.class})
public class CompanyBusinessTest {

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://x-clients-be.onrender.com";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        // Вариант #3 решения проблемы с неизвестными полями в JSON.
        // Переписать конфиг RestAssured. Подложить свой ObjectMapper с настройками
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        RestAssured.config = RestAssured.config().objectMapperConfig(
                objectMapperConfig().jackson2ObjectMapperFactory((type, s) -> mapper)
        );
    }

    @Test
    public void iCanDeleteCompany(CompanyApiHelper helper, CreateCompanyResponse newCompany) throws InterruptedException {
        helper.deleteCompany(newCompany.id());
        Optional<Company> optional = helper.getById(3376);
        assertFalse(optional.isPresent());
    }
}
