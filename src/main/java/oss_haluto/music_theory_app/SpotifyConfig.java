package oss_haluto.music_theory_app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

// このクラスが設定用のクラスであることを示すアノテーション
@Configuration
public class SpotifyConfig {

    // Springに「WebClient.Builder」という部品の作り方を教える
    // @Beanアノテーションを付けたメソッドが返すオブジェクトが、部品として登録される
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
