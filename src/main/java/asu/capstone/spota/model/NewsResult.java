package asu.capstone.spota.model;

import lombok.Data;

import java.util.List;

@Data
public class NewsResult {
    private String teamName;
    private List<News> newsList;
}
