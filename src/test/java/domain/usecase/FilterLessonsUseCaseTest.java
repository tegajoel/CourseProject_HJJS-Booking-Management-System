package domain.usecase;

import domain.entity.coach.Coach;
import domain.entity.lesson.Lesson;
import domain.mock.LessonRepositoryMock;
import domain.repository.LessonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilterLessonsUseCaseTest {
    private FilterLessonsUseCase useCase;
    private LessonRepository lessonRepository;
    private Coach testCoach;
    private final String lessonTime = "4-5pm";

    @BeforeEach
    void setUp() {
        lessonRepository = new LessonRepositoryMock();
        useCase = new FilterLessonsUseCase(lessonRepository);
        testCoach = new Coach("Helen");
    }


    @Test
    public void filterByGrade_validGrade_returnsSuccess() {
        assertTrue(useCase.filterByGrade(0).isSuccess());
        assertTrue(useCase.filterByGrade(3).isSuccess());
    }

    @Test
    public void filterByGrade_invalidGrade_returnsError() {
        assertFalse(useCase.filterByGrade(-3).isSuccess());
    }

    @Test
    public void filterByGrade_invalidGrade_returnsCorrectError() {
        assertEquals(FilterLessonsUseCase.Error.INVALID_INPUT, useCase.filterByGrade(-3).getError());
    }

    @Test
    public void filterByGrade_succeeds_returnsListWithCorrectSize() {
        Lesson lesson1 = new Lesson("Diving1", 3, testCoach, LocalDate.now(), lessonTime);
        Lesson lesson3 = new Lesson("Diving3", 2, testCoach, LocalDate.now(), lessonTime);
        Lesson lesson2 = new Lesson("Diving2", 3, testCoach, LocalDate.now(), lessonTime);
        Lesson lesson4 = new Lesson("Diving4", 1, testCoach, LocalDate.now(), lessonTime);

        lessonRepository.addNewLesson(lesson1);
        lessonRepository.addNewLesson(lesson2);
        lessonRepository.addNewLesson(lesson3);
        lessonRepository.addNewLesson(lesson4);

        var result = useCase.filterByGrade(3).getData();

        assertEquals(2, result.size());

    }

    @Test
    public void filterByGrade_succeeds_returnsListWithSameGrade() {
        Lesson lesson1 = new Lesson("Diving1", 3, testCoach, LocalDate.now(), lessonTime);
        Lesson lesson3 = new Lesson("Diving3", 2, testCoach, LocalDate.now(), lessonTime);
        Lesson lesson2 = new Lesson("Diving2", 3, testCoach, LocalDate.now(), lessonTime);
        Lesson lesson4 = new Lesson("Diving4", 1, testCoach, LocalDate.now(), lessonTime);

        lessonRepository.addNewLesson(lesson1);
        lessonRepository.addNewLesson(lesson2);
        lessonRepository.addNewLesson(lesson3);
        lessonRepository.addNewLesson(lesson4);

        var result = useCase.filterByGrade(3).getData();

        var allSameGrade = true;

        for (Lesson lesson : result) {
            if (lesson.getGrade() != 3) {
                allSameGrade = false;
                break;
            }
        }

        assertFalse(result.isEmpty());
        assertTrue(allSameGrade);
    }

    @Test
    public void filterByGrade_succeedsWithNoMatch_emptyListReturned() {
        Lesson lesson1 = new Lesson("Diving1", 3, testCoach, LocalDate.now(), lessonTime);
        Lesson lesson3 = new Lesson("Diving3", 2, testCoach, LocalDate.now(), lessonTime);
        Lesson lesson2 = new Lesson("Diving2", 3, testCoach, LocalDate.now(), lessonTime);
        Lesson lesson4 = new Lesson("Diving4", 1, testCoach, LocalDate.now(), lessonTime);

        lessonRepository.addNewLesson(lesson1);
        lessonRepository.addNewLesson(lesson2);
        lessonRepository.addNewLesson(lesson3);
        lessonRepository.addNewLesson(lesson4);

        var result = useCase.filterByGrade(4).getData();

        assertTrue(result.isEmpty());
    }

    @Test
    public void filterByDay_validDayWednesday_returnsSuccess() {
        assertTrue(useCase.filterByDay("Wednesday").isSuccess());
    }

    @Test
    public void filterByDay_validDayWithWhiteSpace_returnsSuccess() {
        assertTrue(useCase.filterByDay(" Wednesday  ").isSuccess());
    }

    @Test
    public void filterByDay_validDayFriday_returnsSuccess() {
        assertTrue(useCase.filterByDay("Friday").isSuccess());
    }

    @Test
    public void filterByDay_validDayMixedCase_returnsSuccess() {
        assertTrue(useCase.filterByDay("FriDay").isSuccess());
    }

    @Test
    public void filterByDay_validDayLowercaseCase_returnsSuccess() {
        assertTrue(useCase.filterByDay("saturday").isSuccess());
    }

    @Test
    public void filterByDay_validDayUppercaseCase_returnsSuccess() {
        assertTrue(useCase.filterByDay("MONDAY").isSuccess());
    }

    @Test
    public void filterByDay_validDayNotInTimetable_returnsSuccess() {
        assertTrue(useCase.filterByDay("Sunday").isSuccess());
    }

    @Test
    public void filterByDay_emptyString_returnsError() {
        assertFalse(useCase.filterByDay("").isSuccess());
    }

    @Test
    public void filterByDay_emptyString_returnsCorrectError() {
        assertEquals(FilterLessonsUseCase.Error.INVALID_INPUT, useCase.filterByDay("").getError());
    }

    @Test
    public void filterByDay_randomString_returnsError() {
        assertFalse(useCase.filterByDay("skdsdn").isSuccess());
    }

    @Test
    public void filterByDay_randomString_returnsCorrectError() {
        assertEquals(FilterLessonsUseCase.Error.INVALID_INPUT, useCase.filterByDay("skdsdn").getError());
    }

    @Test
    public void filterByDay_invalidGrade_returnsCorrectError() {
        assertEquals(FilterLessonsUseCase.Error.INVALID_INPUT, useCase.filterByGrade(-3).getError());
    }

    @Test
    public void filterByCoach_emptyString_returnsError() {
        assertFalse(useCase.filterByDay("").isSuccess());
    }

    @Test
    public void filterByCoach_emptyString_returnsCorrectError() {
        assertEquals(FilterLessonsUseCase.Error.INVALID_INPUT, useCase.filterByDay("").getError());
    }

    @Test
    public void filterByCoach_succeeds_returnsListWithCorrectSize() {
        Coach coach1 = new Coach("Helen Paul");
        Coach coach2 = new Coach("Sam Johnson");
        Lesson lesson1 = new Lesson("Diving1", 3, coach1, LocalDate.now(), lessonTime);
        Lesson lesson3 = new Lesson("Diving3", 2, coach2, LocalDate.now(), lessonTime);
        Lesson lesson2 = new Lesson("Diving2", 3, coach1, LocalDate.now(), lessonTime);
        Lesson lesson4 = new Lesson("Diving4", 1, coach1, LocalDate.now(), lessonTime);
        Lesson lesson5 = new Lesson("Diving4", 1, coach2, LocalDate.now(), lessonTime);

        lessonRepository.addNewLesson(lesson1);
        lessonRepository.addNewLesson(lesson2);
        lessonRepository.addNewLesson(lesson3);
        lessonRepository.addNewLesson(lesson4);
        lessonRepository.addNewLesson(lesson5);

        var result = useCase.filterByCoach(coach2.getName()).getData();

        assertEquals(2, result.size());
    }

    @Test
    public void filterByCoach_succeeds_returnsListWithSameCoach() {
        Coach coach1 = new Coach("Helen Paul");
        Coach coach2 = new Coach("Sam Johnson");
        Lesson lesson1 = new Lesson("Diving1", 3, coach1, LocalDate.now(), lessonTime);
        Lesson lesson3 = new Lesson("Diving3", 2, coach2, LocalDate.now(), lessonTime);
        Lesson lesson2 = new Lesson("Diving2", 3, coach1, LocalDate.now(), lessonTime);
        Lesson lesson4 = new Lesson("Diving4", 1, coach1, LocalDate.now(), lessonTime);

        lessonRepository.addNewLesson(lesson1);
        lessonRepository.addNewLesson(lesson2);
        lessonRepository.addNewLesson(lesson3);
        lessonRepository.addNewLesson(lesson4);

        var result = useCase.filterByCoach(coach1.getName()).getData();

        var allSameCoach = true;

        for (Lesson lesson : result) {
            if (lesson.getCoach() != coach1) {
                allSameCoach = false;
                break;
            }
        }

        assertFalse(result.isEmpty());
        assertTrue(allSameCoach);
    }

    @Test
    public void filterByCoach_succeedsWithNoMatch_emptyListReturned() {
        Coach coach1 = new Coach("Helen Paul");
        Coach coach2 = new Coach("Sam Johnson");
        Lesson lesson1 = new Lesson("Diving1", 3, coach1, LocalDate.now(), lessonTime);
        Lesson lesson3 = new Lesson("Diving3", 2, coach2, LocalDate.now(), lessonTime);
        Lesson lesson2 = new Lesson("Diving2", 3, coach1, LocalDate.now(), lessonTime);
        Lesson lesson4 = new Lesson("Diving4", 1, coach1, LocalDate.now(), lessonTime);

        lessonRepository.addNewLesson(lesson1);
        lessonRepository.addNewLesson(lesson2);
        lessonRepository.addNewLesson(lesson3);
        lessonRepository.addNewLesson(lesson4);

        var result = useCase.filterByCoach("Abraham").getData();

        assertTrue(result.isEmpty());
    }

    @Test
    public void filterByCoach_validCoachWithNameCaseSwapped_returnsListWithCorrectSize() {
        Coach coach1 = new Coach("Helen Paul");
        Coach coach2 = new Coach("Sam Johnson");
        Lesson lesson1 = new Lesson("Diving1", 3, coach1, LocalDate.now(), lessonTime);
        Lesson lesson3 = new Lesson("Diving3", 2, coach2, LocalDate.now(), lessonTime);
        Lesson lesson2 = new Lesson("Diving2", 3, coach1, LocalDate.now(), lessonTime);
        Lesson lesson4 = new Lesson("Diving4", 1, coach1, LocalDate.now(), lessonTime);
        Lesson lesson5 = new Lesson("Diving4", 1, coach2, LocalDate.now(), lessonTime);

        lessonRepository.addNewLesson(lesson1);
        lessonRepository.addNewLesson(lesson2);
        lessonRepository.addNewLesson(lesson3);
        lessonRepository.addNewLesson(lesson4);
        lessonRepository.addNewLesson(lesson5);

        var result = useCase.filterByCoach("helEn PauL").getData();

        assertEquals(3, result.size());
    }

    @Test
    public void filterByCoach_validCoachWithWhiteSpace_returnsListWithCorrectSize() {
        Coach coach1 = new Coach("Helen Paul");
        Coach coach2 = new Coach("Sam Johnson");
        Lesson lesson1 = new Lesson("Diving1", 3, coach1, LocalDate.now(), lessonTime);
        Lesson lesson3 = new Lesson("Diving3", 2, coach2, LocalDate.now(), lessonTime);
        Lesson lesson2 = new Lesson("Diving2", 3, coach1, LocalDate.now(), lessonTime);
        Lesson lesson4 = new Lesson("Diving4", 1, coach1, LocalDate.now(), lessonTime);
        Lesson lesson5 = new Lesson("Diving4", 1, coach2, LocalDate.now(), lessonTime);

        lessonRepository.addNewLesson(lesson1);
        lessonRepository.addNewLesson(lesson2);
        lessonRepository.addNewLesson(lesson3);
        lessonRepository.addNewLesson(lesson4);
        lessonRepository.addNewLesson(lesson5);

        var result = useCase.filterByCoach("  helEn PauL  ").getData();

        assertEquals(3, result.size());
    }
}