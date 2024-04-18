package domain.usecase;

import domain.entity.Coach;
import domain.entity.Learner;
import domain.entity.lesson.Lesson;
import domain.entity.lesson.LessonStatus;
import domain.entity.lesson.RegisteredLesson;
import domain.repository.LearnerRepository;
import domain.util.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenerateLearnerReportUseCaseTest {

    private GenerateLearnerReportUseCase useCase;
    private LearnerRepository learnerRepository;
    private Learner learner;
    @BeforeEach
    void setUp() {
        learnerRepository = new LearnerRepositoryMock();
        useCase = new GenerateLearnerReportUseCase(learnerRepository);
        learner = new Learner("John Paul", "Female", 5, 5, "", "");
    }

    @Test
    void getReportForLearner_reportAndLearnerNameSame() {
        learner.setName("Sam Smith");
        assertEquals("Sam Smith", useCase.getReportForLearner(learner).learnerName());
    }

    @Test
    void getReportForLearner_reportAndLearnerAgeSame() {
        learner.setAge(4);
        assertEquals(4, useCase.getReportForLearner(learner).learnerAge());
    }

    @Test
    void getReportForLearner_reportAndLearnerGradeSame() {
        learner.setGrade(3);
        assertEquals(3, useCase.getReportForLearner(learner).currentGrade());
    }

    @Test
    void getReportForLearner_reportAndLearnerGenderSame() {
        learner.setGender("Female");
        assertEquals("Female", useCase.getReportForLearner(learner).learnerGender());
    }

    @Test
    void getReportForLearner_reportAndLearnerRegisteredLessonSame() {
        Coach coach = new Coach("John Smith");
        Lesson lesson1 = new Lesson("Diving1", 3, coach, LocalDate.now());
        Lesson lesson3 = new Lesson("Diving3", 2, coach, LocalDate.now());
        Lesson lesson2 = new Lesson("Diving2", 3, coach, LocalDate.now());
        Lesson lesson4 = new Lesson("Diving4", 1, coach, LocalDate.now());

        learner.registerNewLesson(new RegisteredLesson(lesson1, LessonStatus.BOOKED));
        learner.registerNewLesson(new RegisteredLesson(lesson2, LessonStatus.BOOKED));
        learner.registerNewLesson(new RegisteredLesson(lesson3, LessonStatus.ATTENDED));
        learner.registerNewLesson(new RegisteredLesson(lesson4, LessonStatus.CANCELLED));

        assertEquals(4, useCase.getReportForLearner(learner).totalRegisteredLessons());
    }

    @Test
    void getReportForLearner_reportAndLearnerBookedLessonSame() {
        Coach coach = new Coach("John Smith");
        Lesson lesson1 = new Lesson("Diving1", 3, coach, LocalDate.now());
        Lesson lesson3 = new Lesson("Diving3", 2, coach, LocalDate.now());
        Lesson lesson2 = new Lesson("Diving2", 3, coach, LocalDate.now());
        Lesson lesson4 = new Lesson("Diving4", 1, coach, LocalDate.now());

        learner.registerNewLesson(new RegisteredLesson(lesson1, LessonStatus.BOOKED));
        learner.registerNewLesson(new RegisteredLesson(lesson2, LessonStatus.BOOKED));
        learner.registerNewLesson(new RegisteredLesson(lesson3, LessonStatus.ATTENDED));
        learner.registerNewLesson(new RegisteredLesson(lesson4, LessonStatus.CANCELLED));

        assertEquals(2, useCase.getReportForLearner(learner).bookedLessons().size());
    }

    @Test
    void getReportForLearner_reportAndLearnerAttendedLessonSame() {
        Coach coach = new Coach("John Smith");
        Lesson lesson1 = new Lesson("Diving1", 3, coach, LocalDate.now());
        Lesson lesson2 = new Lesson("Diving2", 3, coach, LocalDate.now());
        Lesson lesson3 = new Lesson("Diving3", 2, coach, LocalDate.now());
        Lesson lesson4 = new Lesson("Diving4", 1, coach, LocalDate.now());

        learner.registerNewLesson(new RegisteredLesson(lesson1, LessonStatus.BOOKED));
        learner.registerNewLesson(new RegisteredLesson(lesson2, LessonStatus.BOOKED));
        learner.registerNewLesson(new RegisteredLesson(lesson4, LessonStatus.CANCELLED));
        learner.registerNewLesson(new RegisteredLesson(lesson3, LessonStatus.ATTENDED));

        assertEquals(1, useCase.getReportForLearner(learner).attendedLessons().size());
    }

    @Test
    void getReportForLearner_reportAndLearnerCancelledLessonSame() {
        Coach coach = new Coach("John Smith");
        Lesson lesson1 = new Lesson("Diving1", 3, coach, LocalDate.now());
        Lesson lesson3 = new Lesson("Diving3", 2, coach, LocalDate.now());
        Lesson lesson2 = new Lesson("Diving2", 3, coach, LocalDate.now());
        Lesson lesson4 = new Lesson("Diving4", 1, coach, LocalDate.now());

        learner.registerNewLesson(new RegisteredLesson(lesson1, LessonStatus.BOOKED));
        learner.registerNewLesson(new RegisteredLesson(lesson2, LessonStatus.BOOKED));
        learner.registerNewLesson(new RegisteredLesson(lesson3, LessonStatus.ATTENDED));
        learner.registerNewLesson(new RegisteredLesson(lesson4, LessonStatus.CANCELLED));

        assertEquals(1, useCase.getReportForLearner(learner).cancelledLessons().size());
    }

    @Test
    void getReportForLearner_NoRegisteredLesson_reportRegisteredLessonIsZero() {
        assertEquals(0, useCase.getReportForLearner(learner).totalRegisteredLessons());
    }

    @Test
    void getReportForAllLearners_returnsReportListWithCorrectSize(){
        Coach coach = new Coach("John Smith");
        Lesson lesson1 = new Lesson("Diving1", 3, coach, LocalDate.now());
        Lesson lesson3 = new Lesson("Diving3", 2, coach, LocalDate.now());
        Lesson lesson2 = new Lesson("Diving2", 3, coach, LocalDate.now());
        Lesson lesson4 = new Lesson("Diving4", 1, coach, LocalDate.now());


        Learner learner1 = new Learner("John Paul", "Female", 5, 5, "", "");
        Learner learner2 = new Learner("Peter Paul", "Male", 5, 5, "", "");
        Learner learner3 = new Learner("Samson Paul", "Female", 5, 5, "", "");

        learner1.registerNewLesson(new RegisteredLesson(lesson1, LessonStatus.BOOKED));
        learner2.registerNewLesson(new RegisteredLesson(lesson2, LessonStatus.BOOKED));
        learner3.registerNewLesson(new RegisteredLesson(lesson3, LessonStatus.ATTENDED));
        learner1.registerNewLesson(new RegisteredLesson(lesson4, LessonStatus.CANCELLED));

        learnerRepository.addNewLearner(learner1);
        learnerRepository.addNewLearner(learner2);
        learnerRepository.addNewLearner(learner3);

        assertEquals(3, useCase.getReportForAllLearners().size());
    }


    private static class LearnerRepositoryMock implements LearnerRepository {
        private final List<Learner> learners = new ArrayList<>();
        @Override
        public boolean addNewLearner(Learner learner) {
            return learners.add(learner);
        }

        @Override
        public List<Learner> getAllLearners() {
            return learners;
        }

        @Override
        public Result<Learner, Error> getLearnerById(int id) {
            return null;
        }
    }



}