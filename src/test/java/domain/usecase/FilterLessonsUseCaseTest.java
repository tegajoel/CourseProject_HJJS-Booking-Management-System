package domain.usecase;

import domain.entity.Coach;
import domain.entity.lesson.Lesson;
import domain.repository.LessonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilterLessonsUseCaseTest {
    private FilterLessonsUseCase sut;
    private LessonRepository lessonRepository;
    private Coach testCoach;

    @BeforeEach
    void setUp() {
        lessonRepository = new LessonRepositoryMock();
        sut = new FilterLessonsUseCase(lessonRepository);
        testCoach = new Coach("Helen");
    }


    @Test
    public void filterByGrade_validGrade_returnsSuccess() {
        assertTrue(sut.filterByGrade(0).isSuccess());
        assertTrue(sut.filterByGrade(3).isSuccess());
    }

    @Test
    public void filterByGrade_invalidGrade_returnsError() {
        assertFalse(sut.filterByGrade(-3).isSuccess());
    }

    @Test
    public void filterByGrade_invalidGrade_returnsCorrectError() {
        assertEquals(FilterLessonsUseCase.Error.INVALID_INPUT, sut.filterByGrade(-3).getError());
    }

    @Test
    public void filterByGrade_succeeds_returnsListWithCorrectSize() {
        Lesson lesson1 = new Lesson("Diving1", 3, testCoach, LocalDate.now());
        Lesson lesson3 = new Lesson("Diving3", 2, testCoach, LocalDate.now());
        Lesson lesson2 = new Lesson("Diving2", 3, testCoach, LocalDate.now());
        Lesson lesson4 = new Lesson("Diving4", 1, testCoach, LocalDate.now());

        lessonRepository.addNewLesson(lesson1);
        lessonRepository.addNewLesson(lesson2);
        lessonRepository.addNewLesson(lesson3);
        lessonRepository.addNewLesson(lesson4);

        var result = sut.filterByGrade(3).getData();

        assertEquals(2, result.size());

    }

    @Test
    public void filterByGrade_succeeds_returnsListWithSameGrade() {
        Lesson lesson1 = new Lesson("Diving1", 3, testCoach, LocalDate.now());
        Lesson lesson3 = new Lesson("Diving3", 2, testCoach, LocalDate.now());
        Lesson lesson2 = new Lesson("Diving2", 3, testCoach, LocalDate.now());
        Lesson lesson4 = new Lesson("Diving4", 1, testCoach, LocalDate.now());

        lessonRepository.addNewLesson(lesson1);
        lessonRepository.addNewLesson(lesson2);
        lessonRepository.addNewLesson(lesson3);
        lessonRepository.addNewLesson(lesson4);

        var result = sut.filterByGrade(3).getData();

        var allSameGrade = true;

        for (Lesson lesson : result) {
            if (lesson.getGrade() != 3) {
                allSameGrade = false;
                break;
            }
        }

        assertTrue(allSameGrade);
    }

    @Test
    public void filterByGrade_succeedsWithNoMatch_emptyListReturned() {
        Lesson lesson1 = new Lesson("Diving1", 3, testCoach, LocalDate.now());
        Lesson lesson3 = new Lesson("Diving3", 2, testCoach, LocalDate.now());
        Lesson lesson2 = new Lesson("Diving2", 3, testCoach, LocalDate.now());
        Lesson lesson4 = new Lesson("Diving4", 1, testCoach, LocalDate.now());

        lessonRepository.addNewLesson(lesson1);
        lessonRepository.addNewLesson(lesson2);
        lessonRepository.addNewLesson(lesson3);
        lessonRepository.addNewLesson(lesson4);

        var result = sut.filterByGrade(4).getData();

        assertTrue(result.isEmpty());
    }

    @Test
    public void filterByDay_validDayWednesday_returnsSuccess() {
        assertTrue(sut.filterByDay("Wednesday").isSuccess());
    }

    @Test
    public void filterByDay_validDayWithWhiteSpace_returnsSuccess() {
        assertTrue(sut.filterByDay(" Wednesday  ").isSuccess());
    }

    @Test
    public void filterByDay_validDayFriday_returnsSuccess() {
        assertTrue(sut.filterByDay("Friday").isSuccess());
    }

    @Test
    public void filterByDay_validDayMixedCase_returnsSuccess() {
        assertTrue(sut.filterByDay("FriDay").isSuccess());
    }

    @Test
    public void filterByDay_validDayLowercaseCase_returnsSuccess() {
        assertTrue(sut.filterByDay("saturday").isSuccess());
    }

    @Test
    public void filterByDay_validDayUppercaseCase_returnsSuccess() {
        assertTrue(sut.filterByDay("MONDAY").isSuccess());
    }

    @Test
    public void filterByDay_validDayNotInTimetable_returnsSuccess() {
        assertTrue(sut.filterByDay("Sunday").isSuccess());
    }

    @Test
    public void filterByDay_emptyString_returnsError() {
        assertFalse(sut.filterByDay("").isSuccess());
    }

    @Test
    public void filterByDay_emptyString_returnsCorrectError() {
        assertEquals(FilterLessonsUseCase.Error.INVALID_INPUT, sut.filterByDay("").getError());
    }

    @Test
    public void filterByDay_randomString_returnsError() {
        assertFalse(sut.filterByDay("skdsdn").isSuccess());
    }

    @Test
    public void filterByDay_randomString_returnsCorrectError() {
        assertEquals(FilterLessonsUseCase.Error.INVALID_INPUT, sut.filterByDay("skdsdn").getError());
    }

    @Test
    public void filterByDay_invalidGrade_returnsCorrectError() {
        assertEquals(FilterLessonsUseCase.Error.INVALID_INPUT, sut.filterByGrade(-3).getError());
    }

    @Test
    public void filterByCoach_emptyString_returnsError() {
        assertFalse(sut.filterByDay("").isSuccess());
    }


    private static class LessonRepositoryMock implements LessonRepository {
        private final List<Lesson> lessons = new ArrayList<>();

        @Override
        public List<Lesson> getAllLessons() {
            return lessons;
        }

        @Override
        public void addNewLesson(Lesson lesson) {
            lessons.add(lesson);
        }
    }

}