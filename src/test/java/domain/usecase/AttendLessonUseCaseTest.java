package domain.usecase;

import domain.entity.Coach;
import domain.entity.Learner;
import domain.entity.Review;
import domain.entity.lesson.Lesson;
import domain.entity.lesson.LessonStatus;
import domain.entity.lesson.RegisteredLesson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AttendLessonUseCaseTest {
    private AttendLessonUseCase useCase;
    private ReviewProviderImpl reviewProvider;
    private Lesson testLesson;
    private Learner testLearner;

    @BeforeEach
    void setUp() {
        useCase = new AttendLessonUseCase();
        reviewProvider = new ReviewProviderImpl();
        reviewProvider.setReview(new Review("Great lesson", 5));
        Coach coach1 = new Coach("Helen Paul");
        testLesson = new Lesson("Diving1", 3, coach1, LocalDate.now());
        String validPhoneNumber = "08012345680";
        testLearner = new Learner("John doe", "Male", 5, 2, validPhoneNumber, validPhoneNumber);
    }

    @Test
    void attendLesson_unattendedLesson_succeeds() {
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));

        assertTrue(useCase.attendLesson(testLesson, testLearner, reviewProvider).isSuccess());
    }

    @Test
    void attendLesson_succeeds_learnerStatusUpdatedToAttended() {
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));
        useCase.attendLesson(testLesson, testLearner, reviewProvider);

        assertEquals(LessonStatus.ATTENDED, testLearner.getLessonStatus(testLesson));
    }

    @Test
    void attendLesson_sameGradeLessonAttended_learnerGradeRemainsSame() {
        testLearner.setGrade(2);
        testLesson.setGrade(2);
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));
        useCase.attendLesson(testLesson, testLearner, reviewProvider);

        assertEquals(2, testLearner.getGrade());
    }

    @Test
    void attendLesson_invalidReview_LessonStatusNotChanged() {
        testLearner.setGrade(2);
        testLesson.setGrade(3);
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));

        LessonStatus expectedResult = testLearner.getLessonStatus(testLesson);
        reviewProvider.setReview(new Review("", -5));
        useCase.attendLesson(testLesson, testLearner, reviewProvider);

        assertEquals(expectedResult, testLearner.getLessonStatus(testLesson));
    }

    @Test
    void attendLesson_invalidReview_LearnerGradeNotChanged() {
        testLearner.setGrade(2);
        testLesson.setGrade(3);
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));

        int currentGrade = testLearner.getGrade();
        reviewProvider.setReview(new Review("", -5));
        useCase.attendLesson(testLesson, testLearner, reviewProvider);

        assertEquals(currentGrade, testLearner.getGrade());
    }

    @Test
    void attendLesson_oneGradeHigherLessonAttended_learnerGradeIncrementByOne() {
        testLearner.setGrade(0);
        testLesson.setGrade(1);
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));
        useCase.attendLesson(testLesson, testLearner, reviewProvider);

        assertEquals(1, testLearner.getGrade());
    }

    @Test
    void attendLesson_attendHigherGradeLesson_learnerGradeUpdatedToLessonGrade() {
        testLearner.setGrade(2);
        testLesson.setGrade(4);
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));
        useCase.attendLesson(testLesson, testLearner, reviewProvider);

        assertEquals(4, testLearner.getGrade());
    }

    @Test
    void attendLesson_lowerGradeLesson_learnerGradeRemainsSame() {
        testLearner.setGrade(3);
        testLesson.setGrade(2);
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));
        useCase.attendLesson(testLesson, testLearner, reviewProvider);

        assertEquals(3, testLearner.getGrade());
    }

    @Test
    void attendLesson_lessonAlreadyAttended_fails() {
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.ATTENDED));

        assertFalse(useCase.attendLesson(testLesson, testLearner, reviewProvider).isSuccess());
    }

    @Test
    void attendLesson_lessonAlreadyAttended_failsWithCorrectError() {
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.ATTENDED));
        var result = useCase.attendLesson(testLesson, testLearner, reviewProvider);

        assertEquals(AttendLessonUseCase.Error.LESSON_ALREADY_ATTENDED, result.getError());
    }

    @Test
    void attendLesson_unRegisteredLesson_fails() {
        assertFalse(useCase.attendLesson(testLesson, testLearner, reviewProvider).isSuccess());
    }

    @Test
    void attendLesson_unRegisteredLesson_failsWithCorrectError() {
        var result = useCase.attendLesson(testLesson, testLearner, reviewProvider);

        assertEquals(AttendLessonUseCase.Error.LEARNER_NOT_REGISTERED_TO_LESSON, result.getError());
    }

    @Test
    void attendLesson_succeeds_provideFeedbackCalled() {
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));
        useCase.attendLesson(testLesson, testLearner, reviewProvider);

        assertTrue(reviewProvider.numberOfTimesCalled > 0);
    }

    @Test
    void attendLesson_succeedsWithValidFeedback_FeedbackAddedToLesson() {
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));

        Review review = new Review("Great!", 5);
        reviewProvider.setReview(review);
        useCase.attendLesson(testLesson, testLearner, reviewProvider);

        assertTrue(testLesson.getReviews().contains(review));
    }

    @Test
    void attendLesson_succeeds_provideFeedbackCalledOnlyOnce() {
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));
        useCase.attendLesson(testLesson, testLearner, reviewProvider);

        assertEquals(1, reviewProvider.numberOfTimesCalled);
    }

    @Test
    void attendLesson_fails_provideFeedbackNotCalled() {
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.ATTENDED));
        useCase.attendLesson(testLesson, testLearner, reviewProvider);

        assertEquals(0, reviewProvider.numberOfTimesCalled);
    }

    @Test
    void attendLesson_validReview_succeeds() {
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));
        reviewProvider.setReview(new Review("Great Lesson!", 3));


        assertTrue(useCase.attendLesson(testLesson, testLearner, reviewProvider).isSuccess());
    }

    @Test
    void attendLesson_emptyReviewMessage_fails() {
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));
        reviewProvider.setReview(new Review("", 3));


        assertFalse(useCase.attendLesson(testLesson, testLearner, reviewProvider).isSuccess());
    }

    @Test
    void attendLesson_negativeReviewRating_fails() {
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));
        reviewProvider.setReview(new Review("Great Lesson!", -3));


        assertFalse(useCase.attendLesson(testLesson, testLearner, reviewProvider).isSuccess());
    }

    @Test
    void attendLesson_invalidReviewRating_fails() {
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));
        reviewProvider.setReview(new Review("Great Lesson!", 6));


        assertFalse(useCase.attendLesson(testLesson, testLearner, reviewProvider).isSuccess());
    }

    @Test
    void attendLesson_invalidReviewRating_failsWithCorrectError() {
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));
        reviewProvider.setReview(new Review("Great Lesson!", 6));

        var result = useCase.attendLesson(testLesson, testLearner, reviewProvider);

        assertEquals(AttendLessonUseCase.Error.INVALID_REVIEW_RATING, result.getError());
    }

    @Test
    void attendLesson_zeroReviewRating_fails() {
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));
        reviewProvider.setReview(new Review("Great Lesson!", 0));


        assertFalse(useCase.attendLesson(testLesson, testLearner, reviewProvider).isSuccess());
    }

    @Test
    void attendLesson_zeroReviewRating_failsWithCorrectError() {
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));
        reviewProvider.setReview(new Review("Great Lesson!", 0));

        var result = useCase.attendLesson(testLesson, testLearner, reviewProvider);

        assertEquals(AttendLessonUseCase.Error.INVALID_REVIEW_RATING, result.getError());
    }

    @Test
    void attendLesson_negativeReviewRating_failsWithCorrectError() {
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));
        reviewProvider.setReview(new Review("Great Lesson!", -4));

        var result = useCase.attendLesson(testLesson, testLearner, reviewProvider);

        assertEquals(AttendLessonUseCase.Error.INVALID_REVIEW_RATING, result.getError());
    }

    @Test
    void attendLesson_emptyReviewMessage_failsCorrectError() {
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));
        reviewProvider.setReview(new Review("", 300));

        var result = useCase.attendLesson(testLesson, testLearner, reviewProvider);

        assertEquals(AttendLessonUseCase.Error.EMPTY_REVIEW_MESSAGE, result.getError());
    }


    private static class ReviewProviderImpl implements AttendLessonUseCase.ReviewProvider {
        private int numberOfTimesCalled = 0;
        private Review review;

        @Override
        public Review provideReview() {
            numberOfTimesCalled++;
            return review;
        }

        public void setReview(Review review) {
            this.review = review;
        }
    }
}