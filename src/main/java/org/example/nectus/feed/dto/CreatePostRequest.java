package org.example.nectus.feed.dto;

import lombok.Data;

@Data
public class CreatePostRequest {
    private String content;
    private String imageUrl;
}
