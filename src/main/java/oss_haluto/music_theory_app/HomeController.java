package oss_haluto.music_theory_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Model をインポート
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // SpotifyServiceを使えるようにする
    private final SpotifyService spotifyService;

    @Autowired
    public HomeController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @GetMapping("/")
    public String home(Model model) { // Modelを引数に追加
        // Spotifyから曲情報を取得
        String trackInfo = spotifyService.getTrackInfo();

        // 取得した情報を "trackInfo" という名前でHTMLに渡す
        model.addAttribute("trackInfo", trackInfo);

        return "home";
    }
}