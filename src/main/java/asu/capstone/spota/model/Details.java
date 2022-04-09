package asu.capstone.spota.model;

import lombok.Data;

@Data
public class Details {
    public String type;
    public String videoId;
    public String duration;
    public long viewCount;
    public long likeCount;
    public long dislikeCount;
    public long commentCount;
    public String publishedAt;
}
