package org.example.nectus.feed.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PostResponse(
        UUID id,
        UUID authorId,
        String authorName,
        String authorHeadline,
        String authorProfilePicture,
        String content,
        String imageUrl,
        long likeCount,
        long commentCount,
        boolean likedByCurrentUser,
        List<CommentResponse> comments,
        LocalDateTime createdAt
) {
}
