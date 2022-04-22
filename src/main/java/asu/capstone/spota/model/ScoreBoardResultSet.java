package asu.capstone.spota.model;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class ScoreBoardResultSet {
    private String name;
    private List<String> headers;
    private List<List<Object>> rowSet;
}
