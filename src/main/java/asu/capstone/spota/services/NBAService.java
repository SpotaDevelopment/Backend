package asu.capstone.spota.services;

import asu.capstone.spota.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class NBAService {
    @Value("${spring.datasource.url}")
    private String DB_URL;

    @Value("${spring.datasource.username}")
    private String USER;

    @Value("${spring.datasource.password}")
    private String PASS;

    private static final Gson gson = new Gson();

    public String getImageUrlForNews(String url) throws IOException, InterruptedException{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://api.linkpreview.net/?key=2b434ee3e96620077f320912ef35cef7&q=" + url))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        ImageContent imageContent = gson.fromJson(response.body(), ImageContent.class);
        String image = imageContent.getImage();
        if(image == "" || image == null)
        {
            return "";
        }
        return image;
    }

    public ScoreBoard getGameScores() throws IOException, InterruptedException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localTime = LocalDate.now(ZoneId.of("GMT-7"));
        String scoresDate = dtf.format(localTime);
        System.out.println("scores Date: " + scoresDate);

        String uri = String.format("http://stats.nba.com/stats/scoreboardv2?DayOffset=0&GameDate=%s&LeagueID=00", scoresDate);
        System.out.println(uri);


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .header("Referer", "http://stats.nba.com/scores")
                //.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36")
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response);

        ScoreBoard scoreBoard = gson.fromJson(response.body(), ScoreBoard.class);

        System.out.println("Score Board: " + scoreBoard);

        return scoreBoard;
    }


    public List<NewsResult> getNews(List<String> teamSubscriptions) throws IOException, InterruptedException {
        List<NewsResult> newsResults = new ArrayList<>();

        for(String teamName : teamSubscriptions) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://nba-latest-news.p.rapidapi.com/news/team/" + teamName))
                    .header("x-rapidapi-host", "nba-latest-news.p.rapidapi.com")
                    .header("x-rapidapi-key", "26703b3174msh5b90d3485cd51ffp1f88dajsn3476ad069f04")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            News[] newsList = gson.fromJson(response.body(), News[].class);
            for(News obj : newsList)
            {
                obj.setImage(getImageUrlForNews(obj.getUrl()));
            }
            //newsResults = gson.fromJson(response.body(), ArrayList.class);
            NewsResult result = new NewsResult();
            result.setTeamName(teamName);
            result.setNewsList(Arrays.asList(newsList));
            newsResults.add(result);
        }

        return newsResults;
    }

    public News[] getGeneralNews() throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://nba-latest-news.p.rapidapi.com/news/source/espn"))
                .header("x-rapidapi-host", "nba-latest-news.p.rapidapi.com")
                .header("x-rapidapi-key", "da9149073emsh6df90e41f689663p115dffjsnf48aee52819c")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        News[] newsList = gson.fromJson(response.body(), News[].class);
        for(News obj : newsList) {
            obj.setImage(getImageUrlForNews(obj.getUrl()));
        }
        return newsList;
    }

    public List<Game> getScores(List<String> teamSubscriptions) {

        List<Game> gameScores = new ArrayList<>();

        try (Connection dbc = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = dbc.createStatement();) {

            for(String teamName : teamSubscriptions) {
                String sqlQuery = String.format("SELECT * FROM games where homeTeamName='%s' OR awayTeamName='%s';", teamName, teamName);

                //getting result set from DB
                ResultSet resultSet = stmt.executeQuery(sqlQuery);

                while (resultSet.next()) {
                    //adding teams to the subscriptions list
                    Game game = new Game();
                    game.setHomeTeamName(resultSet.getString("homeTeamName"));
                    game.setAwayTeamName(resultSet.getString("awayTeamName"));
                    game.setHomeTeamAbrv(resultSet.getString("homeTeamAbrv"));
                    game.setAwayTeamAbrv(resultSet.getString("awayTeamAbrv"));
                    game.setDate(resultSet.getString("date"));
                    game.setHomeTeamScore(Integer.parseInt(resultSet.getString("homeTeamScore")));
                    game.setAwayTeamScore(Integer.parseInt(resultSet.getString("awayTeamScore")));
		    game.setHomeTeamWins(Integer.parseInt(resultSet.getString("homeTeamWins")));
                    game.setHomeTeamLosses(Integer.parseInt(resultSet.getString("homeTeamLosses")));
                    game.setAwayTeamWins(Integer.parseInt(resultSet.getString("awayTeamWins")));
                    game.setAwayTeamLosses(Integer.parseInt(resultSet.getString("awayTeamLosses")));
                    gameScores.add(game);
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
            return null;
        }
        return gameScores;
    }

    public List<Game> getGeneralScores() throws IOException, InterruptedException {
        List<Game> gameScores = new ArrayList<>();

        try (Connection dbc = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = dbc.createStatement();) {

                String sqlQuery = String.format("SELECT * FROM games;");

                //getting result set from DB
                ResultSet resultSet = stmt.executeQuery(sqlQuery);

                while (resultSet.next()) {
                    //adding teams to the subscriptions list
                    Game game = new Game();

                    game.setHomeTeamName(resultSet.getString("homeTeamName"));
                    game.setAwayTeamName(resultSet.getString("awayTeamName"));
                    game.setHomeTeamAbrv(resultSet.getString("homeTeamAbrv"));
                    game.setAwayTeamAbrv(resultSet.getString("awayTeamAbrv"));
                    game.setDate(resultSet.getString("date"));
                    game.setHomeTeamScore(Integer.parseInt(resultSet.getString("homeTeamScore")));
                    game.setAwayTeamScore(Integer.parseInt(resultSet.getString("awayTeamScore")));
                    game.setHomeTeamWins(Integer.parseInt(resultSet.getString("homeTeamWins")));
                    game.setHomeTeamLosses(Integer.parseInt(resultSet.getString("homeTeamLosses")));
                    game.setAwayTeamWins(Integer.parseInt(resultSet.getString("awayTeamWins")));
                    game.setAwayTeamLosses(Integer.parseInt(resultSet.getString("awayTeamLosses")));

                    gameScores.add(game);
                }
            } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return gameScores;
    }
}
