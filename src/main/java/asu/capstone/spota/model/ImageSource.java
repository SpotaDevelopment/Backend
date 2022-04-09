package asu.capstone.spota.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ImageSource {
    private String url;
    private String domain;
    private String lastUpdated;
    private String nextUpdate;
    private String contentType;
    private String mimeType;
    private boolean redirected;
    private String redirectionUrl;
    private float redirectionCount;
    List<String> redirectionTrail = new ArrayList<String>();
    private String title;
    private String description;
    private String name;
    private boolean trackersDetected;
    Icon icon;
    Image image;
    Details details;
}
