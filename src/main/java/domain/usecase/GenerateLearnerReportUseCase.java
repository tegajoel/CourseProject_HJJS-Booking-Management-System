package domain.usecase;

import domain.entity.Learner;
import domain.entity.lesson.Lesson;
import domain.entity.lesson.LessonStatus;
import domain.entity.lesson.RegisteredLesson;
import domain.repository.LearnerRepository;

import java.util.ArrayList;
import java.util.List;

public class GenerateLearnerReportUseCase {
    private LearnerRepository learnerRepository;

    public GenerateLearnerReportUseCase(LearnerRepository learnerRepository) {
        this.learnerRepository = learnerRepository;
    }

    /**
     * Get the report for the provided learner
     *
     * @param learner learner
     * @return a {@link LearnerReport}
     */
    public LearnerReport getReportForLearner(Learner learner) {
        return new LearnerReport(
                learner.getName(),
                learner.getAge(),
                learner.getGender(),
                learner.getGrade(),
                learner.getRegisteredLessons().size(),
                learner.getRegisteredLessons().stream().filter(lesson -> lesson.getLessonStatus() == LessonStatus.BOOKED).map(RegisteredLesson::getLesson).toList(),
                learner.getRegisteredLessons().stream().filter(lesson -> lesson.getLessonStatus() == LessonStatus.ATTENDED).map(RegisteredLesson::getLesson).toList(),
                learner.getRegisteredLessons().stream().filter(lesson -> lesson.getLessonStatus() == LessonStatus.CANCELLED).map(RegisteredLesson::getLesson).toList()
        );
    }

    public List<LearnerReport> getReportForAllLearners(){
        var result = new ArrayList<LearnerReport>();
        learnerRepository.getAllLearners().forEach(learner -> result.add(getReportForLearner(learner)));

        return result;
    }

    public record LearnerReport(
            String learnerName,
            int learnerAge,
            String learnerGender,
            int currentGrade,
            int totalRegisteredLessons,
            List<Lesson> bookedLessons,
            List<Lesson> attendedLessons,
            List<Lesson> cancelledLessons
    ) {

    }
}
