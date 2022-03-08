package asu.capstone.spota.services;

import asu.capstone.spota.model.News;
import asu.capstone.spota.model.NewsResult;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class NBAService {
    private static final Gson gson = new Gson();

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
            //newsResults = gson.fromJson(response.body(), ArrayList.class);
            NewsResult result = new NewsResult();
            result.setTeamName(teamName);
            result.setNewsList(Arrays.asList(newsList));
            newsResults.add(result);
        }

        return newsResults;
    }
}
