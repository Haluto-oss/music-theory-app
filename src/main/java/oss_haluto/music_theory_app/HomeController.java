package oss_haluto.music_theory_app;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// このクラスがWebリクエストを処理する「コントローラー」であることを示すアノテーション
@Controller
public class HomeController {

    // ユーザーがWebサイトのトップページ ("/") にアクセスしたときに、このメソッドを動かすという設定
    @GetMapping("/")
    public String home() {
        // "home.html" という名前のHTMLファイルを探して表示してください、という意味
        return "home";
    }
}