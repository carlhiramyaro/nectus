package org.example.nectus.feed;

import lombok.RequiredArgsConstructor;
import org.example.nectus.common.security.SecurityUtils;
import org.example.nectus.common.service.FileUploadService;
import org.example.nectus.connection.Connection;
import org.example.nectus.connection.ConnectionRepository;
import org.example.nectus.feed.dto.AddCommentRequest;
import org.example.nectus.feed.dto.CommentResponse;
import org.example.nectus.feed.dto.CreatePostRequest;
import org.example.nectus.feed.dto.PostResponse;
import org.example.nectus.job.dto.PageResponse;
import org.example.nectus.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final ConnectionRepository connectionRepository;
    private final FileUploadService fileUploadService;

    @Transactional
    public PostResponse createPost(String content, MultipartFile image){
        User currentUser = SecurityUtils.getCurrentUser();

        String imageUrl = null;

        if (image != null && !image.isEmpty()){
            imageUrl = fileUploadService.uploadImage(image, "nectus/posts");
        }
        Post post = Post.builder()
                .author(currentUser)
                .content(content)
                .imageUrl(imageUrl)
                .build();

        postRepository.save(post);
        return mapToPostResponse(post, currentUser.getId());

    }

    public PageResponse<PostResponse> getFeed(int page, int size){
        User currentUser = SecurityUtils.getCurrentUser();

        List<Connection> connections = connectionRepository
                .findAcceptedConnections(currentUser.getId());

        List<UUID> networkUserIds = connections.stream()
                .map(c -> c.getRequester().getId().equals(currentUser.getId())
                        ? c.getAddressee().getId()
                        : c.getRequester().getId())
                .collect(Collectors.toList());

        networkUserIds.add(currentUser.getId());

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findFeedForUsers(networkUserIds, pageRequest);

        List<PostResponse> content = postPage.getContent()
                .stream()
                .map(p -> mapToPostResponse(p, currentUser.getId()))
                .toList();

        return new PageResponse<>(
                content,
                postPage.getNumber(),
                postPage.getSize(),
                postPage.getTotalElements(),
                postPage.getTotalPages(),
                postPage.isLast()
        );

    }

    public PostResponse getPost(UUID postId){
        User currentUser = SecurityUtils.getCurrentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalStateException("Post not found"));
        return mapToPostResponse(post, currentUser.getId());
    }

    @Transactional
    public void deletePost(UUID postId){
        User currentUser = SecurityUtils.getCurrentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalStateException("Post not found"));

        if (!post.getAuthor().getId().equals(currentUser.getId())){
            throw new IllegalArgumentException("Not authorized to delete this post");
        }

        postRepository.delete(post);
    }

    @Transactional
    public boolean toggleLike(UUID postId){
        User currentUser = SecurityUtils.getCurrentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalStateException("Post not found"));

//        if already liked remove like
        if (postLikeRepository.existsByPostIdAndUser_Id(postId, currentUser.getId())){
            postLikeRepository.findByPostIdAndUser_Id(postId, currentUser.getId())
                    .ifPresent(postLikeRepository::delete);
            return false;
        }

        PostLike like = PostLike.builder()
                .post(post)
                .user(currentUser)
                .build();

        postLikeRepository.save(like);
        return true;

    }

    @Transactional
    public CommentResponse addComment(UUID postId, AddCommentRequest request){
        User currentUser = SecurityUtils.getCurrentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalStateException("Post not found"));

        Comment comment = Comment.builder()
                .post(post)
                .author(currentUser)
                .content(request.content())
                .build();

        commentRepository.save(comment);
        return mapToCommentResponse(comment);
    }

    @Transactional
    public void deleteComment(UUID commentId){
        User currentUser = SecurityUtils.getCurrentUser();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalStateException("Comment not found"));

        if (!comment.getAuthor().getId().equals(currentUser.getId())){
            throw new IllegalArgumentException("Not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }

    public PageResponse<PostResponse> getUserPosts(UUID userId, int page, int size){
        User currentUser = SecurityUtils.getCurrentUser();
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Post> postPage = postRepository
                .findByAuthorIdOrderByCreatedAtDesc(userId, pageRequest);

        List<PostResponse> content = postPage.getContent()
                .stream()
                .map(p -> mapToPostResponse(p, currentUser.getId()))
                .toList();

        return new PageResponse<>(
                content,
                postPage.getNumber(),
                postPage.getSize(),
                postPage.getTotalElements(),
                postPage.getTotalPages(),
                postPage.isLast()
        );
    }

    private PostResponse mapToPostResponse(Post post, UUID currentUserId){
        List<CommentResponse> comments = commentRepository
                .findByPostIdOrderByCreatedAtAsc(post.getId())
                .stream()
                .map(this::mapToCommentResponse)
                .toList();

        boolean likedByCurrentUser = postLikeRepository
                .existsByPostIdAndUser_Id(post.getId(), currentUserId);

        long likeCount = postLikeRepository.countByPostId(post.getId());
        long commentCount = commentRepository.countByPostId(post.getId());

        return new PostResponse(
                post.getId(),
                post.getAuthor().getId(),
                post.getAuthor().getFullName(),
                post.getAuthor().getHeadline(),
                post.getAuthor().getProfilePictureUrl(),
                post.getContent(),
                post.getImageUrl(),
                likeCount,
                commentCount,
                likedByCurrentUser,
                comments,
                post.getCreatedAt()
        );
    }

    private CommentResponse mapToCommentResponse(Comment comment){
        return new CommentResponse(
                comment.getId(),
                comment.getAuthor().getId(),
                comment.getAuthor().getFullName(),
                comment.getAuthor().getProfilePictureUrl(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
