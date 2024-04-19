package domain.usecase;

import domain.entity.coach.Coach;
import domain.entity.lesson.Lesson;
import domain.mock.CoachRepositoryMock;
import domain.repository.CoachRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenerateCoachReportUseCaseTest {
    private GenerateCoachReportUseCase useCase;
    private CoachRepository coachRepository;
    private Coach testCoach;
    private final String lessonTime = "4-5pm";

    @BeforeEach
    void setUp() {
        coachRepository = new CoachRepositoryMock();
        useCase = new GenerateCoachReportUseCase(coachRepository);
        testCoach = new Coach("Mary Anderson");
    }

    @Test
    void getReportForCoach_reportAndLearnerNameSame() {
        testCoach.setName("Mary Anderson");
        assertEquals("Mary Anderson", useCase.getReportForCoach(testCoach).coachName());
    }


    @Test
    void getReportForCoach_reportContainsAccurateNumberLessonsTaught() {
        Lesson lesson1 = new Lesson("Diving1", 3, testCoach, LocalDate.now(), lessonTime);
        Lesson lesson3 = new Lesson("Diving3", 2, testCoach, LocalDate.now(), lessonTime);
        Lesson lesson2 = new Lesson("Diving2", 3, testCoach, LocalDate.now(), lessonTime);
        Lesson lesson4 = new Lesson("Diving4", 1, testCoach, LocalDate.now(), lessonTime);

        assertEquals(4, useCase.getReportForCoach(testCoach).numberOfLessonsTaught());
        assertEquals(4, useCase.getReportForCoach(testCoach).lessonsTaught().size());
    }

    @Test
    void getReportForCoach_reportContainsAccurateLessonsTaught() {
        Lesson lesson1 = new Lesson("Diving1", 3, testCoach, LocalDate.now(), lessonTime);
        Lesson lesson3 = new Lesson("Diving3", 2, testCoach, LocalDate.now(), lessonTime);
        Lesson lesson2 = new Lesson("Diving2", 3, testCoach, LocalDate.now(), lessonTime);
        Lesson lesson4 = new Lesson("Diving4", 1, testCoach, LocalDate.now(), lessonTime);

        var isCoachForAllLessons = true;
        var result = useCase.getReportForCoach(testCoach).lessonsTaught();

        for (Lesson lesson : result) {
            if (lesson.getCoach() != testCoach) {
                isCoachForAllLessons = false;
                break;
            }
        }

        assertTrue(isCoachForAllLessons);
    }

    @Test
    void getReportForAllCoaches_returnsReportListWithCorrectSize() {
        Coach coach = new Coach("John Smith");
        Coach coach2 = new Coach("Abraham Johnson");
        Coach coach3 = new Coach("Peter Parker");

        coachRepository.addNewCoach(coach);
        coachRepository.addNewCoach(coach2);
        coachRepository.addNewCoach(coach3);

        assertEquals(3, useCase.getReportForAllCoaches().size());
    }

}
