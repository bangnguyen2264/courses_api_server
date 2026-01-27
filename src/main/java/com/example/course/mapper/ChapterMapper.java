package com.example.course.mapper;

import com.example.course.model.entity.Chapter;
import com.example.course.model.entity.Subject;
import com.example.course.model.request.ChapterRequest;
import com.example.course.model.request.SubjectRequest;
import com.example.course.model.response.ChapterResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface ChapterMapper {

    ChapterResponse toResponse(Chapter chapter);
    Chapter toEntity(ChapterRequest chapterRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(ChapterRequest request, @MappingTarget Chapter entity);

}

