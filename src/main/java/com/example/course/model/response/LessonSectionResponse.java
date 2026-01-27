package com.example.course.model.response;

import com.example.course.constant.DataType;
import com.example.course.model.entity.LessonSection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LessonSectionResponse {

    private Long id;

    private Long lessonId;

    private DataType dataType;

    private String title;

    private String description;

    private String content;

    private String dataPath;

    private Integer position;

    public static LessonSectionResponse toResponse(LessonSection lessonSection) {
        return LessonSectionResponse.builder()
                .id(lessonSection.getId())
                .lessonId(lessonSection.getLesson().getId())
                .dataType(lessonSection.getDataType())
                .title(lessonSection.getTitle())
                .description(lessonSection.getDescription())
                .content(lessonSection.getContent())
                .dataPath(lessonSection.getDataPath())
                .position(lessonSection.getPosition())
                .build();
    }
}
