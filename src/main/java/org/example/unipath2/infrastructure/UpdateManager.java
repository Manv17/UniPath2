package org.example.unipath2.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class UpdateManager {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    private final String owner;
    private final String repo;
    private final String currentVersion;

    public UpdateManager(String owner, String repo, String currentVersion) {
        this.owner = owner;
        this.repo = repo;
        this.currentVersion = normalizeVersion(currentVersion);
    }

    private static String textOrEmpty(JsonNode node) {
        return node == null || node.isNull() ? "" : node.asText("");
    }

    private static String normalizeVersion(String version) {
        if (version == null) {
            return "0.0.0";
        }
        version = version.trim();
        if (version.startsWith("v") || version.startsWith("V")) {
            version = version.substring(1);
        }
        return version;
    }

    private static boolean isNewer(String latest, String current) {
        int[] latestParts = parseVersion(latest);
        int[] currentParts = parseVersion(current);

        for (int i = 0; i < Math.max(latestParts.length, currentParts.length); i++) {
            int l = i < latestParts.length ? latestParts[i] : 0;
            int c = i < currentParts.length ? currentParts[i] : 0;

            if (l > c) return true;
            if (l < c) return false;
        }
        return false;
    }

    private static int[] parseVersion(String version) {
        String cleaned = version.replaceAll("[^0-9.]", "");
        if (cleaned.isBlank()) {
            return new int[]{0, 0, 0};
        }

        String[] parts = cleaned.split("\\.");
        int[] result = new int[parts.length];

        for (int i = 0; i < parts.length; i++) {
            try {
                result[i] = Integer.parseInt(parts[i]);
            } catch (NumberFormatException e) {
                result[i] = 0;
            }
        }

        return result;
    }

    public void checkForUpdatesAsync(Stage ownerStage) {
        CompletableFuture
                .supplyAsync(this::fetchLatestRelease)
                .thenAccept(release -> {
                    if (release == null) {
                        return;
                    }

                    String latestVersion = normalizeVersion(release.version());
                    if (!isNewer(latestVersion, currentVersion)) {
                        return;
                    }

                    Platform.runLater(() -> showUpdateDialog(ownerStage, release));
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }

    private ReleaseInfo fetchLatestRelease() {
        String apiUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/releases/latest";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Accept", "application/vnd.github+json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Update check failed. HTTP status: " + response.statusCode());
                return null;
            }

            JsonNode root = MAPPER.readTree(response.body());

            String tagName = textOrEmpty(root.get("tag_name"));
            String releaseName = textOrEmpty(root.get("name"));
            String body = textOrEmpty(root.get("body"));
            String htmlUrl = textOrEmpty(root.get("html_url"));

            return new ReleaseInfo(tagName, releaseName, body, htmlUrl);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showUpdateDialog(Stage ownerStage, ReleaseInfo release) {
        ButtonType openPageButton = new ButtonType("Apri pagina release", ButtonBar.ButtonData.OK_DONE);
        ButtonType laterButton = new ButtonType("Più tardi", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(ownerStage);
        alert.setTitle("Aggiornamento disponibile");
        alert.setHeaderText("È disponibile una nuova versione: " + release.version());

        String releaseTitle = release.title().isBlank() ? release.version() : release.title();
        String description = release.description().isBlank()
                ? "Nessuna descrizione disponibile."
                : release.description();

        alert.setContentText(
                "Note di rilascio:\n" + description
        );

        alert.getButtonTypes().setAll(openPageButton, laterButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == openPageButton) {
            openReleasePage(release.url());
        }
    }

    private void openReleasePage(String url) {
        if (url == null || url.isBlank()) {
            return;
        }

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(URI.create(url));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private record ReleaseInfo(String version, String title, String description, String url) {
    }
}