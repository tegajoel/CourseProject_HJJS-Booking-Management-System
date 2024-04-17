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
    private AttendLessonUseCase sut;
    private ReviewProviderImpl reviewProvider;
    private Lesson testLesson;
    private Learner testLearner;

    @BeforeEach
    void setUp() {
        sut = new AttendLessonUseCase();
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

        assertTrue(sut.execute(testLesson, testLearner, reviewProvider).isSuccess());
    }

    @Test
    void attendLesson_succeeds_learnerStatusUpdatedToAttended() {
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));
        sut.execute(testLesson, testLearner, reviewProvider);

        assertEquals(LessonStatus.ATTENDED, testLearner.getLessonStatus(testLesson));
    }

    @Test
    void attendLesson_sameGradeLessonAttended_learnerGradeRemainsSame() {
        testLearner.setGrade(2);
        testLesson.setGrade(2);
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));
        sut.execute(testLesson, testLearner, reviewProvider);

        assertEquals(2, testLearner.getGrade());
    }

    @Test
    void attendLesson_oneGradeHigherLessonAttended_learnerGradeIncrementByOne() {
        testLearner.setGrade(0);
        testLesson.setGrade(1);
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));
        sut.execute(testLesson, testLearner, reviewProvider);

        assertEquals(1, testLearner.getGrade());
    }

    @Test
    void attendLesson_attendHigherGradeLesson_learnerGradeUpdatedToLessonGrade() {
        testLearner.setGrade(2);
        testLesson.setGrade(4);
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));
        sut.execute(testLesson, testLearner, reviewProvider);

        assertEquals(4, testLearner.getGrade());
    }

    @Test
    void attendLesson_lowerGradeLesson_learnerGradeRemainsSame() {
        testLearner.setGrade(3);
        testLesson.setGrade(2);
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));
        sut.execute(testLesson, testLearner, reviewProvider);

        assertEquals(3, testLearner.getGrade());
    }

    @Test
    void attendLesson_lessonAlreadyAttended_fails() {
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.ATTENDED));

        assertFalse(sut.execute(testLesson, testLearner, reviewProvider).isSuccess());
    }

    @Test
    void attendLesson_lessonAlreadyAttended_failsWithCorrectError() {
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.ATTENDED));
        var result = sut.execute(testLesson, testLearner, reviewProvider);

        assertEquals(AttendLessonUseCase.Error.LESSON_ALREADY_ATTENDED, result.getError());
    }

    @Test
    void attendLesson_unRegisteredLesson_fails() {
        assertFalse(sut.execute(testLesson, testLearner, reviewProvider).isSuccess());
    }

    @Test
    void attendLesson_unRegisteredLesson_failsWithCorrectError() {
        var result = sut.execute(testLesson, testLearner, reviewProvider);

        assertEquals(AttendLessonUseCase.Error.LEARNER_NOT_REGISTERED_TO_LESSON, result.getError());
    }

    @Test
    void attendLesson_succeeds_provideFeedbackCalled() {
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));
        sut.execute(testLesson, testLearner, reviewProvider);

        assertTrue(reviewProvider.numberOfTimesCalled > 0);
    }

    @Test
    void attendLesson_succeeds_provideFeedbackCalledOnlyOnce() {
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));
        sut.execute(testLesson, testLearner, reviewProvider);

        assertEquals(1, reviewProvider.numberOfTimesCalled);
    }

    @Test
    void attendLesson_fails_provideFeedbackNotCalled() {
        testLearner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.ATTENDED));
        sut.execute(testLesson, testLearner, reviewProvider);

        assertEquals(0, reviewProvider.numberOfTimesCalled);
    }

    @Test
    void attendLesson_validReview_succeeds() {
        reviewProvider.setReview(new Review("Great Lesson!", 3));


        assertTrue(sut.execute(testLesson, testLearner, reviewProvider).isSuccess());
    }

    @Test
    void attendLesson_emptyReviewMessage_fails() {
        reviewProvider.setReview(new Review("", 3));


        assertFalse(sut.execute(testLesson, testLearner, reviewProvider).isSuccess());
    }

    @Test
    void attendLesson_negativeReviewRating_fails() {
        reviewProvider.setReview(new Review("Great Lesson!", -3));


        assertFalse(sut.execute(testLesson, testLearner, reviewProvider).isSuccess());
    }

    @Test
    void attendLesson_invalidReviewRating_fails() {
        reviewProvider.setReview(new Review("Great Lesson!", 6));


        assertFalse(sut.execute(testLesson, testLearner, reviewProvider).isSuccess());
    }

    @Test
    void attendLesson_invalidReviewRating_failsWithCorrectError() {
        reviewProvider.setReview(new Review("Great Lesson!", 6));

        var result = sut.execute(testLesson, testLearner, reviewProvider);

        assertEquals(AttendLessonUseCase.Error.INVALID_REVIEW_RATING, result.getError());
    }

    @Test
    void attendLesson_negativeReviewRating_failsWithCorrectError() {
        reviewProvider.setReview(new Review("Great Lesson!", -4));

        var result = sut.execute(testLesson, testLearner, reviewProvider);

        assertEquals(AttendLessonUseCase.Error.INVALID_REVIEW_RATING, result.getError());
    }

    @Test
    void attendLesson_emptyReviewMessage_failsCorrectError() {
        reviewProvider.setReview(new Review("", 300));

        var result = sut.execute(testLesson, testLearner, reviewProvider);

        assertEquals(AttendLessonUseCase.Error.EMPTY_REVIEW_MESSAGE, result.getError());
    }


    private static class ReviewProviderImpl implements AttendLessonUseCase.ReviewProvider {
        private int numberOfTimesCalled = 0;
        private Review review;

        @Override
        public Review provideReview() {
            numberOfTimesCalled = 0;
            return review;
        }

        public void setReview(Review review) {
            this.review = review;
        }
    }
}