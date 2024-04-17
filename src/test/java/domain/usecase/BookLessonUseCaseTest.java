package domain.usecase;

import domain.entity.Coach;
import domain.entity.Learner;
import domain.entity.lesson.Lesson;
import domain.entity.lesson.LessonStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BookLessonUseCaseTest {
    private BookLessonUseCase sut;
    private Learner learner;
    private Lesson testLesson;
    private final String validPhoneNumber = "08012345680";
    private final int passingTestGrade = 3;

    @BeforeEach
    void setUp() {
        sut = new BookLessonUseCase();
        learner = new Learner("John doe", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);
        Coach coach = new Coach("Peter");
        testLesson = new Lesson("Diving", passingTestGrade, coach, LocalDate.now());
    }

    @Test
    public void bookLesson_sameGrade_succeeds() {
        assertTrue(sut.execute(testLesson, learner).isSuccess());
    }

    @Test
    public void bookLesson_succeeds_leanerAddedToLesson() {
        sut.execute(testLesson, learner);

        assertTrue(testLesson.getRegisteredLearners().contains(learner));
    }

    @Test
    public void bookLesson_succeeds_lessonAddedToLearner() {
        sut.execute(testLesson, learner);

        assertTrue(learner.hasLessonRegistered(testLesson));
    }

    @Test
    public void bookLesson_succeeds_hasInitialLessonStatusAsBooked() {
        sut.execute(testLesson, learner);

        assertEquals(LessonStatus.BOOKED, learner.getLessonStatus(testLesson));
    }


    @Test
    public void bookLesson_oneGradeAbove_succeeds() {
        testLesson.setGrade(4);
        learner.setGrade(3);

        assertTrue(sut.execute(testLesson, learner).isSuccess());
    }

    @Test
    public void bookLesson_twoGradeAbove_fails() {
        testLesson.setGrade(4);
        learner.setGrade(2);

        assertFalse(sut.execute(testLesson, learner).isSuccess());
    }

    @Test
    public void bookLesson_twoGradeBelow_fails() {
        testLesson.setGrade(1);
        learner.setGrade(3);

        assertFalse(sut.execute(testLesson, learner).isSuccess());
    }

    @Test
    public void bookLesson_twoGradeBelow_failsWithCorrectError() {
        testLesson.setGrade(1);
        learner.setGrade(3);

        var result = sut.execute(testLesson, learner);
        assertEquals(BookLessonUseCase.Error.LESSON_BELOW_LEARNER_GRADE, result.getError());
    }


    @Test
    public void bookLesson_twoGradeAbove_failsWithCorrectError() {
        testLesson.setGrade(4);
        learner.setGrade(2);

        var result = sut.execute(testLesson, learner);
        assertEquals(BookLessonUseCase.Error.LESSON_ABOVE_LEARNER_GRADE, result.getError());
    }

    @Test
    public void bookLesson_duplicateBooking_fails() {
        sut.execute(testLesson, learner);
        assertFalse(sut.execute(testLesson, learner).isSuccess());
    }

    @Test
    public void bookLesson_duplicateBooking_failsWithCorrectError() {
        sut.execute(testLesson, learner);

        var result = sut.execute(testLesson, learner);
        assertEquals(BookLessonUseCase.Error.DUPLICATE_BOOKING, result.getError());
    }

    @Test
    public void bookLesson_fullyBookedLesson_fails() {
        Learner learner1 = new Learner("John doe", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);
        Learner learner2 = new Learner("John doe2", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);
        Learner learner3 = new Learner("John doe3", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);
        Learner learner4 = new Learner("John doe4", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);
        Learner learner5 = new Learner("John doe5", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);

        sut.execute(testLesson, learner1);
        sut.execute(testLesson, learner2);
        sut.execute(testLesson, learner3);
        sut.execute(testLesson, learner4);

        assertFalse(sut.execute(testLesson, learner5).isSuccess());
    }

    @Test
    public void bookLesson_fullyBookedLesson_failsWithCorrectError() {
        Learner learner1 = new Learner("John doe", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);
        Learner learner2 = new Learner("John doe2", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);
        Learner learner3 = new Learner("John doe3", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);
        Learner learner4 = new Learner("John doe4", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);
        Learner learner5 = new Learner("John doe5", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);

        sut.execute(testLesson, learner1);
        sut.execute(testLesson, learner2);
        sut.execute(testLesson, learner3);
        sut.execute(testLesson, learner4);


        var result = sut.execute(testLesson, learner5);
        assertEquals(BookLessonUseCase.Error.LESSON_FULLY_BOOKED, result.getError());
    }

    @Test
    public void bookLesson_fullyBookedLessonWithOneCancelled_succeeds() {
        Learner learner1 = new Learner("John doe", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);
        Learner learner2 = new Learner("John doe2", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);
        Learner learner3 = new Learner("John doe3", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);
        Learner learner4 = new Learner("John doe4", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);
        Learner learner5 = new Learner("John doe5", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);

        sut.execute(testLesson, learner1);
        sut.execute(testLesson, learner2);
        sut.execute(testLesson, learner3);
        sut.execute(testLesson, learner4);

        learner5.updateRegisteredLessonStatus(testLesson, LessonStatus.CANCELLED);

        assertFalse(sut.execute(testLesson, learner5).isSuccess());
    }
}