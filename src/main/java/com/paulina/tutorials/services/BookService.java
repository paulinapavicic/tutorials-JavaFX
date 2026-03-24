package com.paulina.tutorials.services;

import com.paulina.tutorials.models.Book;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookService {
    private final String baseUrl = "http://localhost:8080/api/books";

    public List<Book> getAllBooks() {
        try {
            System.out.println("🔄 GET: " + baseUrl);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("📡 Status: " + response.statusCode());

            if (response.statusCode() == 200) {
                return parseBooksJson(response.body());
            } else {
                System.out.println("❌ HTTP " + response.statusCode());
                return new ArrayList<>();
            }

        } catch (Exception e) {
            System.out.println("❌ GET Error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void saveBook(Book book) throws Exception {
        System.out.println("💾 POST: " + book);

        String json = String.format(
                "{\"title\":\"%s\",\"author\":\"%s\",\"isbn\":\"%s\",\"available\":%s}",
                escapeJson(book.getTitle()),
                escapeJson(book.getAuthor() != null ? book.getAuthor() : ""),
                escapeJson(book.getIsbn() != null ? book.getIsbn() : ""),
                book.isAvailable()
        );

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 201) {
            throw new RuntimeException("Save failed: HTTP " + response.statusCode());
        }
        System.out.println("✅ Saved! Status: " + response.statusCode());
    }

    public void updateBook(Book book) throws Exception {
        if (book.getId() == null) {
            throw new RuntimeException("Cannot update: No ID");
        }

        System.out.println("🔄 PUT: " + book.getId());

        String json = String.format(
                "{\"title\":\"%s\",\"author\":\"%s\",\"isbn\":\"%s\",\"available\":%s}",
                escapeJson(book.getTitle()),
                escapeJson(book.getAuthor() != null ? book.getAuthor() : ""),
                escapeJson(book.getIsbn() != null ? book.getIsbn() : ""),
                book.isAvailable()
        );

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/" + book.getId()))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Update failed: HTTP " + response.statusCode());
        }
        System.out.println("✅ Updated! Status: " + response.statusCode());
    }

    public void deleteBook(Long id) throws Exception {
        if (id == null) {
            throw new RuntimeException("Cannot delete: No ID");
        }

        System.out.println("🗑️ DELETE: " + id);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw new RuntimeException("Delete failed: HTTP " + response.statusCode());
        }
        System.out.println("✅ Deleted! Status: " + response.statusCode());
    }


    private List<Book> parseBooksJson(String json) {
        List<Book> books = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(json);

        while (matcher.find()) {
            String bookJson = "{" + matcher.group(1) + "}";
            Book book = parseSingleBook(bookJson);
            if (book != null) {
                books.add(book);
            }
        }
        return books;
    }

    private Book parseSingleBook(String json) {
        try {
            Long id = extractLong(json, "id");
            String title = extractString(json, "title");
            String author = extractString(json, "author");
            String isbn = extractString(json, "isbn");
            boolean available = extractBoolean(json, "available");

            if (id != null && title != null) {
                Book book = new Book(title, author, isbn, available);
                book.setId(id);
                return book;
            }
        } catch (Exception e) {
            System.out.println("Parse error: " + json);
        }
        return null;
    }

    private Long extractLong(String json, String field) {
        Pattern pattern = Pattern.compile("\"" + field + "\":(-?\\d+)");
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? Long.parseLong(matcher.group(1)) : null;
    }

    private String extractString(String json, String field) {
        Pattern pattern = Pattern.compile("\"" + field + "\":\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? matcher.group(1) : null;
    }

    private boolean extractBoolean(String json, String field) {
        Pattern pattern = Pattern.compile("\"" + field + "\":(true|false)");
        Matcher matcher = pattern.matcher(json);
        return matcher.find() && matcher.group(1).equals("true");
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
