package ru.inno.IntermediateCertification3.Model;

import org.hibernate.type.descriptor.java.DateJavaType;

import java.util.Date;

public record FullEmployee(int id, String firstName, String lastName, String middleName, int companyId, String email, String url, String phone, boolean isActive) {
}
