package oss_haluto.music_theory_app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode; // JsonNodeをインポート

import java.util.Base64;

@Service
public class SpotifyService {

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    // Spotifyの認証サーバーとAPIサーバーのためのWebClient
    private final WebClient authWebClient;
    private final WebClient apiWebClient;

    public SpotifyService(WebClient.Builder webClientBuilder) {
        // 認証用のWebClient
        this.authWebClient = webClientBuilder.baseUrl("https://accounts.spotify.com").build();
        // API呼び出し用のWebClient
        this.apiWebClient = webClientBuilder.baseUrl("https://api.spotify.com/v1").build();
    }

    // アクセストークンを取得するメソッド
    private String getAccessToken() {
        // "Basic " + "clientId:clientSecret" をBase64エンコードした文字列
        String credentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

        // リクエストボディの作成
        MultiValueMap<String, String> bodyValues = new LinkedMultiValueMap<>();
        bodyValues.add("grant_type", "client_credentials");

        // /api/token エンドポイントを呼び出す
        JsonNode responseJson = authWebClient.post()
                .uri("/api/token")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(bodyValues)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        // レスポンスのJSONからアクセストークンを取り出す
        return responseJson.get("access_token").asText();
    }

    // 特定の曲の情報を取得するメソッド
    public String getTrackInfo() {
        // Spotifyの曲ID（YOASOBI - アイドル）
        String trackId = "2_mKpmcTNEk2Y7iH3lP5l_";

        try {
            String accessToken = getAccessToken(); // まずアクセストークンを取得

            // Spotify APIの /v1/tracks/{id} エンドポイントを呼び出す
            String response = apiWebClient.get()
                    .uri("/tracks/" + trackId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // 認証ヘッダーを付与
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return "情報の取得に失敗しました: " + e.getMessage();
        }
    }
}