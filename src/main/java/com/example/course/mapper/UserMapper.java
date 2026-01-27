package com.example.course.mapper;

import com.example.course.model.entity.Subject;
import com.example.course.model.entity.User;
import com.example.course.model.request.RegisterRequest;
import com.example.course.model.request.SubjectRequest;
import com.example.course.model.request.UserUpdateRequest;
import com.example.course.model.response.UserResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UserUpdateRequest request, @MappingTarget User entity);

    User toEntity(RegisterRequest registerRequest);
}
