package domain.usecase;

import domain.entity.coach.Coach;
import domain.entity.lesson.Lesson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BuildTimetableUseCaseTest {

    private BuildTimetableUseCase useCase;
    private List<Coach> coaches;

    @BeforeEach
    void setUp() {
        useCase = new BuildTimetableUseCase();
        coaches = new ArrayList<>();
        coaches.add(new Coach("Peter"));
        coaches.add(new Coach("Sandra"));
        coaches.add(new Coach("Philip"));
        coaches.add(new Coach("Armin"));
    }

    @Test
    void buildAndGetLessons_atLeast44LessonsReturned() {
        var result = useCase.buildAndGetLessons(coaches);
        assertTrue(result.size() >= 44);
    }

    // leach lessons for each grade
    @Test
    void buildAndGetLessons_eachGradeHasLesson_returnsTrue() {
        var result = useCase.buildAndGetLessons(coaches);
        boolean hasGrade1 = false;
        boolean hasGrade2 = false;
        boolean hasGrade3 = false;
        boolean hasGrade4 = false;
        boolean hasGrade5 = false;

        for (Lesson lesson : result) {
            switch (lesson.getGrade()) {
                case 1 -> hasGrade1 = true;
                case 2 -> hasGrade2 = true;
                case 3 -> hasGrade3 = true;
                case 4 -> hasGrade4 = true;
                case 5 -> hasGrade5 = true;
            }
        }
        assertTrue(hasGrade1 && hasGrade2 && hasGrade3 && hasGrade4 && hasGrade5);
    }


    @Test
    void buildAndGetLessons_eachCoachAssignedLesson_returnsTrue() {
        useCase.buildAndGetLessons(coaches);

        boolean allHaveLesson = true;

        for (Coach coach : coaches) {
            if (coach.getAssignedLessons().isEmpty()) {
                allHaveLesson = false;
                break;
            }
        }

        assertTrue(allHaveLesson);
    }

    // when coach is assigned to a lesson, the coach is correctly assigned to lesson

    @Test
    void buildAndGetLessons_eachLessonHasId_noExceptionThrownToGetEachId() {
        var result = useCase.buildAndGetLessons(coaches);

        var error = assertThrows(IllegalStateException.class, () -> {
            result.forEach(Lesson::getId);
        });

        assertNull(error);
    }

    @Test
    void buildAndGetLessons_noLessonForGrazeZeroOrBelow_returnsTrue() {
        var result = useCase.buildAndGetLessons(coaches);
        boolean hasGradeZeroOrBelow = false;

        for (Lesson lesson : result) {
            if (lesson.getGrade() <= 0) {
                hasGradeZeroOrBelow = true;
                break;
            }
        }
        assertFalse(hasGradeZeroOrBelow);
    }

    @Test
    void buildAndGetLessons_noLessonForGrazeSoxOrAbove_returnsTrue() {
        var result = useCase.buildAndGetLessons(coaches);
        boolean hasGradeSixOrAbove = false;

        for (Lesson lesson : result) {
            if (lesson.getGrade() >= 6) {
                hasGradeSixOrAbove = true;
                break;
            }
        }
        assertFalse(hasGradeSixOrAbove);
    }

    @Test
    void buildAndGetLessons_LessonsOnlyOnMonWedFriAndSat_returnsTrue() {
        var result = useCase.buildAndGetLessons(coaches);
        boolean hasOnyOnValidDays = true;

        for (Lesson lesson : result) {
            var day = lesson.getLessonDate().getDayOfWeek();

            if (day == DayOfWeek.TUESDAY
                    || day == DayOfWeek.THURSDAY
                    || day == DayOfWeek.SUNDAY
            ) {
                hasOnyOnValidDays = false;
                break;
            }
        }
        assertTrue(hasOnyOnValidDays);
    }
}