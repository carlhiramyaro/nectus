package org.example.nectus.feed.dto;

import jakarta.validation.constraints.NotBlank;

public record AddCommentRequest(
        @NotBlank(message = "Comment cannot be empty")
        String content
) {
}
