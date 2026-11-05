package com.ondc.config;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Jwt jwt = new Jwt();
    private Cors cors = new Cors();
    private Upload upload = new Upload();
    private Tesseract tesseract = new Tesseract();
    private Gemini gemini = new Gemini();
    private Anthropic anthropic = new Anthropic();

    @Getter
    @Setter
    public static class Jwt {
        private String secret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
        private long expiration = 86_400_000L;
    }

    @Getter
    @Setter
    public static class Cors {
        private String origins = "http://localhost:5173";

        public List<String> originList() {
            return Arrays.stream(origins.split(","))
                    .map(String::trim)
                    .filter(value -> !value.isEmpty())
                    .toList();
        }
    }

    @Getter
    @Setter
    public static class Upload {
        private String dir = "uploads";
    }

    @Getter
    @Setter
    public static class Tesseract {
        private String dataPath = "tessdata";
    }

    @Getter
    @Setter
    public static class Gemini {
        private String apiKey = "";
        private String model = "gemini-2.5-flash";
        private String baseUrl = "https://generativelanguage.googleapis.com";
        private int timeoutSeconds = 45;
    }

    @Getter
    @Setter
    public static class Anthropic {
        private String apiKey = "";
        private String model = "claude-sonnet-4-5";
        private String baseUrl = "https://api.anthropic.com";
        private String version = "2023-06-01";
        private int timeoutSeconds = 45;
    }
}
