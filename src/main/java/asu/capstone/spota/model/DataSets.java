package asu.capstone.spota.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DataSets {
    List<String> Available = new ArrayList<>();
    List<Object> EastConfStandingsByDay = new ArrayList <Object>();
    List<Object> GameHeader = new ArrayList <Object>();
    List<Object> LastMeeting = new ArrayList <Object>();
    List<Object> LineScore = new ArrayList <Object>();
    List<Object> SeriesStandings = new ArrayList <Object>();
    List<Object> TeamLeaders = new ArrayList <Object>();
    List<Object> TicketLinks = new ArrayList <Object>();
    List<Object> WestConfStandingsByDay = new ArrayList <Object>();
    List<Object> WinProbability = new ArrayList <Object>();
}

