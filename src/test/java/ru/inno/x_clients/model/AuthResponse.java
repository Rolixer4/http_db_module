package ru.inno.x_clients.model;

public record AuthResponse(String userToken, String role, String displayName, String login) {
}

