package domain.usecase;

import domain.entity.Coach;
import domain.entity.Learner;
import domain.entity.lesson.Lesson;
import domain.entity.lesson.LessonStatus;
import domain.entity.lesson.RegisteredLesson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CancelLessonUseCaseTest {
    private CancelLessonUseCase sut;
    private Learner learner;
    private Lesson testLesson;
    private final String validPhoneNumber = "08012345680";
    private final int passingTestGrade = 3;

    @BeforeEach
    void setup() {
        sut = new CancelLessonUseCase();
        learner = new Learner("John doe", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);
        Coach coach = new Coach("Peter");
        testLesson = new Lesson("Diving", passingTestGrade, coach, LocalDate.now());
    }

    @Test
    public void cancelLesson_registeredAndBookedLesson_Succeeds() {
        learner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));

        assertTrue(sut.execute(testLesson, learner).isSuccess());
    }

    @Test
    public void cancelLesson_succeeds_lessonStatusUpdatedToCancelled() {
        learner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.BOOKED));

        sut.execute(testLesson, learner);
        assertEquals(LessonStatus.CANCELLED, learner.getLessonStatus(testLesson));
    }

    @Test
    public void cancelLesson_registeredAndCancelledLesson_fails() {
        learner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.CANCELLED));

        assertFalse(sut.execute(testLesson, learner).isSuccess());
    }

    @Test
    public void cancelLesson_registeredAndAttendedLesson_fails() {
        learner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.ATTENDED));

        assertFalse(sut.execute(testLesson, learner).isSuccess());
    }

    @Test
    public void cancelLesson_notRegistered_fails() {
        assertFalse(sut.execute(testLesson, learner).isSuccess());
    }

    @Test
    public void cancelLesson_notRegistered_failsWithCorrectError() {
        assertEquals(CancelLessonUseCase.Error.NO_BOOKING_FOR_LESSON, sut.execute(testLesson, learner).getError());
    }

    @Test
    public void cancelLesson_registeredAndCancelledLesson_failsWithCorrectError() {
        learner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.CANCELLED));

        assertEquals(CancelLessonUseCase.Error.LESSON_ALREADY_CANCELLED, sut.execute(testLesson, learner).getError());
    }

    @Test
    public void cancelLesson_registeredAndAttendedLesson_failsWithCorrectError() {
        learner.registerNewLesson(new RegisteredLesson(testLesson, LessonStatus.ATTENDED));

        assertEquals(CancelLessonUseCase.Error.LESSON_ALREADY_ATTENDED, sut.execute(testLesson, learner).getError());
    }


}