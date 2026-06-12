package org.example.nectus.feed;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

//    Get posts from a list of user IDs, newest first. Fan out read approach
    @Query("""
SELECT p FROM Post p
WHERE p.author.id IN :userIds
ORDER BY p.createdAt DESC
""")
    Page<Post> findFeedForUsers(
            @Param("userIds") List<UUID> userIds,
            Pageable pageable
    );

    Page<Post> findByAuthorIdOrderByCreatedAtDesc(UUID authorId, Pageable pageable);
}
