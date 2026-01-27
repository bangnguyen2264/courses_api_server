package com.example.course.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties("app.api")
public class AppUtils {
    private String baseUrlApi;
    private String title;
    private String version;
    private String description;
    private String contactName;
    private String contactEmail;
}