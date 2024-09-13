package ru.inno.IntermediateCertification3.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EmployeeResponse(int id) {
}
