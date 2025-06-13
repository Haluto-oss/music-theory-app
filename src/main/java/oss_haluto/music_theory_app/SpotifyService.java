// package oss_haluto.music_theory_app;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.MediaType;
// import org.springframework.stereotype.Service;
// import org.springframework.util.LinkedMultiValueMap;
// import org.springframework.util.MultiValueMap;
// import org.springframework.web.reactive.function.client.WebClient;
// import com.fasterxml.jackson.databind.JsonNode; // JsonNodeをインポート

// import java.util.Base64;

// @Service
// public class SpotifyService {

//     @Value("${spotify.client.id}")
//     private String clientId;

//     @Value("${spotify.client.secret}")
//     private String clientSecret;

//     // Spotifyの認証サーバーとAPIサーバーのためのWebClient
//     private final WebClient authWebClient;
//     private final WebClient apiWebClient;

//     public SpotifyService(WebClient.Builder webClientBuilder) {
//         // 認証用のWebClient
//         this.authWebClient = webClientBuilder.baseUrl("https://accounts.spotify.com").build();
//         // API呼び出し用のWebClient
//         this.apiWebClient = webClientBuilder.baseUrl("https://api.spotify.com/v1").build();
//     }

//     // アクセストークンを取得するメソッド
//     private String getAccessToken() {
//         // "Basic " + "clientId:clientSecret" をBase64エンコードした文字列
//         String credentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

//         // リクエストボディの作成
//         MultiValueMap<String, String> bodyValues = new LinkedMultiValueMap<>();
//         bodyValues.add("grant_type", "client_credentials");

//         // /api/token エンドポイントを呼び出す
//         JsonNode responseJson = authWebClient.post()
//                 .uri("/api/token")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
//                 .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                 .bodyValue(bodyValues)
//                 .retrieve()
//                 .bodyToMono(JsonNode.class)
//                 .block();

//         // レスポンスのJSONからアクセストークンを取り出す
//         return responseJson.get("access_token").asText();
//     }

//     // 特定の曲の情報を取得するメソッド
//     public String getTrackInfo() {
//         // Spotifyの曲ID
//         String trackId = "2mKpmcTNEk2Y7iH3lP5l";

//         try {
//             String accessToken = getAccessToken(); // まずアクセストークンを取得

//             // Spotify APIの /v1/tracks/{id} エンドポイントを呼び出す
//             String response = apiWebClient.get()
//                     .uri(uriBuilder -> uriBuilder
//                         .path("/tracks/{id}")
//                         .queryParam("market", "JP") // 日本市場を指定
//                         .build(trackId))
//                     .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // 認証ヘッダーを付与
//                     .retrieve()
//                     .bodyToMono(String.class)
//                     .block();

//             return response;

//         } catch (Exception e) {
//             e.printStackTrace();
//             return "情報の取得に失敗しました: " + e.getMessage();
//         }
//     }
// }


package oss_haluto.music_theory_app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException; // エラー詳細取得のためインポート
import com.fasterxml.jackson.databind.JsonNode;

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

    private String getAccessToken() {
        System.out.println("--- アクセストークン取得開始 ---");
        System.out.println("クライアントID: " + clientId);
        // Secretは全て表示せず、最初の数文字だけ表示（セキュリティのため）
        System.out.println("クライアントSecret（先頭5文字）: " + (clientSecret != null && clientSecret.length() > 5 ? clientSecret.substring(0, 5) : ""));

        try {
            String credentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
            MultiValueMap<String, String> bodyValues = new LinkedMultiValueMap<>();
            bodyValues.add("grant_type", "client_credentials");

            JsonNode responseJson = authWebClient.post()
                    .uri("/api/token")
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(bodyValues)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
            
            String accessToken = responseJson.get("access_token").asText();
            System.out.println("アクセストークン取得成功！ トークン（先頭10文字）: " + (accessToken.length() > 10 ? accessToken.substring(0, 10) : ""));
            System.out.println("--- アクセストークン取得終了 ---");
            return accessToken;

        } catch (WebClientResponseException e) {
            System.err.println("!!! アクセストークン取得でエラー発生 !!!");
            System.err.println("エラーコード: " + e.getStatusCode());
            System.err.println("エラーレスポンス: " + e.getResponseBodyAsString());
            throw e; // エラーを再スロー
        }
    }

    public String getTrackInfo() {
        System.out.println("\n--- 曲情報取得開始 ---");
        String trackId = "6lod4";
        System.out.println("リクエストするTrack ID: " + trackId);

        try {
            String accessToken = getAccessToken();

            System.out.println("APIリクエストを送信します...");
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
            System.err.println("エラーレスポンス: " + e.getResponseBodyAsString()); // ★これが一番重要な情報
            return "情報の取得に失敗しました: " + e.getStatusCode() + " " + e.getResponseBodyAsString();
        } catch (Exception e) {
            System.err.println("!!! 予期せぬエラーが発生 !!!");
            e.printStackTrace();
            return "情報の取得に失敗しました: " + e.getMessage();
        }
    }
}