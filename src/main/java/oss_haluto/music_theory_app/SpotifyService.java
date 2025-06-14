package oss_haluto.music_theory_app;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Base64;

@Service
public class SpotifyService {

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    private final WebClient authWebClient;
    private final WebClient apiWebClient;

    public SpotifyService(WebClient.Builder webClientBuilder) {
        this.authWebClient = webClientBuilder.baseUrl("https://accounts.spotify.com").build();
        this.apiWebClient = webClientBuilder.baseUrl("https://api.spotify.com/v1").build();
    }

    // Authorization Codeを使ってアクセストークンを取得する新しいメソッド
    public String getAccessTokenFromCode(String code) {
        System.out.println("--- Authorization Codeを使ってアクセストークンを取得します ---");
        String redirectUri = "http://127.0.0.1:8080/callback";
        String credentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

        MultiValueMap<String, String> bodyValues = new LinkedMultiValueMap<>();
        bodyValues.add("grant_type", "authorization_code");
        bodyValues.add("code", code);
        bodyValues.add("redirect_uri", redirectUri);

        try {
            JsonNode responseJson = authWebClient.post()
                    .uri("/api/token")
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(bodyValues)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
            
            String accessToken = responseJson.get("access_token").asText();
            System.out.println("ユーザー用アクセストークン取得成功！");
            return accessToken;
        } catch (WebClientResponseException e) {
            System.err.println("!!! アクセストークン交換でエラー発生 !!!");
            System.err.println("エラーレスポンス: " + e.getResponseBodyAsString());
            return null;
        }
    }

    // アクセストークンを使って曲情報を取得するように修正
    public String getTrackInfo(String accessToken) {
        System.out.println("\n--- 曲情報取得開始 ---");
        String trackId = "4u7EneVyA5o2s8rg4f4B28"; // Queen - "Bohemian Rhapsody"
        System.out.println("リクエストするTrack ID: " + trackId);

        try {
            String response = apiWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                        .path("/tracks/{id}")
                        .queryParam("market", "JP")
                        .build(trackId))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            System.out.println("APIリクエスト成功！");
            return response;

        } catch (WebClientResponseException e) {
            System.err.println("!!! 曲情報取得でエラー発生 !!!");
            System.err.println("エラーコード: " + e.getStatusCode());
            System.err.println("エラーレスポンス: " + e.getResponseBodyAsString());
            return "情報の取得に失敗しました: " + e.getResponseBodyAsString();
        }
    }
}