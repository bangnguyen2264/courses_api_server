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
    public LessonResponse create(LessonRequest lessonRequest) {
        Lesson lesson = lessonMapper.toEntity(lessonRequest);

        Chapter chapter = chapterRepository.findById(lessonRequest.getChapterId())
                .orElseThrow(() ->
                        new NotFoundException("Chapter not found with id " + lessonRequest.getChapterId())
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
    public List<LessonResponse> addAll(List<LessonRequest> lessonRequests) {
        if (lessonRequests == null || lessonRequests.isEmpty()) {
            return List.of();
        }

        Set<Long> chapterIds = lessonRequests.stream()
                .map(LessonRequest::getChapterId)
                .collect(Collectors.toSet());

        List<Chapter> chapters = chapterRepository.findAllById(chapterIds);

        Map<Long, Chapter> chapterMap = chapters.stream()
                .collect(Collectors.toMap(Chapter::getId, Function.identity()));

        List<Long> missingChapters = chapterIds.stream()
                .filter(id -> !chapterMap.containsKey(id))
                .toList();

        if (!missingChapters.isEmpty()) {
            throw new NotFoundException("Chapters not found with ids: " + missingChapters);
        }

        List<Lesson> lessons = lessonRequests.stream()
                .map(req -> {
                    Lesson lesson = lessonMapper.toEntity(req);
                    Chapter chapter = chapterMap.get(req.getChapterId());
                    lesson.setChapter(chapter);
                    chapter.getLessons().add(lesson);
                    return lesson;
                })
                .toList();

        List<Lesson> savedLessons = lessonRepository.saveAll(lessons);

        chapterRepository.saveAll(chapters);

        return savedLessons.stream()
                .map(LessonResponse::toResponse)
                .toList();
    }

    @Override
    @Cacheable(
            value = "lesson-list",
            key = "T(java.util.Objects).hash(#lessonFilter)"
    )
    public ApiResponse<LessonResponse> getAll(LessonFilter lessonFilter) {
        Pageable pageable = PageableUtil.createPageable(lessonFilter);
        Page<Lesson> lessons = lessonRepository.findAll(LessonSpecification.filter(lessonFilter), pageable);
        Page<LessonResponse> result = lessons.map(LessonResponse::toResponse);
        return ApiResponse.fromPage(result);
    }

    @Override
    @Cacheable(value = "lesson", key = "#id")
    public LessonResponse getById(Long id) {
        return LessonResponse.toResponse(findById(id));
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "lesson", key = "#id"),
                    @CacheEvict(value = "lesson-list", allEntries = true)
            }
    )
    public LessonResponse update(Long id, LessonRequest lessonRequest) {
        Lesson lesson = findById(id);
        lessonMapper.updateEntityFromRequest(lessonRequest, lesson);
        Lesson saved = lessonRepository.save(lesson);
        return LessonResponse.toResponse(saved);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "lesson", key = "#id"),
                    @CacheEvict(value = "lesson-list", allEntries = true)
            }
    )
    public void delete(Long id) {
        Lesson lesson = findById(id);
        lessonRepository.delete(lesson);
    }

    public Lesson findById(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Lesson not found with id: " + id)
                );
    }
}
