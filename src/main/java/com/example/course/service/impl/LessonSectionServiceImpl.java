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
    public LessonSectionResponse create(LessonSectionRequest lessonSectionRequest) {
        try {
            LessonSection lessonSection = lessonSectionMapper.toEntity(lessonSectionRequest);

            Lesson lesson = lessonRepository.findById(lessonSectionRequest.getLessonId())
                    .orElseThrow(() ->
                            new NotFoundException("Lesson not found with id: " + lessonSectionRequest.getLessonId())
                    );

            if (lessonSectionRequest.getDataType() != DataType.OTHER
                    && lessonSectionRequest.getFile() != null
                    && !lessonSectionRequest.getFile().isEmpty()) {

                String dataPath = mediaService.uploadImage(
                        lessonSectionRequest.getFile(),
                        lessonSectionRequest.getDataType(),
                        false
                );
                lessonSection.setDataPath(dataPath);
            }

            lessonSection.setLesson(lesson);
            lesson.getSections().add(lessonSection);

            LessonSection saved = lessonSectionRepository.save(lessonSection);
            lessonRepository.save(lesson);

            return LessonSectionResponse.toResponse(saved);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "lesson-section-list", allEntries = true)
    public List<LessonSectionResponse> addAll(List<LessonSectionRequest> sectionRequests) {
        if (sectionRequests == null || sectionRequests.isEmpty()) {
            return List.of();
        }

        Set<Long> lessonIds = sectionRequests.stream()
                .map(LessonSectionRequest::getLessonId)
                .collect(Collectors.toSet());

        List<Lesson> lessons = lessonRepository.findAllById(lessonIds);

        Map<Long, Lesson> lessonMap = lessons.stream()
                .collect(Collectors.toMap(Lesson::getId, Function.identity()));

        List<Long> missingLessons = lessonIds.stream()
                .filter(id -> !lessonMap.containsKey(id))
                .toList();

        if (!missingLessons.isEmpty()) {
            throw new NotFoundException("Lessons not found with ids: " + missingLessons);
        }

        List<LessonSection> sections = sectionRequests.stream()
                .map(req -> {
                    try {
                        LessonSection section = lessonSectionMapper.toEntity(req);
                        Lesson lesson = lessonMap.get(req.getLessonId());

                        section.setLesson(lesson);
                        lesson.getSections().add(section);

                        if (req.getFile() != null && !req.getFile().isEmpty()) {
                            String dataPath = mediaService.uploadImage(
                                    req.getFile(),
                                    req.getDataType(),
                                    false
                            );
                            section.setDataPath(dataPath);
                        }

                        return section;

                    } catch (IOException e) {
                        throw new RuntimeException(
                                "Failed to upload file for lessonSection: " + req.getTitle(),
                                e
                        );
                    }
                })
                .toList();

        List<LessonSection> savedSections = lessonSectionRepository.saveAll(sections);

        lessonRepository.saveAll(lessons);

        return savedSections.stream()
                .map(LessonSectionResponse::toResponse)
                .toList();
    }

    @Override
    @Cacheable(
            value = "lesson-section-list",
            key = "T(java.util.Objects).hash(#lessonSectionFilter)"
    )
    public ApiResponse<LessonSectionResponse> getAll(LessonSectionFilter lessonSectionFilter) {
        Pageable pageable = PageableUtil.createPageable(lessonSectionFilter);

        Page<LessonSection> lessonSections =
                lessonSectionRepository.findAll(
                        LessonSectionSpecification.filter(lessonSectionFilter),
                        pageable
                );

        Page<LessonSectionResponse> results =
                lessonSections.map(LessonSectionResponse::toResponse);

        return ApiResponse.fromPage(results);
    }

    @Override
    @Cacheable(value = "lesson-section", key = "#id")
    public LessonSectionResponse getById(Long id) {
        return lessonSectionMapper.toResponse(findById(id));
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "lesson-section", key = "#id"),
                    @CacheEvict(value = "lesson-section-list", allEntries = true)
            }
    )
    public LessonSectionResponse update(Long id, LessonSectionRequest lessonSectionRequest) {
        LessonSection lessonSection = findById(id);
        lessonSectionMapper.updateEntityFromRequest(lessonSectionRequest, lessonSection);
        LessonSection saved = lessonSectionRepository.save(lessonSection);
        return lessonSectionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "lesson-section", key = "#id"),
                    @CacheEvict(value = "lesson-section-list", allEntries = true)
            }
    )
    public void delete(Long id) {
        LessonSection lessonSection = findById(id);
        lessonSectionRepository.delete(lessonSection);
    }

    private LessonSection findById(Long id) {
        return lessonSectionRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("LessonSection not found with id: " + id)
                );
    }
}
