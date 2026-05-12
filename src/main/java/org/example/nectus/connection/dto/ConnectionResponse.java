package org.example.nectus.connection.dto;

import org.example.nectus.connection.ConnectionStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ConnectionResponse(
        UUID connectionId,
        UUID userId, //The other user's details
        String fullName,
        String headline,
        String profilePictureUrl,
        ConnectionStatus status,
        boolean iAmTheRequester,
        LocalDateTime createdAt
) {
}
