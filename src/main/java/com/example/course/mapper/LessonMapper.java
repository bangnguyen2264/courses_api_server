package com.example.course.mapper;

import com.example.course.model.entity.Lesson;
import com.example.course.model.entity.Subject;
import com.example.course.model.request.LessonRequest;
import com.example.course.model.request.SubjectRequest;
import com.example.course.model.response.LessonResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface LessonMapper {

    LessonResponse toResponse(Lesson lesson);
    Lesson toEntity(LessonRequest lessonRequest);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(LessonRequest request, @MappingTarget Lesson entity);

}
