package com.example.course.controller;

import com.example.course.constant.DataType;
import com.example.course.service.impl.MediaServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaServiceImpl mediaService;

    // Upload public
    @PostMapping("/upload/public")
    public ResponseEntity<String> uploadPublic(@RequestParam MultipartFile file, @RequestParam DataType type) throws IOException {
        String url = mediaService.uploadImage(file, type, true);
        return ResponseEntity.status(HttpStatus.CREATED).body(url);
    }

    // Upload private
    @PostMapping("/upload/private")
    public ResponseEntity<String> uploadPrivate(@RequestParam MultipartFile file, @RequestParam DataType type) throws IOException {
        String url = mediaService.uploadImage(file, type, false);
        return ResponseEntity.status(HttpStatus.CREATED).body(url);
    }

    // View public image (no auth)
    @GetMapping("/public/{id}")
    public ResponseEntity<byte[]> viewPublic(@PathVariable String id) throws IOException {
        return mediaService.getPublicImage(id);
    }

    // View private image (need auth)
    @GetMapping("/private/{id}")
    public ResponseEntity<byte[]> viewPrivate(@PathVariable String id) throws IOException {
        return mediaService.getPrivateImage(id);
    }
}

