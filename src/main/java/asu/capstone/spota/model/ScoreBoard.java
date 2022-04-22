package asu.capstone.spota.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ScoreBoard {
    private String resource;
    private List<Object> parameters = new ArrayList<>();
    private List<ScoreBoardResultSet> resultSets = new ArrayList<>();
}
