package org.company;

import com.google.gson.*;

public class ExtractCaptionTracks {

    public static JsonObject getVideoInfoWithCaptions(String json) {
        JsonObject result = new JsonObject();
        try {
            JsonObject playerResponse = JsonParser.parseString(json).getAsJsonObject();

            JsonObject videoDetails = playerResponse.getAsJsonObject("videoDetails");
            String videoId = videoDetails.has("videoId") ? videoDetails.get("videoId").getAsString() : "";
            String title = videoDetails.has("title") ? videoDetails.get("title").getAsString() : "";
            String channel = videoDetails.has("author") ? videoDetails.get("author").getAsString() : "";
            String description = videoDetails.has("shortDescription") ? videoDetails.get("shortDescription").getAsString() : "";
            String lengthSeconds = videoDetails.has("lengthSeconds") ? videoDetails.get("lengthSeconds").getAsString() : "";

            String formattedLength = "";
            try {
                int secs = Integer.parseInt(lengthSeconds);
                int h = secs / 3600;
                int m = (secs % 3600) / 60;
                int s = secs % 60;
                formattedLength = (h > 0 ? h + ":" : "") + String.format("%02d:%02d", m, s);
            } catch (Exception ignore) {}

            String defaultLanguage = getDefaultCaptionLanguage(json);
            String ccUrl = getCaptionUrlForLanguage(json, defaultLanguage);

            String uploadDate = "";
            try {
                JsonObject microformat = playerResponse
                        .getAsJsonObject("microformat")
                        .getAsJsonObject("playerMicroformatRenderer");
                uploadDate = microformat.has("uploadDate") ? microformat.get("uploadDate").getAsString() : "";
            } catch (Exception ignore) {}

            result.addProperty("video_id", videoId);
            result.addProperty("title", title);
            result.addProperty("channel", channel);
            result.addProperty("description", description);
            result.addProperty("length_seconds", lengthSeconds);
            result.addProperty("length_formatted", formattedLength);
            result.addProperty("upload_date", uploadDate);
            result.addProperty("default_caption_language", defaultLanguage != null ? defaultLanguage : "");
            result.addProperty("caption_url", ccUrl != null ? ccUrl : "");
        } catch (Exception e) {
            System.out.println("Error extracting video details or captions: " + e.getMessage());
        }
        return result;
    }

    public static String getDefaultCaptionLanguage(String json) {
        try {
            JsonObject playerResponse = JsonParser.parseString(json).getAsJsonObject();
            JsonObject captions = playerResponse
                    .getAsJsonObject("captions")
                    .getAsJsonObject("playerCaptionsTracklistRenderer");
            JsonArray captionTracks = captions.getAsJsonArray("captionTracks");

            if (captionTracks.size() > 0) {
                JsonObject track = captionTracks.get(0).getAsJsonObject();
                return track.get("languageCode").getAsString();
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    public static String getCaptionUrlForLanguage(String json, String languageCode) {
        try {
            JsonObject playerResponse = JsonParser.parseString(json).getAsJsonObject();
            JsonObject captions = playerResponse
                    .getAsJsonObject("captions")
                    .getAsJsonObject("playerCaptionsTracklistRenderer");
            JsonArray captionTracks = captions.getAsJsonArray("captionTracks");

            for (JsonElement elem : captionTracks) {
                JsonObject track = elem.getAsJsonObject();
                if (languageCode.equals(track.get("languageCode").getAsString())) {
                    return track.get("baseUrl").getAsString();
                }
            }
        } catch (Exception e) {
            System.out.println("Error extracting caption URL for language: " + languageCode);
        }
        return null;
    }
}