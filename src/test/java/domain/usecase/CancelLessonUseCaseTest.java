package domain.usecase;

import domain.entity.coach.Coach;
import domain.entity.learner.Learner;
import domain.entity.lesson.Lesson;
import domain.entity.lesson.LessonStatus;
import domain.entity.lesson.RegisteredLesson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CancelLessonUseCaseTest {
    private CancelLessonUseCase useCase;
    private Learner learner;
    private Lesson testLesson;
    private final String validPhoneNumber = "08012345680";
    private final int passingTestGrade = 3;

    @BeforeEach
    void setup() {
        useCase = new CancelLessonUseCase();
        learner = new Learner("John doe", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);
        Coach coach = new Coach("Peter");
        testLesson = new Lesson("Diving", passingTestGrade, coach, LocalDate.now(), "4-5pm");
    }

    @Test
    public void cancelLesson_registeredAndBookedLesson_Succeeds() {
        learner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));

        assertTrue(useCase.cancelLesson(testLesson, learner).isSuccess());
    }

    @Test
    public void cancelLesson_succeeds_lessonStatusUpdatedToCancelled() {
        learner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));

        useCase.cancelLesson(testLesson, learner);
        assertEquals(LessonStatus.CANCELLED, learner.getLessonStatus(testLesson));
    }

    @Test
    public void cancelLesson_registeredAndCancelledLesson_fails() {
        learner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.CANCELLED));

        assertFalse(useCase.cancelLesson(testLesson, learner).isSuccess());
    }

    @Test
    public void cancelLesson_registeredAndAttendedLesson_fails() {
        learner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.ATTENDED));

        assertFalse(useCase.cancelLesson(testLesson, learner).isSuccess());
    }

    @Test
    public void cancelLesson_notRegistered_fails() {
        assertFalse(useCase.cancelLesson(testLesson, learner).isSuccess());
    }

    @Test
    public void cancelLesson_notRegistered_failsWithCorrectError() {
        assertEquals(CancelLessonUseCase.Error.NO_BOOKING_FOR_LESSON, useCase.cancelLesson(testLesson, learner).getError());
    }

    @Test
    public void cancelLesson_registeredAndCancelledLesson_failsWithCorrectError() {
        learner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.CANCELLED));

        assertEquals(CancelLessonUseCase.Error.LESSON_ALREADY_CANCELLED, useCase.cancelLesson(testLesson, learner).getError());
    }

    @Test
    public void cancelLesson_registeredAndAttendedLesson_failsWithCorrectError() {
        learner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.ATTENDED));

        assertEquals(CancelLessonUseCase.Error.LESSON_ALREADY_ATTENDED, useCase.cancelLesson(testLesson, learner).getError());
    }


}