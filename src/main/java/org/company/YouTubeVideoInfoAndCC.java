package org.company;

import com.google.gson.*;
import java.io.FileWriter;
import java.io.IOException;

public class YouTubeVideoInfoAndCC {
    public static JsonObject Final_CC(String YtUrl) throws Exception {
        String json = YouTubeInitialPlayerResponseExtractor.getInitialPlayerResponseJson(YtUrl);

        JsonObject infoJson = ExtractCaptionTracks.getVideoInfoWithCaptions(json);
        String ccTitle = infoJson.has("title") ? infoJson.get("title").getAsString() : "";
        String ccUrl = infoJson.has("caption_url") ? infoJson.get("caption_url").getAsString() : "";

        String ccParagraph;
        if (!ccUrl.isEmpty()) {
            ccParagraph = YouTubeCCPlainTextExtractor.extractPlainTextFromCC(ccUrl);
            infoJson.addProperty("video_cc", ccParagraph);
        } else {
            infoJson.addProperty("video_cc", "");
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String finalJson = gson.toJson(infoJson);

        String filePath = String.format("%s.json", ccTitle);

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(finalJson);
            System.out.println("JSON file created successfully at: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return infoJson;
    }
}