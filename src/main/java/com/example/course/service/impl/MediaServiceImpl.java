package com.example.course.service.impl;

import com.example.course.constant.DataType;
import com.example.course.exception.BadRequestException;
import com.example.course.model.entity.MediaFile;
import com.example.course.repository.MediaFileRepository;
import com.example.course.utils.MediaUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
@Service
@RequiredArgsConstructor
public class MediaServiceImpl {

    private final MediaFileRepository mediaFileRepository;

    /* ==============================
       Upload
     ============================== */

    @Transactional
    public String uploadImage(MultipartFile file, DataType dataType, boolean isPublic) throws IOException {

        if (dataType != DataType.OTHER) {
            if (file == null || file.isEmpty()) {
                throw new BadRequestException("File is required for media type: " + dataType);
            }

            if (!isValidFile(file, dataType)) {
                throw new BadRequestException("Invalid file type for media type: " + dataType);
            }
        }

        byte[] fileData = null;
        String contentType = null;
        String originalName = null;

        if (file != null && !file.isEmpty()) {
            contentType = file.getContentType();
            originalName = file.getOriginalFilename();

            fileData = (dataType == DataType.IMAGE)
                    ? MediaUtils.compressImage(file.getBytes())
                    : file.getBytes();
        }

        MediaFile saved = mediaFileRepository.save(
                MediaFile.builder()
                        .name(originalName)
                        .type(contentType)
                        .data(fileData)
                        .isPublic(isPublic)
                        .build()
        );

        return (isPublic ? "/api/media/public/" : "/api/media/private/") + saved.getId();
    }

    /* ==============================
       Public
     ============================== */

    @Cacheable(
            value = "media",
            key = "'{media-data}:public:' + #id",
            unless = "#result == null"
    )
    public byte[] getPublicBytes(String id) {

        MediaFile mediaFile = mediaFileRepository.findById(id)
                .filter(MediaFile::isPublic)
                .orElseThrow(() -> new BadRequestException("File not found or private"));

        return decompressIfNeeded(mediaFile);
    }

    public ResponseEntity<byte[]> getPublicImage(String id) {
        return buildResponse(id, getPublicBytes(id));
    }

    /* ==============================
       Private
     ============================== */

    @Cacheable(
            value = "media",
            key = "'{media-data}:private:' + #id",
            unless = "#result == null"
    )
    public byte[] getPrivateBytes(String id) {

        MediaFile mediaFile = mediaFileRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("File not found"));

        return decompressIfNeeded(mediaFile);
    }

    public ResponseEntity<byte[]> getPrivateImage(String id) {
        return buildResponse(id, getPrivateBytes(id));
    }

    /* ==============================
       Delete
     ============================== */

    @Caching(evict = {
            @CacheEvict(value = "media", key = "'{media-data}:public:' + #id"),
            @CacheEvict(value = "media", key = "'{media-data}:private:' + #id")
    })
    @Transactional
    public void delete(String id) {
        mediaFileRepository.deleteById(id);
    }

    /* ==============================
       Utils
     ============================== */

    private byte[] decompressIfNeeded(MediaFile mediaFile) {
        if (mediaFile.getType() != null && mediaFile.getType().startsWith("image/")) {
            return MediaUtils.decompressImage(mediaFile.getData());
        }
        return mediaFile.getData();
    }

    private ResponseEntity<byte[]> buildResponse(String id, byte[] data) {

        MediaFile meta = mediaFileRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("File not found"));

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(meta.getType()))
                .header("Content-Disposition", "inline; filename=\"" + meta.getName() + "\"")
                .body(data);
    }

    private boolean isValidFile(MultipartFile file, DataType mediaType) {
        String contentType = file.getContentType();
        if (contentType == null) return false;

        return switch (mediaType) {
            case TEXT ->
                    contentType.equals("application/pdf")
                            || contentType.equals("application/msword")
                            || contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                            || contentType.equals("text/plain");
            case IMAGE -> contentType.startsWith("image/");
            case VIDEO -> contentType.equals("video/mp4");
            case AUDIO -> contentType.equals("audio/mpeg");
            case OTHER -> true;
        };
    }
}
