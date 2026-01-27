package com.example.course.mapper;

import com.example.course.model.entity.Subject;
import com.example.course.model.request.SubjectRequest;
import com.example.course.model.response.SubjectResponse;
import org.mapstruct.*;


@Mapper(componentModel = "spring")
public interface SubjectMapper {

    // Entity → Response
    SubjectResponse toResponse(Subject subject);


    // Request → Entity (dùng cho create)
    Subject toEntity(SubjectRequest request);

    // Partial update (dùng cho update)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(SubjectRequest request, @MappingTarget Subject entity);
}
