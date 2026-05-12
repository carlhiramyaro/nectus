package org.example.nectus.connection.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ConnectionRequest(
        @NotNull(message = "Target user Id is required")
        UUID targetUserId
) {
}
