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
            key = "'{subject-data}:list:' + #filter.toString()",
            unless = "#result == null"
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
    @Cacheable(
            value = "subject",
            key = "'{subject-data}:id:' + #id"
    )
    public SubjectDetailResponse getById(Long id) {
        Subject subject = findById(id);
        return SubjectDetailResponse.toResponse(subject);
    }

    @Override
    @Transactional
    @CacheEvict(value = "subject-list", allEntries = true)
    public SubjectResponse create(SubjectRequest subjectRequest) {
        if (subjectRepository.existsByName(subjectRequest.getName())) {
            throw new BadRequestException("Subject name already exists");
        }
        Subject subject = subjectMapper.toEntity(subjectRequest);
        Subject saved = subjectRepository.save(subject);
        return subjectMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "subject-list", allEntries = true)
    public List<SubjectResponse> addAll(List<SubjectRequest> subjectRequests) {
        if (subjectRequests == null || subjectRequests.isEmpty()) {
            return List.of();
        }

        List<String> names = subjectRequests.stream().map(SubjectRequest::getName).toList();
        List<String> existingNames = subjectRepository.findByNameIn(names)
                .stream().map(Subject::getName).toList();

        if (!existingNames.isEmpty()) {
            throw new BadRequestException("These subject names already exist: " + existingNames);
        }

        List<Subject> subjects = subjectRequests.stream().map(subjectMapper::toEntity).toList();
        List<Subject> savedSubjects = subjectRepository.saveAll(subjects);

        return savedSubjects.stream().map(subjectMapper::toResponse).toList();
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "subject", key = "'{subject-data}:id:' + #id"),
                    @CacheEvict(value = "subject-list", allEntries = true)
            }
    )
    public SubjectResponse update(Long id, SubjectRequest request) {
        Subject subject = findById(id);
        subjectMapper.updateEntityFromRequest(request, subject);
        Subject saved = subjectRepository.save(subject);
        return subjectMapper.toResponse(saved);
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "subject", key = "'{subject-data}:id:' + #id"),
                    @CacheEvict(value = "subject-list", allEntries = true)
            }
    )
    public void delete(Long id) {
        Subject subject = findById(id);
        subjectRepository.delete(subject);
    }

    private Subject findById(Long id) {
        return subjectRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Subject not found with id: " + id)
        );
    }
}