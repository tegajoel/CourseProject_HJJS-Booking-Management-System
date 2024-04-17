package domain.usecase;

import domain.entity.Learner;
import domain.entity.Review;
import domain.entity.lesson.Lesson;
import domain.util.Result;

public class AttendLessonUseCase {

    /**
     * Attend a lesson
     * @param lesson lesson to attend
     * @param learner attending learner
     * @param reviewProvider callback which is called after the learner and lesson has been validated
     * @return result with an empty object on success, otherwise, an error result with {@link Error} as its data
     */
    public Result<Object, Error> execute(Lesson lesson, Learner learner, ReviewProvider reviewProvider){
        return Result.success(Result.NO_VALUE);
    }

    /**
     * Interface to be called when a review is needed for the lesson
     */
    public interface ReviewProvider {
        /**
         * Provide a review
         * @return the learner's review of the lesson
         */
        Review provideReview();
    }

    public enum Error{
        LEARNER_NOT_REGISTERED_TO_LESSON, LESSON_ALREADY_ATTENDED, INVALID_REVIEW_RATING, EMPTY_REVIEW_MESSAGE

    }
}
