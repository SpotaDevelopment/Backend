package asu.capstone.spota.model;

import lombok.Data;

@Data
public class Details {
    private String type;
    private String videoId;
    private String duration;
    private long viewCount;
    private long likeCount;
    private long dislikeCount;
    private long commentCount;
    private String publishedAt;
}
