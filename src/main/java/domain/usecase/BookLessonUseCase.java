package domain.usecase;

import domain.entity.Learner;
import domain.entity.lesson.Lesson;
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
    public Result<Object, Error> execute(Lesson lesson, Learner learner) {
        return Result.success(Result.NO_VALUE);
    }

    public enum Error {
        LESSON_ABOVE_LEARNER_GRADE, LESSON_BELOW_LEARNER_GRADE, DUPLICATE_BOOKING, LESSON_FULLY_BOOKED
    }
}
