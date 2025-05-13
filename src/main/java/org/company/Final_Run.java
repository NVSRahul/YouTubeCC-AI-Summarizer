package org.company;

import com.google.gson.JsonObject;

import java.util.Scanner;

public class Final_Run {
    public static void Final_Res(String YtUrl) throws Exception {
        JsonObject videoInfoJson = YouTubeVideoInfoAndCC.Final_CC(YtUrl);
        String aiResponse = AI_Work.AIResponse(videoInfoJson);

        assert aiResponse != null;
        String[] words = aiResponse.split("\\s+");
        StringBuilder output = new StringBuilder();

        int wordLimit = Math.min(100, words.length);
        for (int i = 0; i < wordLimit; i++) {
            output.append(words[i]);
            if (i < wordLimit - 1) output.append(" ");
        }
        System.out.println("AI Response (first 100 words):\n" + output);
    }

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the YouTube video URL: ");
        String YtUrl = sc.nextLine();
        Final_Res(YtUrl);
    }
}