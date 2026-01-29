package com.example.course.service.impl;

import com.example.course.constant.DataType;
import com.example.course.exception.BadRequestException;
import com.example.course.exception.NotFoundException;
import com.example.course.filter.UserFilter;
import com.example.course.mapper.UserMapper;
import com.example.course.model.entity.User;
import com.example.course.model.request.ChangePasswordRequest;
import com.example.course.model.request.UserUpdateRequest;
import com.example.course.model.response.ApiResponse;
import com.example.course.model.response.UserResponse;
import com.example.course.repository.UserRepository;
import com.example.course.service.UserService;
import com.example.course.specification.UserSpecification;
import com.example.course.utils.PageableUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MediaServiceImpl mediaService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Cacheable(
            value = "user-list",
            key = "'{user-data}:list:' + #filter.toString()",
            unless = "#result == null"
    )
    public ApiResponse<UserResponse> getAll(UserFilter filter) {
        Pageable pageable = PageableUtil.createPageable(filter);
        Page<User> users = userRepository.findAll(UserSpecification.filter(filter), pageable);
        Page<UserResponse> result = users.map(userMapper::toResponse);
        return ApiResponse.fromPage(result);
    }

    @Override
    @Cacheable(
            value = "user",
            key = "'{user-data}:id:' + #id"
    )
    public UserResponse getById(Long id) {
        User user = findById(id);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "user", key = "'{user-data}:id:' + #id"),
                    @CacheEvict(value = "user-list", allEntries = true)
            }
    )
    public UserResponse update(Long id, UserUpdateRequest request) {
        User user = findById(id);

        userMapper.updateEntityFromRequest(request, user);

        if (request.getAvatar() != null) {
            try {
                String newAvatarUrl = mediaService.uploadImage(request.getAvatar(), DataType.IMAGE, true);
                user.setAvatarUrl(newAvatarUrl);
            } catch (Exception e) {
                throw new BadRequestException(e.getMessage());
            }
        }

        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "user", key = "'{user-data}:id:' + #id"),
                    @CacheEvict(value = "user-list", allEntries = true)
            }
    )
    public void delete(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "user", key = "'{user-data}:id:' + #id"),
                    @CacheEvict(value = "user-list", allEntries = true)
            }
    )
    public String changePassword(Long id, ChangePasswordRequest request) {
        User user = findById(id);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("New password is match with current password");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Confirm password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return "Changed password successfully";
    }

    private User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("User not found with id: " + id)
                );
    }
}