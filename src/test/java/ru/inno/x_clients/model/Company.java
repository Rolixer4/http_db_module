package ru.inno.x_clients.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//@JsonIgnoreProperties(ignoreUnknown = true)
public record Company(int id, String name, String description, boolean isActive, String createDateTime, String lastChangedDateTime, String deletedAt) {
}
