package com.wssoftwares.ikutibnb_backend.house;

import com.wssoftwares.ikutibnb_backend.config.AppConfig;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.Base64;
import java.util.UUID;

@Component
public class ImageUtils {

    private final AppConfig appConfig;

    public ImageUtils(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public String saveHouseImage(String base64Image, UUID houseId) throws IOException {
        return saveImage(base64Image, houseId, "house_images", "house");
    }

    public String saveRoomImage(String base64Image, UUID roomId) throws IOException {
        return saveImage(base64Image, roomId, "room_images", "room");
    }

    public String saveProfilePicture(String base64Image, UUID userId) throws IOException {
        return saveImage(base64Image, userId, "profile_pictures", "user");
    }

    private String saveImage(String base64Image, Object id, String folderName, String prefix) throws IOException {
        Path folder = Paths.get(appConfig.getUploadDir(), folderName);

        // Create folder if missing
        if (!Files.exists(folder)) {
            Files.createDirectories(folder);
        }

        String[] parts = base64Image.split(",");
        if (parts.length != 2 || !parts[0].startsWith("data:image")) {
            throw new IllegalArgumentException("Invalid base64 image format");
        }

        String extension = parts[0].split("/")[1].split(";")[0];
        byte[] imageBytes;
        try {
            imageBytes = Base64.getDecoder().decode(parts[1]);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid image Base64 data. Send the actual encoded image bytes.",
                    e);
        }

        int maxSize = 30 * 1024 * 1024; // 30MB
        if (imageBytes.length > maxSize) {
            throw new IllegalArgumentException("Image size exceeds 20MB limit");
        }

        String filename = prefix + "_" + id + "_" + UUID.randomUUID() + "." + extension;
        Path filePath = folder.resolve(filename);

        Files.write(filePath, imageBytes, StandardOpenOption.CREATE_NEW);

        return appConfig.getBaseUrl() + "/uploads/" + folderName + "/" + filename;
    }
}
