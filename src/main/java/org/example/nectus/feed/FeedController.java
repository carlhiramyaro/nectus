package org.example.nectus.feed;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.nectus.feed.dto.AddCommentRequest;
import org.example.nectus.feed.dto.CommentResponse;
import org.example.nectus.feed.dto.CreatePostRequest;
import org.example.nectus.feed.dto.PostResponse;
import org.example.nectus.job.dto.PageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @PostMapping(value = "/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> createPost(
            @RequestParam(required = false) String content,
            @RequestParam(required = false) MultipartFile image
            ){

        if((content == null || content.isBlank()) && (image == null || image.isEmpty())){
            throw new IllegalArgumentException("Post must have content or an image");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(feedService.createPost(content, image));
    }

    @GetMapping
    public ResponseEntity<PageResponse<PostResponse>> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(feedService.getFeed(page, size));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable UUID postId){
        return ResponseEntity.ok(feedService.getPost(postId));
    }

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<Boolean> toggleLike(@PathVariable UUID postId){
        return ResponseEntity.ok(feedService.toggleLike(postId));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable UUID postId,
            @Valid @RequestBody AddCommentRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(feedService.addComment(postId, request));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId){
        feedService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{userId}/posts")
    public ResponseEntity<PageResponse<PostResponse>> getUserPosts(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(feedService.getUserPosts(userId, page, size));
    }

}
