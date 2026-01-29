package com.example.course.config;

import com.example.course.constant.DataType;
import com.example.course.constant.ExamDuration;
import com.example.course.model.entity.*;
import com.example.course.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private static final int SUBJECT_COUNT = 1000;
    private static final int CHAPTER_PER_SUBJECT = 35;
    private static final int LESSON_PER_CHAPTER = 5;
    private static final int SECTION_PER_LESSON = 4;
    private static final int QUIZ_PER_SUBJECT = 100;

    private final SubjectRepository subjectRepository;
    private final ChapterRepository chapterRepository;
    private final LessonRepository lessonRepository;
    private final LessonSectionRepository lessonSectionRepository;
    private final QuizRepository quizRepository;
    private final ExamRepository examRepository;

    private final EntityManager em;

    private final Faker faker = new Faker();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if (subjectRepository.count() > 0) {
            log.info("Data already exists → skip seeding");
            return;
        }

        long start = System.currentTimeMillis();
        log.info("🚀 ULTRA FAST SEEDING START");

        /* =========================================
           CREATE ALL DATA IN MEMORY FIRST
         ========================================= */

        List<Subject> subjects = new ArrayList<>(SUBJECT_COUNT);
        List<Chapter> chapters = new ArrayList<>();
        List<Lesson> lessons = new ArrayList<>();
        List<LessonSection> sections = new ArrayList<>();
        List<Quiz> quizzes = new ArrayList<>();
        List<Exam> exams = new ArrayList<>();

        for (int s = 1; s <= SUBJECT_COUNT; s++) {

            Subject subject = Subject.builder()
                    .name("Course " + s)
                    .description(faker.lorem().sentence())
                    .status(true)
                    .position(s)
                    .build();
            subject .setCreatedBy("admin@gmail.com");
            subject.setUpdatedBy("admin@gmail.com");

            subjects.add(subject);

            /* ---------- chapters ---------- */
            List<Quiz> subjectQuizzes = new ArrayList<>();

            for (int c = 1; c <= CHAPTER_PER_SUBJECT; c++) {

                Chapter chapter = Chapter.builder()
                        .subject(subject)
                        .title("Chapter " + c)
                        .position(c)
                        .build();

                chapters.add(chapter);

                /* ---------- lessons ---------- */
                for (int l = 1; l <= LESSON_PER_CHAPTER; l++) {

                    Lesson lesson = Lesson.builder()
                            .chapter(chapter)
                            .title("Lesson " + l)
                            .build();

                    lessons.add(lesson);

                    /* ---------- sections ---------- */
                    for (int sec = 1; sec <= SECTION_PER_LESSON; sec++) {

                        LessonSection section = LessonSection.builder()
                                .lesson(lesson)
                                .title("Section " + sec)
                                .content(faker.lorem().paragraph())
                                .dataType(sec % 2 == 0 ? DataType.VIDEO : DataType.TEXT)
                                .position(sec)
                                .build();

                        sections.add(section);
                    }
                }
            }

            /* ---------- quizzes ---------- */
            for (int q = 0; q < QUIZ_PER_SUBJECT; q++) {

                Quiz quiz = Quiz.builder()
                        .subject(subject)
                        .question(faker.lorem().sentence())
                        .options(mapper.writeValueAsString(List.of("A", "B", "C", "D")))
                        .correctAnswers("[0]")
                        .multipleChoice(false)
                        .build();

                quizzes.add(quiz);
                subjectQuizzes.add(quiz);
            }

            /* ---------- exams ---------- */
            exams.add(createExam(subject, "15 phút", ExamDuration.MIN_15, subjectQuizzes.subList(0, 20)));
            exams.add(createExam(subject, "Giữa kỳ", ExamDuration.MIN_45, subjectQuizzes.subList(0, 50)));
            exams.add(createExam(subject, "Cuối kỳ", ExamDuration.MIN_60, subjectQuizzes.subList(0, 60)));

            if (s % 100 == 0)
                log.info("[BUILD] {} subjects prepared...", s);
        }

        /* =========================================
           SAVE IN HUGE BATCH (FAST PART)
         ========================================= */

        log.info("💾 Saving subjects...");
        subjectRepository.saveAll(subjects);
        flush();

        log.info("💾 Saving chapters...");
        chapterRepository.saveAll(chapters);
        flush();

        log.info("💾 Saving lessons...");
        lessonRepository.saveAll(lessons);
        flush();

        log.info("💾 Saving sections...");
        lessonSectionRepository.saveAll(sections);
        flush();

        log.info("💾 Saving quizzes...");
        quizRepository.saveAll(quizzes);
        flush();

        log.info("💾 Saving exams...");
        examRepository.saveAll(exams);
        flush();

        long end = System.currentTimeMillis();

        log.info("=======================================");
        log.info("✅ DONE");
        log.info("Subjects : {}", subjects.size());
        log.info("Chapters : {}", chapters.size());
        log.info("Lessons  : {}", lessons.size());
        log.info("Sections : {}", sections.size());
        log.info("Quizzes  : {}", quizzes.size());
        log.info("Exams    : {}", exams.size());
        log.info("⏱ Time: {}s", (end - start) / 1000);
        log.info("=======================================");
    }

    private Exam createExam(Subject subject, String title, ExamDuration duration, List<Quiz> quizzes) {
        return Exam.builder()
                .title(title)
                .subject(subject)
                .duration(duration)
                .quizzes(quizzes)
                .build();
    }

    private void flush() {
        em.flush();
        em.clear();
    }
}
