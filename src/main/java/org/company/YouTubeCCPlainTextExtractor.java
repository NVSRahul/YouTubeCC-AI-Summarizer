package org.company;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

public class YouTubeCCPlainTextExtractor {

    public static String extractPlainTextFromCC(String ccUrl) throws Exception {
        HttpURLConnection conn = (HttpURLConnection)new URL(ccUrl).openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        StringBuilder xmlBuilder = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            xmlBuilder.append(line).append("\n");
        }
        br.close();

        String xml = xmlBuilder.toString();

        List<String> allTexts = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xml)));

        NodeList textNodes = doc.getElementsByTagName("text");
        for (int i = 0; i < textNodes.getLength(); i++) {
            String caption = textNodes.item(i).getTextContent();
            caption = htmlUnescape(caption).replace("\n", " ").trim();
            if (!caption.isEmpty()) {
                allTexts.add(caption);
            }
        }

        return String.join(" ", allTexts).replaceAll("\\s+", " ");
    }

    private static String htmlUnescape(String str) {
        return str.replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                .replace("&apos;", "'")
                .replace("&nbsp;", " ");
    }
}