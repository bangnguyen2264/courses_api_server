package com.example.course.model.response;

import com.example.course.model.entity.Subject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
@SuperBuilder
public class SubjectDetailResponse extends SubjectResponse implements Serializable {
    private List<ChapterDetailResponse> chapters;

    public static SubjectDetailResponse toResponse(Subject subject) {
        return SubjectDetailResponse.builder()
                .id(subject.getId())
                .name(subject.getName())
                .description(subject.getDescription())
                .position(subject.getPosition())
                .chapters(subject.getChapters().stream().map(ChapterDetailResponse::toResponse).collect(Collectors.toList()))
                .build();
    }
}
