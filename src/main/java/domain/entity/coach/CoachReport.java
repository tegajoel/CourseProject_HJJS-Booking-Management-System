package domain.entity.coach;

import domain.entity.Rating;
import domain.entity.lesson.Lesson;

import java.util.List;
import java.util.Map;

public record CoachReport(
        String coachName,
        int numberOfLessonsTaught,
        List<Lesson> lessonsTaught,
        Rating averageLessonRating,
        List<Map<Rating, Lesson>> averageRatingPerLesson
) {
}