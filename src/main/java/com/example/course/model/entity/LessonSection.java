    package com.example.course.model.entity;

    import com.example.course.constant.DataType;
    import jakarta.persistence.*;
    import lombok.*;

    import java.io.Serializable;

    @Entity
    @Table(name = "lesson_section")
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class LessonSection extends BaseEntity{

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "lesson_id", nullable = false)
        private Lesson lesson;

        @Enumerated(EnumType.STRING)
        @Column(name = "section_type", nullable = false)
        private DataType dataType;

        @Column(columnDefinition = "TEXT")
        private String title;

        @Column(columnDefinition = "TEXT")
        private String description;

        @Column(columnDefinition = "TEXT")
        private String content;

        private String dataPath;

        private Integer position;
    }
