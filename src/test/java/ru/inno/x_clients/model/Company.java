package ru.inno.x_clients.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Вариант #2 решения проблемы с неизвестными полями в JSON.
// Поставить над классом аннотацию @JsonIgnoreProperties(ignoreUnknown = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public record Company(int id, String name, String description, boolean isActive) {
}


