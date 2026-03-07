package com.restaurantmanagement.order_api.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    /**
     * Uploads a file and returns the accessible URL
     * @param file   the file to upload
     * @param folder logical folder name e.g. "menu-items", "restaurants"
     * @return public URL to access the file
     */
    String uploadFile(MultipartFile file, String folder);

    /**
     * Deletes a file by its URL
     * @param fileUrl the URL returned by uploadFile()
     */
    void deleteFile(String fileUrl);
}
