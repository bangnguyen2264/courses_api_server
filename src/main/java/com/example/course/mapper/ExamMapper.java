package com.example.course.mapper;


import com.example.course.model.entity.Chapter;
import com.example.course.model.entity.Exam;
import com.example.course.model.request.ChapterRequest;
import com.example.course.model.request.ExamRequest;
import com.example.course.model.response.ChapterResponse;
import com.example.course.model.response.ExamResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ExamMapper {

    default ExamResponse toResponse(Exam exam) {
        return ExamResponse.builder()
                .id(exam.getId())
                .title(exam.getTitle())
                .duration(exam.getDuration().getMinutes())
                .build();
    }
    Exam toEntity(ExamRequest examRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(ExamRequest request, @MappingTarget Exam entity);
}
