package com.example.course.config;

import com.example.course.constant.DataType;
import com.example.course.constant.ExamDuration;
import com.example.course.model.entity.*;
import com.example.course.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final SubjectRepository subjectRepository;
    private final ChapterRepository chapterRepository;
    private final LessonRepository lessonRepository;
    private final LessonSectionRepository lessonSectionRepository;
    private final QuizRepository quizRepository;
    private final ExamRepository examRepository;
     private final UserRepository userRepository; // Cần inject User nếu muốn seed ExamResult

    private final Faker faker = new Faker();
    private final ObjectMapper objectMapper = new ObjectMapper(); // Để convert List -> JSON String

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (subjectRepository.count() == 0) {
            log.info("Bắt đầu khởi tạo dữ liệu mẫu...");
            seedSubjectsAndHierarchy();
            log.info("Hoàn tất khởi tạo dữ liệu mẫu!");
        } else {
            log.info("Không tạo dữ liệu mẫu");
        }

    }

    private void seedSubjectsAndHierarchy() throws JsonProcessingException {
        // Tạo 10 môn học
        for (int i = 1; i <= 50; i++) {
            Subject subject = Subject.builder()
                    .name(faker.educator().course() + " " + i) // Thêm số để tránh trùng unique name
                    .description(faker.lorem().sentence(10))
                    .status(true)
                    .position(i)
                    .build();
            subject.setCreatedBy("admin@gmail.com");
            subject.setUpdatedBy("admin@gmail.com");

            // Lưu Subject trước để lấy ID
            Subject savedSubject = subjectRepository.save(subject);

            // 1. Tạo Chapter cho Subject này
            seedChapters(savedSubject);

            // 2. Tạo Quiz cho Subject này
            List<Quiz> quizzes = seedQuizzes(savedSubject);

            // 3. Tạo Exam từ các Quiz vừa tạo
            seedExams(savedSubject, quizzes);
        }
    }

    private void seedChapters(Subject subject) {
        // Mỗi môn có 15 chương
        for (int i = 1; i <= 15; i++) {
            Chapter chapter = Chapter.builder()
                    .subject(subject)
                    .title("Chương " + i + ": " + faker.book().title())
                    .description(faker.lorem().sentence())
                    .position(i)
                    .build();
            chapter.setCreatedBy("admin@gmail.com");
            chapter.setUpdatedBy("admin@gmail.com");

            Chapter savedChapter = chapterRepository.save(chapter);

            // Tạo Lesson cho Chapter này
            seedLessons(savedChapter);
        }
    }

    private void seedLessons(Chapter chapter) {
        // Mỗi chương có 3-5 bài học
        int lessonCount = faker.number().numberBetween(3, 5);
        for (int i = 1; i <= lessonCount; i++) {
            Lesson lesson = Lesson.builder()
                    .chapter(chapter)
                    .title("Bài " + i + ": " + faker.educator().campus())
                    .description(faker.lorem().sentence())
                    .position(i)
                    .build();
            lesson.setCreatedBy("admin@gmail.com");
            lesson.setUpdatedBy("admin@gmail.com");

            Lesson savedLesson = lessonRepository.save(lesson);

            // Tạo Section cho Lesson này
            seedLessonSections(savedLesson);
        }
    }

    private void seedLessonSections(Lesson lesson) {
        // Mỗi bài học có 2 section (ví dụ: 1 Video, 1 Text)

        // Section 1: Text/Lý thuyết
        LessonSection textSection = LessonSection.builder()
                .lesson(lesson)
                .title("Lý thuyết")
                .description("Nội dung bài học")
                .content(faker.lorem().paragraph(5)) // Nội dung dài giả lập
                .dataType(DataType.TEXT) // Đảm bảo bạn có Enum giá trị này (hoặc thay bằng cái bạn có)
                .position(1)
                .build();
        textSection.setCreatedBy("admin@gmail.com");
        textSection.setUpdatedBy("admin@gmail.com");
        lessonSectionRepository.save(textSection);

        // Section 2: Video (Giả lập)
        LessonSection videoSection = LessonSection.builder()
                .lesson(lesson)
                .title("Video hướng dẫn")
                .description("Xem video sau để hiểu rõ hơn")
                .dataPath("https://youtu.be/a3ICNMQW7Ok?si=q_LKEMaoyCQvN3i0") // Link giả
                .dataType(DataType.VIDEO) // Đảm bảo bạn có Enum giá trị này
                .position(2)
                .build();
        videoSection.setCreatedBy("admin@gmail.com");
        videoSection.setUpdatedBy("admin@gmail.com");
        lessonSectionRepository.save(videoSection);
    }

    private List<Quiz> seedQuizzes(Subject subject) throws JsonProcessingException {
        List<Quiz> quizzes = new ArrayList<>();
        // Tạo 100 câu hỏi cho mỗi môn
        for (int i = 0; i < 100; i++) {
            List<String> options = Arrays.asList(
                    faker.lorem().word(),
                    faker.lorem().word(),
                    faker.lorem().word(),
                    faker.lorem().word()
            );

            // Random đáp án đúng (index 0-3)
            List<Integer> correctAnswers = List.of(faker.number().numberBetween(0, 4));

            Quiz quiz = Quiz.builder()
                    .subject(subject)
                    .question(faker.chuckNorris().fact() + "?") // Câu hỏi vui
                    .options(objectMapper.writeValueAsString(options)) // Convert List -> JSON String
                    .correctAnswers(objectMapper.writeValueAsString(correctAnswers)) // Convert List -> JSON String
                    .multipleChoice(false)
                    .build();

            quizzes.add(quizRepository.save(quiz));
        }
        return quizzes;
    }

    private void seedExams(Subject subject, List<Quiz> subjectQuizzes) {
        // Tạo 3 bài kiểm tra: Giữa kỳ và Cuối kỳ
        if (subjectQuizzes.size() >= 60) {

            // Bài 15 phút: 20 câu (Lấy từ index 0 đến 20)
            createExam(subject, "Kiểm tra 15 phút", ExamDuration.MIN_15,
                    new ArrayList<>(subjectQuizzes.subList(0, 20)));

            // Bài Giữa kỳ 45 phút: 50 câu (Lấy từ index 0 đến 50)
            createExam(subject, "Kiểm tra Giữa kỳ", ExamDuration.MIN_45,
                    new ArrayList<>(subjectQuizzes.subList(0, 50)));

            // Bài Cuối kỳ 60 phút: 60 câu (Lấy tất cả 60 câu)
            createExam(subject, "Kiểm tra Cuối kỳ", ExamDuration.MIN_60,
                    new ArrayList<>(subjectQuizzes.subList(0, 60)));
        }
    }

    private void createExam(Subject subject, String title, ExamDuration duration, List<Quiz> quizzes) {
        Exam exam = Exam.builder()
                .title(title)
                .subject(subject)
                .duration(duration) // Đảm bảo Enum khớp với file ExamDuration của bạn
                .quizzes(quizzes)
                .build();
        exam.setCreatedBy("admin@gmail.com");
        exam.setUpdatedBy("admin@gmail.com");
        examRepository.save(exam);
    }
}