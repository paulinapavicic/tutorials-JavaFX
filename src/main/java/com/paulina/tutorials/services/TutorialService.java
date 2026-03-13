package com.paulina.tutorials.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paulina.tutorials.models.Tutorial;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TutorialService {
    private final String baseUrl = "http://localhost:8081/api/tutorials";

    public List<Tutorial> getAllTutorials() {
        try {
            System.out.println("🔄 Calling: " + baseUrl);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("📡 Response status: " + response.statusCode());
            System.out.println("📄 Response body: " + response.body());

            List<Tutorial> tutorials = parseTutorialsJson(response.body());
            System.out.println("✅ Parsed " + tutorials.size() + " tutorials");

            return tutorials;

        } catch (Exception e) {
            System.out.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<Tutorial> parseTutorialsJson(String json) {
        List<Tutorial> tutorials = new ArrayList<>();


        Pattern pattern = Pattern.compile("\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(json);

        while (matcher.find()) {
            String tutorialJson = "{" + matcher.group(1) + "}";
            Tutorial tutorial = parseSingleTutorial(tutorialJson);
            if (tutorial != null) {
                tutorials.add(tutorial);
            }
        }
        return tutorials;
    }

    private Tutorial parseSingleTutorial(String json) {
        try {
            Long id = extractLong(json, "id");
            String title = extractString(json, "title");
            String description = extractString(json, "description");
            boolean published = extractBoolean(json, "published");

            if (id != null && title != null) {
                return new Tutorial(id, title, description, published);
            }
        } catch (Exception e) {
            System.out.println("Failed to parse: " + json);
        }
        return null;
    }

    private Long extractLong(String json, String field) {
        Pattern pattern = Pattern.compile("\"" + field + "\":(-?\\d+)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        return null;
    }

    private String extractString(String json, String field) {
        Pattern pattern = Pattern.compile("\"" + field + "\":\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private boolean extractBoolean(String json, String field) {
        Pattern pattern = Pattern.compile("\"" + field + "\":(true|false)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1).equals("true");
        }
        return false;
    }


}
