package asu.capstone.spota.model;

import lombok.Data;

import javax.swing.*;
import java.util.ArrayList;

@Data
public class ImageSource<Details> {
    private String url;
    private String domain;
    private String lastUpdated;
    private String nextUpdate;
    private String contentType;
    private String mimeType;
    private boolean redirected;
    private String redirectionUrl;
    private float redirectionCount;
    ArrayList<Object> redirectionTrail = new ArrayList<Object>();
    private String title;
    private String description;
    private String name;
    private boolean trackersDetected;
    Icon icon;
    Image image;
    Details details;
}
