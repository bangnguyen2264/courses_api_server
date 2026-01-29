package com.example.course.service.impl;

import com.example.course.exception.NotFoundException;
import com.example.course.filter.LessonFilter;
import com.example.course.mapper.LessonMapper;
import com.example.course.model.entity.Chapter;
import com.example.course.model.entity.Lesson;
import com.example.course.model.request.LessonRequest;
import com.example.course.model.response.ApiResponse;
import com.example.course.model.response.LessonResponse;
import com.example.course.repository.ChapterRepository;
import com.example.course.repository.LessonRepository;
import com.example.course.service.LessonService;
import com.example.course.specification.LessonSpecification;
import com.example.course.utils.PageableUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;
    private final ChapterRepository chapterRepository;

    @Override
    @Transactional
    @CacheEvict(value = "lesson-list", allEntries = true)
    public LessonResponse create(LessonRequest request) {
        Lesson lesson = lessonMapper.toEntity(request);

        Chapter chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() ->
                        new NotFoundException("Chapter not found with id " + request.getChapterId())
                );

        lesson.setChapter(chapter);
        chapter.getLessons().add(lesson);

        Lesson saved = lessonRepository.save(lesson);
        chapterRepository.save(chapter);

        return LessonResponse.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "lesson-list", allEntries = true)
    public List<LessonResponse> addAll(List<LessonRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return List.of();
        }

        Set<Long> chapterIds = requests.stream()
                .map(LessonRequest::getChapterId)
                .collect(Collectors.toSet());

        Map<Long, Chapter> chapterMap = chapterRepository.findAllById(chapterIds)
                .stream()
                .collect(Collectors.toMap(Chapter::getId, Function.identity()));

        List<Long> missing = chapterIds.stream()
                .filter(id -> !chapterMap.containsKey(id))
                .toList();

        if (!missing.isEmpty()) {
            throw new NotFoundException("Chapters not found with ids: " + missing);
        }

        List<Lesson> lessons = requests.stream()
                .map(req -> {
                    Lesson lesson = lessonMapper.toEntity(req);
                    Chapter chapter = chapterMap.get(req.getChapterId());
                    lesson.setChapter(chapter);
                    chapter.getLessons().add(lesson);
                    return lesson;
                })
                .toList();

        List<Lesson> saved = lessonRepository.saveAll(lessons);
        chapterRepository.saveAll(chapterMap.values());

        return saved.stream()
                .map(LessonResponse::toResponse)
                .toList();
    }

    @Override
    @Cacheable(
            value = "lesson-list",
            key = "'{lesson-data}:list:' + #filter.toString()",
            unless = "#result == null"
    )
    public ApiResponse<LessonResponse> getAll(LessonFilter filter) {
        Pageable pageable = PageableUtil.createPageable(filter);

        Page<Lesson> page = lessonRepository.findAll(
                LessonSpecification.filter(filter),
                pageable
        );

        return ApiResponse.fromPage(page.map(LessonResponse::toResponse));
    }

    @Override
    @Cacheable(
            value = "lesson",
            key = "'{lesson-data}:id:' + #id"
    )
    public LessonResponse getById(Long id) {
        return LessonResponse.toResponse(findById(id));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "lesson", key = "'{lesson-data}:id:' + #id"),
            @CacheEvict(value = "lesson-list", allEntries = true)
    })
    public LessonResponse update(Long id, LessonRequest request) {
        Lesson lesson = findById(id);
        lessonMapper.updateEntityFromRequest(request, lesson);
        return LessonResponse.toResponse(lessonRepository.save(lesson));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "lesson", key = "'{lesson-data}:id:' + #id"),
            @CacheEvict(value = "lesson-list", allEntries = true)
    })
    public void delete(Long id) {
        lessonRepository.delete(findById(id));
    }

    private Lesson findById(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Lesson not found with id: " + id)
                );
    }
}