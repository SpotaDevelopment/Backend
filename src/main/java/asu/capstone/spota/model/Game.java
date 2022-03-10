package asu.capstone.spota.model;

import lombok.Data;

@Data
public class Game {
    String homeTeamName;
    String awayTeamName;
    String homeTeamAbrv;
    String awayTeamAbrv;
    String date;
    int homeTeamScore;
    int awayTeamScore;
    int homeTeamWins;
    int homeTeamLosses;
    int awayTeamWins;
    int awayTeamLosses;
}
