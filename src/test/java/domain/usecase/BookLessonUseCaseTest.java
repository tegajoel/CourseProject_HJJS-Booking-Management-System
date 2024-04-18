package domain.usecase;

import domain.entity.coach.Coach;
import domain.entity.learner.Learner;
import domain.entity.lesson.Lesson;
import domain.entity.lesson.LessonStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BookLessonUseCaseTest {
    private BookLessonUseCase useCase;
    private Learner learner;
    private Lesson testLesson;
    private final String validPhoneNumber = "08012345680";
    private final int passingTestGrade = 3;

    @BeforeEach
    void setUp() {
        useCase = new BookLessonUseCase();
        learner = new Learner("John doe", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);
        Coach coach = new Coach("Peter");
        testLesson = new Lesson("Diving", passingTestGrade, coach, LocalDate.now());
    }

    @Test
    public void bookLesson_sameGrade_succeeds() {
        assertTrue(useCase.bookLesson(testLesson, learner).isSuccess());
    }

    @Test
    public void bookLesson_succeeds_leanerAddedToLesson() {
        useCase.bookLesson(testLesson, learner);

        assertTrue(testLesson.getRegisteredLearners().contains(learner));
    }

    @Test
    public void bookLesson_succeeds_lessonAddedToLearner() {
        useCase.bookLesson(testLesson, learner);

        assertTrue(learner.hasLessonRegistered(testLesson));
    }

    @Test
    public void bookLesson_succeeds_hasInitialLessonStatusAsBooked() {
        useCase.bookLesson(testLesson, learner);

        assertEquals(LessonStatus.BOOKED, learner.getLessonStatus(testLesson));
    }


    @Test
    public void bookLesson_oneGradeAbove_succeeds() {
        testLesson.setGrade(4);
        learner.setGrade(3);

        assertTrue(useCase.bookLesson(testLesson, learner).isSuccess());
    }

    @Test
    public void bookLesson_twoGradeAbove_fails() {
        testLesson.setGrade(4);
        learner.setGrade(2);

        assertFalse(useCase.bookLesson(testLesson, learner).isSuccess());
    }

    @Test
    public void bookLesson_twoGradeBelow_fails() {
        testLesson.setGrade(1);
        learner.setGrade(3);

        assertFalse(useCase.bookLesson(testLesson, learner).isSuccess());
    }

    @Test
    public void bookLesson_twoGradeBelow_failsWithCorrectError() {
        testLesson.setGrade(1);
        learner.setGrade(3);

        var result = useCase.bookLesson(testLesson, learner);
        assertEquals(BookLessonUseCase.Error.LESSON_BELOW_LEARNER_GRADE, result.getError());
    }


    @Test
    public void bookLesson_twoGradeAbove_failsWithCorrectError() {
        testLesson.setGrade(4);
        learner.setGrade(2);

        var result = useCase.bookLesson(testLesson, learner);
        assertEquals(BookLessonUseCase.Error.LESSON_ABOVE_LEARNER_GRADE, result.getError());
    }

    @Test
    public void bookLesson_duplicateBooking_fails() {
        useCase.bookLesson(testLesson, learner);
        assertFalse(useCase.bookLesson(testLesson, learner).isSuccess());
    }

    @Test
    public void bookLesson_duplicateBooking_failsWithCorrectError() {
        useCase.bookLesson(testLesson, learner);

        var result = useCase.bookLesson(testLesson, learner);
        assertEquals(BookLessonUseCase.Error.DUPLICATE_BOOKING, result.getError());
    }

    @Test
    public void bookLesson_fullyBookedLesson_fails() {
        Learner learner1 = new Learner("John doe", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);
        Learner learner2 = new Learner("John doe2", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);
        Learner learner3 = new Learner("John doe3", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);
        Learner learner4 = new Learner("John doe4", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);
        Learner learner5 = new Learner("John doe5", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);

        useCase.bookLesson(testLesson, learner1);
        useCase.bookLesson(testLesson, learner2);
        useCase.bookLesson(testLesson, learner3);
        useCase.bookLesson(testLesson, learner4);

        assertFalse(useCase.bookLesson(testLesson, learner5).isSuccess());
    }

    @Test
    public void bookLesson_fullyBookedLesson_failsWithCorrectError() {
        Learner learner1 = new Learner("John doe", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);
        Learner learner2 = new Learner("John doe2", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);
        Learner learner3 = new Learner("John doe3", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);
        Learner learner4 = new Learner("John doe4", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);
        Learner learner5 = new Learner("John doe5", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);

        useCase.bookLesson(testLesson, learner1);
        useCase.bookLesson(testLesson, learner2);
        useCase.bookLesson(testLesson, learner3);
        useCase.bookLesson(testLesson, learner4);


        var result = useCase.bookLesson(testLesson, learner5);
        assertEquals(BookLessonUseCase.Error.LESSON_FULLY_BOOKED, result.getError());
    }

    @Test
    public void bookLesson_fullyBookedLessonWithOneCancelled_succeeds() {
        Learner learner1 = new Learner("John doe", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);
        Learner learner2 = new Learner("John doe2", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);
        Learner learner3 = new Learner("John doe3", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);
        Learner learner4 = new Learner("John doe4", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);
        Learner learner5 = new Learner("John doe5", "Male", 5, passingTestGrade, validPhoneNumber, validPhoneNumber);

        useCase.bookLesson(testLesson, learner1);
        useCase.bookLesson(testLesson, learner2);
        useCase.bookLesson(testLesson, learner3);
        useCase.bookLesson(testLesson, learner4);

        learner5.updateRegisteredLessonStatus(testLesson, LessonStatus.CANCELLED);

        assertFalse(useCase.bookLesson(testLesson, learner5).isSuccess());
    }
}