package domain.usecase;

import domain.entity.coach.Coach;
import domain.entity.Rating;
import domain.entity.coach.CoachReport;
import domain.entity.lesson.Lesson;
import domain.repository.CoachRepository;
import domain.util.LessonUtil;

import java.util.List;
import java.util.Map;

public class GenerateCoachReportUseCase {
    private final CoachRepository coachRepository;

    public GenerateCoachReportUseCase(CoachRepository coachRepository) {
        this.coachRepository = coachRepository;
    }

    /**
     * Get the report for the provided learner
     *
     * @param coach learner
     * @return a {@link CoachReport}
     */
    public CoachReport getReportForCoach(Coach coach) {
        return new CoachReport(
                coach.getName(),
                coach.getAssignedLessons().size(),
                coach.getAssignedLessons(),
                LessonUtil.getAverageRating(coach.getAssignedLessons().
                        stream().map(lesson -> LessonUtil.getAverageReviewRating(lesson.getReviews()))
                        .toList()),
                coach.getAssignedLessons().
                        stream().map(lesson -> Map.of(LessonUtil.getAverageReviewRating(lesson.getReviews()), lesson))
                        .toList()
        );
    }

    /**
     * Get a list of reports for all the coaches
     *
     * @return report list
     */
    public List<CoachReport> getReportForAllCoaches() {
        return coachRepository.getAllCoaches().stream().map(this::getReportForCoach).toList();
    }
}
