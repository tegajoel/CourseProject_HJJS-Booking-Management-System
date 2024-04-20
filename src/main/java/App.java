import data.InMemCoachRepository;
import data.InMemLearnersRepository;
import data.InMemLessonRepository;
import domain.usecase.*;
import presentation.controller.HomeScreenViewController;
import presentation.view.HomeScreenView;

public class App {
    public static void main(String[] args) {
        var coachRepo = InMemCoachRepository.getInstance();
        var lessonRepo = InMemLessonRepository.getInstance();
        var learnerRepo = InMemLearnersRepository.getInstance();

        coachRepo.generateSampleCoaches();
        learnerRepo.generateSampleLearners();

        var lessons = new BuildTimetableUseCase().buildAndGetLessons(coachRepo.getAllCoaches());
        lessonRepo.addLessons(lessons);


        new HomeScreenViewController(
                new HomeScreenView(),
                new AttendLessonUseCase(),
                new BookLessonUseCase(),
                new CancelLessonUseCase(),
                new FilterLessonsUseCase(lessonRepo),
                new GenerateCoachReportUseCase(coachRepo),
                new GenerateLearnerReportUseCase(learnerRepo),
                new RegisterNewLearnerUseCase(learnerRepo),
                learnerRepo,
                coachRepo);
    }
}
