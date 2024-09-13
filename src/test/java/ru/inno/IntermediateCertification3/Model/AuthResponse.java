package ru.inno.IntermediateCertification3.Model;

public record AuthResponse(String userToken, String role, String displayName, String login) {
}
