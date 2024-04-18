package domain.usecase;

import domain.entity.learner.Learner;
import domain.entity.lesson.Lesson;
import domain.entity.lesson.LessonStatus;
import domain.entity.lesson.RegisteredLesson;
import domain.util.Result;

public class BookLessonUseCase {
    /**
     * Book a new lesson
     *
     * @param lesson  lesson to be booked
     * @param learner learner to book a lesson
     * @return Result with {@link Result#NO_VALUE} on success,
     * otherwise, {@link Result#error(Object)} with {@link Error} as its error data
     */
    public Result<Object, Error> bookLesson(Lesson lesson, Learner learner) {
        if (learner.getGrade() + 1 < lesson.getGrade()) {
            return Result.error(Error.LESSON_ABOVE_LEARNER_GRADE);
        }

        if (lesson.getGrade() < learner.getGrade()) {
            return Result.error(Error.LESSON_BELOW_LEARNER_GRADE);
        }

        if (lesson.getRegisteredLearners().contains(learner)) {
            return Result.error(Error.DUPLICATE_BOOKING);
        }

        if (lesson.getRegisteredLearners().size() >= 4) {
            int activeLessons = 0;
            for (Learner ln : lesson.getRegisteredLearners()) {
                if (ln.getLessonStatus(lesson) != LessonStatus.CANCELLED) {
                    activeLessons++;
                }
            }

            if (activeLessons >= 4) {
                return Result.error(Error.LESSON_FULLY_BOOKED);
            }
        }

        lesson.addLearner(learner);
        learner.registerNewLesson(new RegisteredLesson(lesson, LessonStatus.BOOKED));

        return Result.success(Result.NO_VALUE);
    }

    public enum Error {
        LESSON_ABOVE_LEARNER_GRADE, LESSON_BELOW_LEARNER_GRADE, DUPLICATE_BOOKING, LESSON_FULLY_BOOKED
    }
}
