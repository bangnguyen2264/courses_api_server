package com.example.course.service.impl;

import com.example.course.exception.NotFoundException;
import com.example.course.filter.ChapterFilter;
import com.example.course.mapper.ChapterMapper;
import com.example.course.model.entity.Chapter;
import com.example.course.model.entity.Subject;
import com.example.course.model.request.ChapterRequest;
import com.example.course.model.response.ApiResponse;
import com.example.course.model.response.ChapterDetailResponse;
import com.example.course.model.response.ChapterResponse;
import com.example.course.repository.ChapterRepository;
import com.example.course.repository.SubjectRepository;
import com.example.course.service.ChapterService;
import com.example.course.specification.ChapterSpecification;
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
public class ChapterServiceImpl implements ChapterService {

    private final ChapterRepository chapterRepository;
    private final SubjectRepository subjectRepository;
    private final ChapterMapper chapterMapper;

    @Override
    @Transactional
    @CacheEvict(value = "chapter-list", allEntries = true)
    public ChapterResponse create(ChapterRequest chapterRequest) {
        Chapter chapter = chapterMapper.toEntity(chapterRequest);
        Subject subject = subjectRepository.findById(chapterRequest.getSubjectId())
                .orElseThrow(() -> new NotFoundException("Subject not found with id: " + chapterRequest.getSubjectId()));
        chapter.setSubject(subject);
        subject.getChapters().add(chapter);
        Chapter saved = chapterRepository.save(chapter);
        subjectRepository.save(subject);
        return ChapterResponse.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "chapter-list", allEntries = true)
    public List<ChapterResponse> addAll(List<ChapterRequest> chapterRequests) {
        if (chapterRequests == null || chapterRequests.isEmpty()) {
            return List.of();
        }

        Set<Long> subjectIds = chapterRequests.stream()
                .map(ChapterRequest::getSubjectId)
                .collect(Collectors.toSet());

        List<Subject> subjects = subjectRepository.findAllById(subjectIds);
        Map<Long, Subject> subjectMap = subjects.stream()
                .collect(Collectors.toMap(Subject::getId, Function.identity()));

        List<Long> missingSubjects = subjectIds.stream()
                .filter(id -> !subjectMap.containsKey(id))
                .toList();

        if (!missingSubjects.isEmpty()) {
            throw new NotFoundException("Subjects not found with ids: " + missingSubjects);
        }

        List<Chapter> chapters = chapterRequests.stream()
                .map(req -> {
                    Chapter chapter = chapterMapper.toEntity(req);
                    Subject subject = subjectMap.get(req.getSubjectId());
                    chapter.setSubject(subject);
                    subject.getChapters().add(chapter);
                    return chapter;
                })
                .toList();

        List<Chapter> savedChapters = chapterRepository.saveAll(chapters);
        subjectRepository.saveAll(subjects);

        return savedChapters.stream()
                .map(ChapterResponse::toResponse)
                .toList();
    }


    @Override
    @Cacheable(
            value = "chapter-list",
            key = "T(java.util.Objects).hash(#filter)"
    )
    public ApiResponse<ChapterDetailResponse> getAll(ChapterFilter filter) {
        Pageable pageable = PageableUtil.createPageable(filter);
        Page<Chapter> chapters = chapterRepository.findAll(ChapterSpecification.filter(filter), pageable);
        Page<ChapterDetailResponse> result = chapters.map(ChapterDetailResponse::toResponse);
        return ApiResponse.fromPage(result);
    }

    @Override
    @Cacheable(value = "chapter", key = "#id")
    public ChapterDetailResponse getById(Long id) {
        return ChapterDetailResponse.toResponse(findById(id));
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "chapter", key = "#id"),
                    @CacheEvict(value = "chapter-list", allEntries = true)
            }
    )
    public ChapterResponse update(Long id, ChapterRequest chapterRequest) {
        Chapter chapter = findById(id);
        chapterMapper.updateEntityFromRequest(chapterRequest, chapter);
        chapter = chapterRepository.save(chapter);
        return ChapterResponse.toResponse(chapter);
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "chapter", key = "#id"),
                    @CacheEvict(value = "chapter-list", allEntries = true)
            }
    )
    public void delete(Long id) {
        Chapter chapter = findById(id);
        chapterRepository.delete(chapter);
    }

    public Chapter findById(Long id) {
        return chapterRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Chapter not found with id: " + id)
        );
    }
}