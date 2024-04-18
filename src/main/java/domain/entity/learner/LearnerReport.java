package domain.entity.learner;

import domain.entity.lesson.Lesson;

import java.util.List;
public record LearnerReport(
        String learnerName,
        int learnerAge,
        String learnerGender,
        int currentGrade,
        int totalRegisteredLessons,
        List<Lesson> bookedLessons,
        List<Lesson> attendedLessons,
        List<Lesson> cancelledLessons
) {

}
