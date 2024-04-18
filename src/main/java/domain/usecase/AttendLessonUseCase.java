package domain.usecase;

import domain.entity.Learner;
import domain.entity.Review;
import domain.entity.lesson.Lesson;
import domain.entity.lesson.LessonStatus;
import domain.util.Result;

public class AttendLessonUseCase {

    /**
     * Attend a lesson
     *
     * @param lesson         lesson to attend
     * @param learner        attending learner
     * @param reviewProvider callback which is called after the learner and lesson has been validated
     * @return result with an empty object on success, otherwise, an error result with {@link Error} as its data
     */
    public Result<Object, Error> attendLesson(Lesson lesson, Learner learner, ReviewProvider reviewProvider) {
        if (!learner.hasLessonRegistered(lesson)) return Result.error(Error.LEARNER_NOT_REGISTERED_TO_LESSON);

        if (learner.getLessonStatus(lesson) == LessonStatus.ATTENDED)
            return Result.error(Error.LESSON_ALREADY_ATTENDED);

        Review review = reviewProvider.provideReview();

        if (review.getMessage().isBlank()) return Result.error(Error.EMPTY_REVIEW_MESSAGE);

        if (review.getRating() <= 0 || review.getRating() > 5) return Result.error(Error.INVALID_REVIEW_RATING);

        if (lesson.getGrade() > learner.getGrade()) {
            learner.setGrade(lesson.getGrade());
        }

        learner.updateRegisteredLessonStatus(lesson, LessonStatus.ATTENDED);
        lesson.addReview(review);

        return Result.success(Result.NO_VALUE);
    }

    /**
     * Interface to be called when a review is needed for the lesson
     */
    public interface ReviewProvider {
        /**
         * Provide a review
         *
         * @return the learner's review of the lesson
         */
        Review provideReview();
    }

    public enum Error {
        LEARNER_NOT_REGISTERED_TO_LESSON, LESSON_ALREADY_ATTENDED, INVALID_REVIEW_RATING, EMPTY_REVIEW_MESSAGE
    }
}
