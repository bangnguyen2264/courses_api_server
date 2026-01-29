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

import java.util.*;
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
    public ChapterResponse create(ChapterRequest request) {
        Chapter chapter = chapterMapper.toEntity(request);

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new NotFoundException("Subject not found with id: " + request.getSubjectId()));

        chapter.setSubject(subject);
        subject.getChapters().add(chapter);

        Chapter saved = chapterRepository.save(chapter);
        subjectRepository.save(subject);

        return ChapterResponse.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "chapter-list", allEntries = true)
    public List<ChapterResponse> addAll(List<ChapterRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return List.of();
        }

        Set<Long> subjectIds = requests.stream()
                .map(ChapterRequest::getSubjectId)
                .collect(Collectors.toSet());

        Map<Long, Subject> subjectMap = subjectRepository.findAllById(subjectIds)
                .stream()
                .collect(Collectors.toMap(Subject::getId, Function.identity()));

        List<Long> missing = subjectIds.stream()
                .filter(id -> !subjectMap.containsKey(id))
                .toList();

        if (!missing.isEmpty()) {
            throw new NotFoundException("Subjects not found with ids: " + missing);
        }

        List<Chapter> chapters = requests.stream()
                .map(req -> {
                    Chapter chapter = chapterMapper.toEntity(req);
                    Subject subject = subjectMap.get(req.getSubjectId());
                    chapter.setSubject(subject);
                    subject.getChapters().add(chapter);
                    return chapter;
                })
                .toList();

        List<Chapter> saved = chapterRepository.saveAll(chapters);
        subjectRepository.saveAll(subjectMap.values());

        return saved.stream()
                .map(ChapterResponse::toResponse)
                .toList();
    }

    @Override
    @Cacheable(
            value = "chapter-list",
            key = "'{chapter-data}:list:' + #filter.toString()",
            unless = "#result == null"
    )
    public ApiResponse<ChapterDetailResponse> getAll(ChapterFilter filter) {
        Pageable pageable = PageableUtil.createPageable(filter);

        Page<Chapter> page =
                chapterRepository.findAll(ChapterSpecification.filter(filter), pageable);

        return ApiResponse.fromPage(page.map(ChapterDetailResponse::toResponse));
    }

    @Override
    @Cacheable(
            value = "chapter",
            key = "'{chapter-data}:id:' + #id"
    )
    public ChapterDetailResponse getById(Long id) {
        return ChapterDetailResponse.toResponse(findById(id));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "chapter", key = "'{chapter-data}:id:' + #id"),
            @CacheEvict(value = "chapter-list", allEntries = true)
    })
    public ChapterResponse update(Long id, ChapterRequest request) {
        Chapter chapter = findById(id);

        chapterMapper.updateEntityFromRequest(request, chapter);

        Chapter saved = chapterRepository.save(chapter);

        return ChapterResponse.toResponse(saved);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "chapter", key = "'{chapter-data}:id:' + #id"),
            @CacheEvict(value = "chapter-list", allEntries = true)
    })
    public void delete(Long id) {
        chapterRepository.delete(findById(id));
    }

    private Chapter findById(Long id) {
        return chapterRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Chapter not found with id: " + id));
    }
}