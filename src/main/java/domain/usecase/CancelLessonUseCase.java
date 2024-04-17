package domain.usecase;

import domain.entity.Learner;
import domain.entity.lesson.Lesson;
import domain.entity.lesson.LessonStatus;
import domain.util.Result;

public class CancelLessonUseCase {

    /**
     * Cancel the booked lesson of this learner
     *
     * @param lessonToCancel lesson to cancel
     * @param learner        the learner
     * @return Result with empty object on success, and {@link Error} when an error occurs
     */
    public Result<Object, Error> execute(Lesson lessonToCancel, Learner learner) {
        if (!learner.hasLessonRegistered(lessonToCancel)) {
            return Result.error(Error.NO_BOOKING_FOR_LESSON);
        }

        return switch (learner.getLessonStatus(lessonToCancel)) {
            case BOOKED -> {
                learner.updateRegisteredLessonStatus(lessonToCancel, LessonStatus.CANCELLED);
                yield Result.success(Result.NO_VALUE);
            }
            case ATTENDED -> Result.error(Error.LESSON_ALREADY_ATTENDED);
            case CANCELLED -> Result.error(Error.LESSON_ALREADY_CANCELLED);
        };
    }

    public enum Error {
        LESSON_ALREADY_ATTENDED, LESSON_ALREADY_CANCELLED, NO_BOOKING_FOR_LESSON
    }
}
