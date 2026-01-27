package com.example.course.mapper;

import com.example.course.model.entity.Quiz;
import com.example.course.model.entity.Subject;
import com.example.course.model.request.QuizRequest;
import com.example.course.model.response.QuizResponse;
import com.example.course.model.response.QuizReviewResponse;
import com.example.course.utils.JsonUtils;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
@Mapper(componentModel = "spring")
public interface QuizMapper {

    default QuizResponse toResponse(Quiz quiz) {
        if (quiz == null) return null;

        QuizResponse res = new QuizResponse();
        res.setId(quiz.getId());
        res.setQuestion(quiz.getQuestion());
        res.setOptions(JsonUtils.fromJsonToList(quiz.getOptions(), String.class));
        res.setMultipleChoice(quiz.isMultipleChoice());
        return res;
    }

    default QuizReviewResponse toReviewResponse(Quiz quiz) {
        if (quiz == null) return null;
        QuizReviewResponse res = new QuizReviewResponse();
        res.setId(quiz.getId());
        res.setQuestion(quiz.getQuestion());
        res.setOptions(JsonUtils.fromJsonToList(quiz.getOptions(), String.class));
        res.setCorrectAnswers(quiz.getCorrectAnswers());
        res.setMultipleChoice(quiz.isMultipleChoice());
        return res;
    }

    default Quiz toEntity(QuizRequest request, Subject subject) {
        if (request == null) return null;

        return Quiz.builder()
                .question(request.getQuestion())
                .options(JsonUtils.toJson(request.getOptions()))
                .correctAnswers(JsonUtils.toJson(request.getCorrectAnswers()))
                .multipleChoice(request.isMultipleChoice())
                .subject(subject)
                .build();
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    default void updateEntityFromRequest(QuizRequest request,
                                         @MappingTarget Quiz entity) {

        if (request.getQuestion() != null) {
            entity.setQuestion(request.getQuestion());
        }

        if (request.getOptions() != null) {
            entity.setOptions(JsonUtils.toJson(request.getOptions()));
        }

        if (request.getCorrectAnswers() != null) {
            entity.setCorrectAnswers(JsonUtils.toJson(request.getCorrectAnswers()));
        }

        entity.setMultipleChoice(request.isMultipleChoice());
    }
}
