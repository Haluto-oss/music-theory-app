package oss_haluto.music_theory_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // Valueをインポート
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder; // URLEncoderをインポート
import java.nio.charset.StandardCharsets; // StandardCharsetsをインポート

@Controller
public class HomeController {

    private final SpotifyService spotifyService;

    // Spotifyがログイン後にリダイレクトしてくる /callback パスを処理する
    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code, Model model) {
    // @RequestParam("code") は、URLの?code=...の部分を取得し、
    // String型の変数`code`に自動で入れてくれる、Springの便利な機能です。

        System.out.println("Spotifyから受け取ったAuthorization Code: " + code);

    // 受け取ったコードを画面に表示するために、Modelに追加します。
        model.addAttribute("authorizationCode", code);

    // TODO: 次のステップで、このコードを使ってアクセストークンを取得する処理を追加します。

        return "callback"; // 新しく作る "callback.html" を表示するよう指示
    }

    // application.propertiesからclientIdを読み込む
    @Value("${spotify.client.id}")
    private String clientId;

    @Autowired
    public HomeController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @GetMapping("/")
    public String home(Model model) {
        // --- SpotifyログインURLの生成 ---
        String redirectUri = "http://127.0.0.1:8080/callback"; // ログイン後の戻り先
        String scope = "user-read-private user-read-email"; // 要求する権限
        
        // URLエンコード処理
        String encodedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);

        String spotifyAuthUrl = "https://open.spotify.com/track/ABCDE0" +
                "?response_type=code" +
                "&client_id=" + clientId +
                "&scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8) +
                "&redirect_uri=" + encodedRedirectUri;

        model.addAttribute("spotifyAuthUrl", spotifyAuthUrl);
        // --- ここまでが追加・修正部分 ---

        // 以前の曲情報取得のロジックは、一旦コメントアウトします
        // String trackInfo = spotifyService.getTrackInfo();
        // model.addAttribute("trackInfo", trackInfo);

        return "home";
    }
}