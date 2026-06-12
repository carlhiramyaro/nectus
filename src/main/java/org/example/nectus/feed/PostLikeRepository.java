package org.example.nectus.feed;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PostLikeRepository extends JpaRepository<PostLike, UUID> {

    boolean existsByPostIdAndUser_Id(UUID postId, UUID userId);

    Optional<PostLike> findByPostIdAndUser_Id(UUID postId, UUID userId);

    long countByPostId(UUID postId);
}
