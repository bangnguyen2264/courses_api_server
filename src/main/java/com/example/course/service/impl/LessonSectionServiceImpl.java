package com.example.course.service.impl;

import com.example.course.constant.DataType;
import com.example.course.exception.NotFoundException;
import com.example.course.filter.LessonSectionFilter;
import com.example.course.mapper.LessonSectionMapper;
import com.example.course.model.entity.Lesson;
import com.example.course.model.entity.LessonSection;
import com.example.course.model.request.LessonSectionRequest;
import com.example.course.model.response.ApiResponse;
import com.example.course.model.response.LessonSectionResponse;
import com.example.course.repository.LessonRepository;
import com.example.course.repository.LessonSectionRepository;
import com.example.course.service.LessonSectionService;
import com.example.course.specification.LessonSectionSpecification;
import com.example.course.utils.PageableUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonSectionServiceImpl implements LessonSectionService {

    private final LessonSectionRepository lessonSectionRepository;
    private final LessonRepository lessonRepository;
    private final LessonSectionMapper lessonSectionMapper;
    private final MediaServiceImpl mediaService;

    @Override
    @Transactional
    @CacheEvict(value = "lesson-section-list", allEntries = true)
    public LessonSectionResponse create(LessonSectionRequest request) {
        try {
            Lesson lesson = lessonRepository.findById(request.getLessonId())
                    .orElseThrow(() ->
                            new NotFoundException("Lesson not found with id: " + request.getLessonId())
                    );

            LessonSection section = lessonSectionMapper.toEntity(request);

            if (request.getFile() != null && !request.getFile().isEmpty()) {
                String path = mediaService.uploadImage(
                        request.getFile(),
                        request.getDataType(),
                        false
                );
                section.setDataPath(path);
            }

            section.setLesson(lesson);
            lesson.getSections().add(section);

            LessonSection saved = lessonSectionRepository.save(section);
            lessonRepository.save(lesson);

            return lessonSectionMapper.toResponse(saved);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "lesson-section-list", allEntries = true)
    public List<LessonSectionResponse> addAll(List<LessonSectionRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return List.of();
        }

        Set<Long> lessonIds = requests.stream()
                .map(LessonSectionRequest::getLessonId)
                .collect(Collectors.toSet());

        Map<Long, Lesson> lessonMap = lessonRepository.findAllById(lessonIds)
                .stream()
                .collect(Collectors.toMap(Lesson::getId, Function.identity()));

        List<Long> missing = lessonIds.stream()
                .filter(id -> !lessonMap.containsKey(id))
                .toList();

        if (!missing.isEmpty()) {
            throw new NotFoundException("Lessons not found with ids: " + missing);
        }

        List<LessonSection> sections = requests.stream()
                .map(req -> {
                    try {
                        LessonSection section = lessonSectionMapper.toEntity(req);
                        Lesson lesson = lessonMap.get(req.getLessonId());

                        section.setLesson(lesson);
                        lesson.getSections().add(section);

                        if (req.getFile() != null && !req.getFile().isEmpty()) {
                            String path = mediaService.uploadImage(
                                    req.getFile(),
                                    req.getDataType(),
                                    false
                            );
                            section.setDataPath(path);
                        }

                        return section;

                    } catch (IOException e) {
                        throw new RuntimeException("Failed to upload file", e);
                    }
                })
                .toList();

        List<LessonSection> saved = lessonSectionRepository.saveAll(sections);
        lessonRepository.saveAll(lessonMap.values());

        return saved.stream()
                .map(lessonSectionMapper::toResponse)
                .toList();
    }

    @Override
    @Cacheable(
            value = "lesson-section-list",
            key = "'{lesson-section-data}:list:' + #filter.toString()",
            unless = "#result == null"
    )
    public ApiResponse<LessonSectionResponse> getAll(LessonSectionFilter filter) {
        Pageable pageable = PageableUtil.createPageable(filter);

        Page<LessonSection> page = lessonSectionRepository.findAll(
                LessonSectionSpecification.filter(filter),
                pageable
        );

        return ApiResponse.fromPage(page.map(lessonSectionMapper::toResponse));
    }

    @Override
    @Cacheable(
            value = "lesson-section",
            key = "'{lesson-section-data}:id:' + #id"
    )
    public LessonSectionResponse getById(Long id) {
        return lessonSectionMapper.toResponse(findById(id));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "lesson-section", key = "'{lesson-section-data}:id:' + #id"),
            @CacheEvict(value = "lesson-section-list", allEntries = true)
    })
    public LessonSectionResponse update(Long id, LessonSectionRequest request) {
        LessonSection section = findById(id);
        lessonSectionMapper.updateEntityFromRequest(request, section);
        return lessonSectionMapper.toResponse(lessonSectionRepository.save(section));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "lesson-section", key = "'{lesson-section-data}:id:' + #id"),
            @CacheEvict(value = "lesson-section-list", allEntries = true)
    })
    public void delete(Long id) {
        lessonSectionRepository.delete(findById(id));
    }

    private LessonSection findById(Long id) {
        return lessonSectionRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("LessonSection not found with id: " + id)
                );
    }
}