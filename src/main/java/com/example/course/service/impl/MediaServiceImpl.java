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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl {

    private final MediaFileRepository mediaFileRepository;

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

            if (dataType == DataType.IMAGE) {
                fileData = MediaUtils.compressImage(file.getBytes());
            } else {
                fileData = file.getBytes();
            }
        }

        MediaFile savedFile = mediaFileRepository.save(
                MediaFile.builder()
                        .name(originalName)
                        .type(contentType)
                        .data(fileData)
                        .isPublic(isPublic)
                        .build()
        );

        String endpoint = isPublic ? "/api/media/public/" : "/api/media/private/";
        return endpoint + savedFile.getId();
    }

    @Cacheable(value = "media", key = "#id")
    public ResponseEntity<byte[]> getPublicImage(String id) throws IOException {
        MediaFile mediaFile = mediaFileRepository.findById(id)
                .filter(MediaFile::isPublic)
                .orElseThrow(() -> new BadRequestException("File not found or private"));
        return buildResponse(mediaFile);
    }

    @Cacheable(value = "media", key = "#id")
    public ResponseEntity<byte[]> getPrivateImage(String id) throws IOException {
        MediaFile mediaFile = mediaFileRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("File not found"));
        return buildResponse(mediaFile);
    }

    @CacheEvict(value = "media", key = "#id")
    @Transactional
    public void delete(String id) {
        mediaFileRepository.deleteById(id);
    }

    private ResponseEntity<byte[]> buildResponse(MediaFile mediaFile) throws IOException {
        byte[] fileData;

        if (mediaFile.getType() != null && mediaFile.getType().startsWith("image/")) {
            fileData = MediaUtils.decompressImage(mediaFile.getData());
        } else {
            fileData = mediaFile.getData();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(mediaFile.getType()))
                .header("Content-Disposition", "inline; filename=\"" + mediaFile.getName() + "\"")
                .body(fileData);
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
