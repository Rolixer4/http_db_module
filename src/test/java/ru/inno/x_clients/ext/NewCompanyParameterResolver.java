package ru.inno.x_clients.ext;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import ru.inno.x_clients.helper.CompanyApiHelper;
import ru.inno.x_clients.model.CreateCompanyResponse;

public class NewCompanyParameterResolver implements ParameterResolver {
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(CreateCompanyResponse.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        CompanyApiHelper helper = new CompanyApiHelper();
        return helper.createCompany("Temp company", "Компания, которая будет удалена");
    }
}
