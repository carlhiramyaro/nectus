package org.example.nectus.feed.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        UUID authorId,
        String authorName,
        String authorProfilePicture,
        String content,
        LocalDateTime createdAt

) {
}
