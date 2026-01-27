package com.example.course.mapper;

import com.example.course.model.entity.LessonSection;
import com.example.course.model.entity.Subject;
import com.example.course.model.request.LessonSectionRequest;
import com.example.course.model.request.SubjectRequest;
import com.example.course.model.response.LessonSectionResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface LessonSectionMapper {
    LessonSection toEntity(LessonSectionRequest lessonSectionRequest);
    LessonSectionResponse toResponse(LessonSection lessonSection);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(LessonSectionRequest request, @MappingTarget LessonSection entity);
}
