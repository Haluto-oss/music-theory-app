package oss_haluto.music_theory_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class HomeController {

    private final SpotifyService spotifyService;
    private final String clientId; // finalを追加

    // ★★★ここが修正の最重要ポイント★★★
    // コンストラクタで、SpotifyServiceとclientIdの両方を受け取るように変更
    @Autowired
    public HomeController(SpotifyService spotifyService, @Value("${spotify.client.id}") String clientId) {
        this.spotifyService = spotifyService;
        this.clientId = clientId; // 受け取ったclientIdをフィールドに設定
    }

    @GetMapping("/")
    public String home(Model model) {
        String redirectUri = "http://127.0.0.1:8080/callback";
        String scope = "user-read-private user-read-email";

        String encodedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);

        String spotifyAuthUrl = "https://open.spotify.com/track/ABCDE0" +
                "?response_type=code" +
                "&client_id=" + this.clientId + // this.clientId を使う
                "&scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8) +
                "&redirect_uri=" + encodedRedirectUri;

        model.addAttribute("spotifyAuthUrl", spotifyAuthUrl);

        System.out.println("--- 生成されたSpotify認証URL ---");
        System.out.println(spotifyAuthUrl);
        System.out.println("---------------------------------");

        return "home";
    }

    // ... 既存の @Controller や public class HomeController(...) の部分はそのまま ...

    @GetMapping("/callback")
        public String callback(@RequestParam("code") String code, Model model) {
        String redirectUri = "http://127.0.0.1:8080/callback";
        String scope = "user-read-private user-read-email";

        // ★★★この行を修正しました★★★
        String encodedScope = scope.replace(" ", "%20");
        String encodedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);

        String spotifyAuthUrl = "https://open.spotify.com/track/ABCDE0" +
                "?response_type=code" +
                "&client_id=" + this.clientId +
                "&scope=" + encodedScope +
                "&redirect_uri=" + encodedRedirectUri;

        model.addAttribute("spotifyAuthUrl", spotifyAuthUrl);

        return "home";
    }
}