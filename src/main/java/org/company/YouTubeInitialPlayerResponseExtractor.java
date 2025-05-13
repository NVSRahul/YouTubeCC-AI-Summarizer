package org.company;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YouTubeInitialPlayerResponseExtractor {

    public static String downloadHtml(String videoUrl) throws Exception {
        String fullUrl = videoUrl;
        if (videoUrl.matches("https?://youtu\\.be/([\\w-]{11})")) {
            String videoId = videoUrl.replaceAll(".*youtu\\.be/([\\w-]{11}).*", "$1");
            fullUrl = "https://www.youtube.com/watch?v=" + videoId;
        }
        HttpURLConnection conn = (HttpURLConnection) new URL(fullUrl).openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        StringBuilder html = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                html.append(line).append("\n");
            }
        }
        return html.toString();
    }

    public static String extractInitialPlayerResponseJson(String html) {
        Pattern pattern = Pattern.compile("ytInitialPlayerResponse\\s*=\\s*(\\{.*?});", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static String getInitialPlayerResponseJson(String videoUrl) throws Exception {
        String html = downloadHtml(videoUrl);
        String json = extractInitialPlayerResponseJson(html);
        if (json != null) {
            System.out.println("Found ytInitialPlayerResponse JSON.");
            return json;
        } else {
            System.out.println("ytInitialPlayerResponse JSON not found.");
            throw new RuntimeException("ytInitialPlayerResponse JSON not found.");
        }
    }
}