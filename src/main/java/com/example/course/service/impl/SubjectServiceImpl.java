package com.example.course.service.impl;

import com.example.course.exception.BadRequestException;
import com.example.course.exception.NotFoundException;
import com.example.course.filter.SubjectFilter;
import com.example.course.mapper.SubjectMapper;
import com.example.course.model.entity.Subject;
import com.example.course.model.request.SubjectRequest;
import com.example.course.model.response.ApiResponse;
import com.example.course.model.response.SubjectDetailResponse;
import com.example.course.model.response.SubjectResponse;
import com.example.course.repository.SubjectRepository;
import com.example.course.service.SubjectService;
import com.example.course.specification.SubjectSpecification;
import com.example.course.utils.PageableUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubjectServiceImpl implements SubjectService {
    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;

    @Override
    @Cacheable(
            value = "subject-list",
            key = "T(java.util.Objects).hash(#filter)"
    )
    public ApiResponse<SubjectResponse> getAll(SubjectFilter filter) {
        Pageable pageable = PageableUtil.createPageable(filter);
        Page<Subject> pageResult = subjectRepository.findAll(
                SubjectSpecification.filter(filter),
                pageable
        );
        Page<SubjectResponse> data = pageResult.map(subjectMapper::toResponse);

        return ApiResponse.fromPage(data);
    }

    @Override
    @Cacheable(value = "subject", key = "#id")
    public SubjectDetailResponse getById(Long id) {
        Subject subject = findById(id);
        return SubjectDetailResponse.toResponse(subject);
    }

    @Override
    @CacheEvict(value = {"subject-list"}, allEntries = true)
    @Transactional
    public SubjectResponse create(SubjectRequest subjectRequest) {

        // 1. Check trùng tên
        if (subjectRepository.existsByName(subjectRequest.getName())) {
            throw new BadRequestException("Subject name already exists");
        }
        // 2. Taọ mới
        Subject subject = subjectMapper.toEntity(subjectRequest);

        // 3. Save
        Subject saved = subjectRepository.save(subject);

        // 4. Map entity → response
        return subjectMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value="subject-list", allEntries=true)
    public List<SubjectResponse> addAll(List<SubjectRequest> subjectRequests) {
        if (subjectRequests == null || subjectRequests.isEmpty()) {
            return List.of();
        }

        // 1. Lấy danh sách tên gửi lên
        List<String> names = subjectRequests.stream()
                .map(SubjectRequest::getName)
                .toList();

        // 2. Kiểm tra trùng với DB
        List<String> existingNames = subjectRepository.findByNameIn(names)
                .stream()
                .map(Subject::getName)
                .toList();

        if (!existingNames.isEmpty()) {
            throw new BadRequestException("These subject names already exist: " + existingNames);
        }

        // 3. Map request → entity
        List<Subject> subjects = subjectRequests.stream()
                .map(subjectMapper::toEntity)
                .toList();

        // 4. Save all
        List<Subject> savedSubjects = subjectRepository.saveAll(subjects);

        // 5. Map entity → response
        return savedSubjects.stream()
                .map(subjectMapper::toResponse)
                .toList();
    }



    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "subject", key = "#id"),      // Chỉ xóa item đang sửa
                    @CacheEvict(value = "subject-list", allEntries = true) // Xóa list vì tên có thể đổi làm thay đổi thứ tự/kết quả tìm kiếm
            }
    )    public SubjectResponse update(Long id, SubjectRequest request) {

        Subject subject = findById(id);

        if (request.getName() != null &&
               ! subject.getName().equals(request.getName())) {
            throw new BadRequestException("Subject name already exists");
        }

        subjectMapper.updateEntityFromRequest(request, subject);

        Subject saved = subjectRepository.save(subject);

        return subjectMapper.toResponse(saved);
    }


    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "subject", key = "#id"),       // Chỉ xóa đúng thằng bị delete
                    @CacheEvict(value = "subject-list", allEntries = true) // Xóa list thì phải xóa hết
            }
    )    public void delete(Long id) {
        Subject subject = findById(id);

        subjectRepository.delete(subject);
    }

    private Subject findById(Long id) {
        return subjectRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Subject not found with id: " + id)
        );
    }
}
